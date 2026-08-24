package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ai.AiMovementAnalysis
import com.example.ai.GeminiAiService
import com.example.data.model.AiMovementGuideEntity
import com.example.data.model.BodyWeightLogEntity
import com.example.data.model.CustomRoutineEntity
import com.example.data.model.ExerciseEntity
import com.example.data.model.ExerciseSetEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.PlannedWorkoutEntity
import com.example.data.model.WorkoutExerciseEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.data.repository.BodyWeightSummary
import com.example.data.repository.ExerciseProgressionSuggestion
import com.example.data.repository.GymRepository
import com.example.data.repository.StreakInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveWorkoutExercise(
    val exercise: ExerciseEntity,
    val sets: MutableList<ActiveSetState> = mutableListOf(),
    val previousSessionSets: List<ExerciseSetEntity> = emptyList(),
    val progressionSuggestion: ExerciseProgressionSuggestion? = null
)

data class ActiveSetState(
    val setNumber: Int,
    var weightLbsText: String = "",
    var repsText: String = "",
    var rpeText: String = "",
    var setType: String = "R", // "R" (reps/normal), "W" (warmup), "D" (drop set), "S" (super set)
    var isWarmup: Boolean = false,
    var isCompleted: Boolean = false,
    var isPR: Boolean = false
)

data class ActiveWorkoutState(
    val name: String,
    val startTimeMillis: Long = System.currentTimeMillis(),
    val exercises: MutableList<ActiveWorkoutExercise> = mutableListOf(),
    var notes: String = ""
)

sealed interface AiAnalysisUiState {
    object Idle : AiAnalysisUiState
    object Loading : AiAnalysisUiState
    data class Success(val analysis: AiMovementAnalysis) : AiAnalysisUiState
    data class Error(val message: String) : AiAnalysisUiState
}

class GymViewModel(private val repository: GymRepository) : ViewModel() {

    private val geminiService = GeminiAiService()

    // --- StateFlows from Room ---
    val allExercises: StateFlow<List<ExerciseEntity>> = repository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSessions: StateFlow<List<WorkoutSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBodyWeightLogs: StateFlow<List<BodyWeightLogEntity>> = repository.allBodyWeightLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bodyWeightSummary: StateFlow<BodyWeightSummary> = repository.bodyWeightSummary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BodyWeightSummary(0.0, 0.0, 0.0, false, 0))

    val allPRs: StateFlow<List<PersonalRecordEntity>> = repository.allPersonalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAiGuides: StateFlow<List<AiMovementGuideEntity>> = repository.allAiGuides
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlannedWorkouts: StateFlow<List<PlannedWorkoutEntity>> = repository.allPlannedWorkouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomRoutines: StateFlow<List<CustomRoutineEntity>> = repository.allCustomRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val streakInfo: StateFlow<StreakInfo> = repository.streakInfo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StreakInfo(0, false, 0, "0 Weeks"))

    // --- Active Workout State ---
    private val _activeWorkout = MutableStateFlow<ActiveWorkoutState?>(null)
    val activeWorkout: StateFlow<ActiveWorkoutState?> = _activeWorkout.asStateFlow()

    // --- AI Movement Analysis State ---
    private val _aiAnalysisState = MutableStateFlow<AiAnalysisUiState>(AiAnalysisUiState.Idle)
    val aiAnalysisState: StateFlow<AiAnalysisUiState> = _aiAnalysisState.asStateFlow()

    // --- PR Celebration Pop-up ---
    private val _latestNewPR = MutableStateFlow<PersonalRecordEntity?>(null)
    val latestNewPR: StateFlow<PersonalRecordEntity?> = _latestNewPR.asStateFlow()

    // --- Unit Preference (lbs vs kg) ---
    private val _isKgUnit = MutableStateFlow(false)
    val isKgUnit: StateFlow<Boolean> = _isKgUnit.asStateFlow()

    fun toggleWeightUnit() {
        _isKgUnit.value = !_isKgUnit.value
    }

    fun setWeightUnit(isKg: Boolean) {
        _isKgUnit.value = isKg
    }

    fun formatWeight(weightLbs: Double): String {
        return if (_isKgUnit.value) {
            val kg = weightLbs * 0.45359237
            if (kg % 1.0 == 0.0) "${kg.toInt()} kg" else "${String.format(java.util.Locale.US, "%.1f", kg)} kg"
        } else {
            if (weightLbs % 1.0 == 0.0) "${weightLbs.toInt()} lbs" else "${String.format(java.util.Locale.US, "%.1f", weightLbs)} lbs"
        }
    }

    // --- Progression Suggestions Cache ---
    private val _progressionMap = MutableStateFlow<Map<Long, ExerciseProgressionSuggestion>>(emptyMap())
    val progressionMap: StateFlow<Map<Long, ExerciseProgressionSuggestion>> = _progressionMap.asStateFlow()

    init {
        // Load initial progression suggestions whenever exercises or sessions update
        viewModelScope.launch {
            allExercises.collect { exercises ->
                refreshProgressionMap(exercises)
            }
        }
    }

    private suspend fun refreshProgressionMap(exercises: List<ExerciseEntity>) {
        val map = mutableMapOf<Long, ExerciseProgressionSuggestion>()
        exercises.forEach { ex ->
            map[ex.id] = repository.getProgressionForExercise(ex)
        }
        _progressionMap.value = map
    }

    // --- Workout Actions ---
    fun startNewWorkout(routineName: String = "Custom Workout", initialExercises: List<ExerciseEntity> = emptyList()) {
        viewModelScope.launch {
            val activeExercises = mutableListOf<ActiveWorkoutExercise>()
            for (exercise in initialExercises) {
                val prevSets = repository.getLastSessionSetsForExercise(exercise.id)
                val suggestion = repository.getProgressionForExercise(exercise)
                val sets = mutableListOf<ActiveSetState>()

                val defaultWeight = if (suggestion.suggestedWeight > 0) suggestion.suggestedWeight.toString() else if (prevSets.isNotEmpty()) prevSets.first().weightLbs.toString() else "135"
                val defaultReps = if (prevSets.isNotEmpty()) prevSets.first().reps.toString() else "10"

                sets.add(ActiveSetState(setNumber = 1, weightLbsText = defaultWeight, repsText = defaultReps))
                sets.add(ActiveSetState(setNumber = 2, weightLbsText = defaultWeight, repsText = defaultReps))
                sets.add(ActiveSetState(setNumber = 3, weightLbsText = defaultWeight, repsText = defaultReps))

                activeExercises.add(
                    ActiveWorkoutExercise(
                        exercise = exercise,
                        sets = sets,
                        previousSessionSets = prevSets,
                        progressionSuggestion = suggestion
                    )
                )
            }

            _activeWorkout.value = ActiveWorkoutState(
                name = routineName,
                startTimeMillis = System.currentTimeMillis(),
                exercises = activeExercises
            )
        }
    }

    fun addExerciseToActiveWorkout(exercise: ExerciseEntity) {
        val current = _activeWorkout.value ?: return
        viewModelScope.launch {
            val prevSets = repository.getLastSessionSetsForExercise(exercise.id)
            val suggestion = repository.getProgressionForExercise(exercise)
            val defaultWeight = if (suggestion.suggestedWeight > 0) suggestion.suggestedWeight.toString() else if (prevSets.isNotEmpty()) prevSets.first().weightLbs.toString() else "100"
            val defaultReps = if (prevSets.isNotEmpty()) prevSets.first().reps.toString() else "10"

            val sets = mutableListOf(
                ActiveSetState(1, defaultWeight, defaultReps),
                ActiveSetState(2, defaultWeight, defaultReps),
                ActiveSetState(3, defaultWeight, defaultReps)
            )

            val updatedExercises = current.exercises.toMutableList()
            updatedExercises.add(
                ActiveWorkoutExercise(
                    exercise = exercise,
                    sets = sets,
                    previousSessionSets = prevSets,
                    progressionSuggestion = suggestion
                )
            )
            _activeWorkout.value = current.copy(exercises = updatedExercises)
        }
    }

    fun addSetToExercise(exerciseIndex: Int) {
        val current = _activeWorkout.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val targetExercise = current.exercises[exerciseIndex]
            val lastSet = targetExercise.sets.lastOrNull()
            val newSetNumber = targetExercise.sets.size + 1
            val newSet = ActiveSetState(
                setNumber = newSetNumber,
                weightLbsText = lastSet?.weightLbsText ?: "100",
                repsText = lastSet?.repsText ?: "10"
            )
            val updatedSets = targetExercise.sets.toMutableList().apply { add(newSet) }
            val updatedExercises = current.exercises.toMutableList().apply {
                this[exerciseIndex] = targetExercise.copy(sets = updatedSets)
            }
            _activeWorkout.value = current.copy(exercises = updatedExercises)
        }
    }

    fun removeSetFromExercise(exerciseIndex: Int, setIndex: Int) {
        val current = _activeWorkout.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val targetExercise = current.exercises[exerciseIndex]
            if (setIndex in targetExercise.sets.indices) {
                val updatedSets = targetExercise.sets.toMutableList().apply { removeAt(setIndex) }
                val reindexedSets = updatedSets.mapIndexed { idx, set -> set.copy(setNumber = idx + 1) }.toMutableList()
                val updatedExercises = current.exercises.toMutableList().apply {
                    this[exerciseIndex] = targetExercise.copy(sets = reindexedSets)
                }
                _activeWorkout.value = current.copy(exercises = updatedExercises)
            }
        }
    }

    fun updateSetValues(
        exerciseIndex: Int,
        setIndex: Int,
        weight: String,
        reps: String,
        rpe: String? = null,
        setType: String = "R",
        isWarmup: Boolean = false,
        isCompleted: Boolean = false
    ) {
        val current = _activeWorkout.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val targetExercise = current.exercises[exerciseIndex]
            if (setIndex in targetExercise.sets.indices) {
                val currentSet = targetExercise.sets[setIndex]
                val effectiveWarmup = (setType == "W" || isWarmup)
                val updatedSet = currentSet.copy(
                    weightLbsText = weight,
                    repsText = reps,
                    rpeText = rpe ?: currentSet.rpeText,
                    setType = setType,
                    isWarmup = effectiveWarmup,
                    isCompleted = isCompleted
                )
                val updatedSets = targetExercise.sets.toMutableList().apply { this[setIndex] = updatedSet }
                val updatedExercises = current.exercises.toMutableList().apply {
                    this[exerciseIndex] = targetExercise.copy(sets = updatedSets)
                }
                _activeWorkout.value = current.copy(exercises = updatedExercises)
            }
        }
    }

    fun cycleSetType(exerciseIndex: Int, setIndex: Int) {
        val current = _activeWorkout.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val targetExercise = current.exercises[exerciseIndex]
            if (setIndex in targetExercise.sets.indices) {
                val currentSet = targetExercise.sets[setIndex]
                val nextType = when (currentSet.setType) {
                    "R" -> "W"
                    "W" -> "D"
                    "D" -> "S"
                    else -> "R"
                }
                updateSetValues(
                    exerciseIndex = exerciseIndex,
                    setIndex = setIndex,
                    weight = currentSet.weightLbsText,
                    reps = currentSet.repsText,
                    rpe = currentSet.rpeText,
                    setType = nextType,
                    isWarmup = (nextType == "W"),
                    isCompleted = currentSet.isCompleted
                )
            }
        }
    }

    fun setExplicitSetType(exerciseIndex: Int, setIndex: Int, type: String) {
        val current = _activeWorkout.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val targetExercise = current.exercises[exerciseIndex]
            if (setIndex in targetExercise.sets.indices) {
                val currentSet = targetExercise.sets[setIndex]
                updateSetValues(
                    exerciseIndex = exerciseIndex,
                    setIndex = setIndex,
                    weight = currentSet.weightLbsText,
                    reps = currentSet.repsText,
                    rpe = currentSet.rpeText,
                    setType = type,
                    isWarmup = (type == "W"),
                    isCompleted = currentSet.isCompleted
                )
            }
        }
    }

    fun toggleSetCompleted(exerciseIndex: Int, setIndex: Int) {
        val current = _activeWorkout.value ?: return
        if (exerciseIndex in current.exercises.indices) {
            val targetExercise = current.exercises[exerciseIndex]
            if (setIndex in targetExercise.sets.indices) {
                val set = targetExercise.sets[setIndex]
                val willBeCompleted = !set.isCompleted

                // If marking as completed and valid non-warmup weight/reps, check if it's a new PR
                if (willBeCompleted && set.setType != "W") {
                    val weight = set.weightLbsText.toDoubleOrNull() ?: 0.0
                    val reps = set.repsText.toIntOrNull() ?: 0
                    if (weight > 0 && reps > 0) {
                        val est1RM = if (reps == 1) weight else weight * (1.0 + (reps / 30.0))
                        val currentPRs = allPRs.value.filter { it.exerciseId == targetExercise.exercise.id }
                        val maxWeightPR = currentPRs.find { it.recordType == "MaxWeight" }?.value ?: 0.0
                        val est1RMPR = currentPRs.find { it.recordType == "Est1RM" }?.value ?: 0.0

                        if (weight > maxWeightPR || est1RM > est1RMPR) {
                            val candidatePR = PersonalRecordEntity(
                                exerciseId = targetExercise.exercise.id,
                                exerciseName = targetExercise.exercise.name,
                                recordType = if (weight > maxWeightPR) "MaxWeight" else "Est1RM",
                                value = if (weight > maxWeightPR) weight else Math.round(est1RM * 10.0) / 10.0,
                                reps = reps,
                                achievedAtMillis = System.currentTimeMillis()
                            )
                            _latestNewPR.value = candidatePR
                        }
                    }
                }

                updateSetValues(
                    exerciseIndex = exerciseIndex,
                    setIndex = setIndex,
                    weight = set.weightLbsText,
                    reps = set.repsText,
                    rpe = set.rpeText,
                    setType = set.setType,
                    isWarmup = (set.setType == "W" || set.isWarmup),
                    isCompleted = willBeCompleted
                )
            }
        }
    }

    fun dismissPRCelebration() {
        _latestNewPR.value = null
    }

    fun finishActiveWorkout() {
        val current = _activeWorkout.value ?: return
        viewModelScope.launch {
            // Trim down and only keep completed sets
            val exercisesWithCompletedSets = current.exercises.mapNotNull { ex ->
                val completedSets = ex.sets.filter { it.isCompleted }
                if (completedSets.isNotEmpty()) {
                    ex.copy(sets = completedSets.toMutableList())
                } else null
            }

            if (exercisesWithCompletedSets.isEmpty()) {
                // No completed sets at all - discard workout
                _activeWorkout.value = null
                return@launch
            }

            val durationMinutes = ((System.currentTimeMillis() - current.startTimeMillis) / (60 * 1000)).toInt().coerceAtLeast(1)
            var totalVolume = 0.0
            var totalSetsCount = 0

            exercisesWithCompletedSets.forEach { ex ->
                ex.sets.forEach { s ->
                    val w = s.weightLbsText.toDoubleOrNull() ?: 0.0
                    val r = s.repsText.toIntOrNull() ?: 0
                    totalVolume += (w * r)
                    totalSetsCount += 1
                }
            }

            val session = WorkoutSessionEntity(
                name = current.name,
                dateMillis = current.startTimeMillis,
                durationMinutes = durationMinutes,
                notes = current.notes,
                totalVolumeLbs = totalVolume,
                totalSets = totalSetsCount,
                completed = true
            )
            val sessionId = repository.insertWorkoutSession(session)

            exercisesWithCompletedSets.forEachIndexed { orderIdx, ex ->
                val weId = repository.insertWorkoutExercise(
                    WorkoutExerciseEntity(
                        sessionId = sessionId,
                        exerciseId = ex.exercise.id,
                        exerciseName = ex.exercise.name,
                        orderIndex = orderIdx
                    )
                )

                val setsToSave = ex.sets.mapIndexed { idx, s ->
                    val weight = s.weightLbsText.toDoubleOrNull() ?: 0.0
                    val reps = s.repsText.toIntOrNull() ?: 0
                    val rpe = s.rpeText.toDoubleOrNull()
                    ExerciseSetEntity(
                        workoutExerciseId = weId,
                        exerciseId = ex.exercise.id,
                        setNumber = idx + 1,
                        weightLbs = weight,
                        reps = reps,
                        rpe = rpe,
                        setType = s.setType,
                        isWarmup = (s.setType == "W" || s.isWarmup),
                        completedAtMillis = System.currentTimeMillis()
                    )
                }

                for (setEntity in setsToSave) {
                    val (_, pr) = repository.logSet(setEntity, ex.exercise.name)
                    if (pr != null && _latestNewPR.value == null) {
                        _latestNewPR.value = pr
                    }
                }
            }

            // Increment routine usage count if it matches a custom routine
            val matchingRoutine = allCustomRoutines.value.find { it.name.equals(current.name, ignoreCase = true) }
            if (matchingRoutine != null) {
                incrementRoutineUsage(matchingRoutine.id)
            }

            refreshProgressionMap(allExercises.value)
            _activeWorkout.value = null
        }
    }

    fun discardActiveWorkout() {
        _activeWorkout.value = null
    }

    // --- Schedule & Planned Workouts ---
    fun scheduleWorkout(
        routineName: String,
        dayOfWeek: Int,
        dateMillis: Long,
        exerciseIdsCsv: String,
        focusDescription: String,
        targetNotes: String = ""
    ) {
        viewModelScope.launch {
            repository.insertPlannedWorkout(
                PlannedWorkoutEntity(
                    routineName = routineName,
                    dayOfWeek = dayOfWeek,
                    dateMillis = dateMillis,
                    focusDescription = focusDescription,
                    exerciseIdsCsv = exerciseIdsCsv,
                    targetNotes = targetNotes,
                    isCompleted = false
                )
            )
        }
    }

    fun togglePlannedWorkoutCompleted(planned: PlannedWorkoutEntity) {
        viewModelScope.launch {
            repository.updatePlannedWorkout(planned.copy(isCompleted = !planned.isCompleted))
        }
    }

    fun deletePlannedWorkout(planned: PlannedWorkoutEntity) {
        viewModelScope.launch {
            repository.deletePlannedWorkout(planned)
        }
    }

    // --- Custom Routines Management ---
    fun createCustomRoutine(
        name: String,
        subtitle: String,
        targetCategory: String,
        exerciseIdsCsv: String
    ) {
        viewModelScope.launch {
            repository.insertCustomRoutine(
                CustomRoutineEntity(
                    name = name,
                    subtitle = subtitle,
                    targetCategory = targetCategory,
                    exerciseIdsCsv = exerciseIdsCsv
                )
            )
        }
    }

    fun togglePinCustomRoutine(routine: CustomRoutineEntity) {
        viewModelScope.launch {
            repository.updateCustomRoutine(routine.copy(isPinned = !routine.isPinned))
        }
    }

    fun updateCustomRoutine(routine: CustomRoutineEntity) {
        viewModelScope.launch {
            repository.updateCustomRoutine(routine)
        }
    }

    fun incrementRoutineUsage(routineId: Long) {
        viewModelScope.launch {
            val routine = allCustomRoutines.value.find { it.id == routineId } ?: return@launch
            repository.updateCustomRoutine(routine.copy(usageCount = routine.usageCount + 1))
        }
    }

    fun deleteCustomRoutine(routine: CustomRoutineEntity) {
        viewModelScope.launch {
            repository.deleteCustomRoutine(routine)
        }
    }

    // --- PR Management ---
    fun updatePersonalRecord(pr: PersonalRecordEntity, newValue: Double, newReps: Int) {
        viewModelScope.launch {
            repository.updatePersonalRecord(pr.copy(value = newValue, reps = newReps, achievedAtMillis = System.currentTimeMillis()))
        }
    }

    fun deletePersonalRecord(pr: PersonalRecordEntity) {
        viewModelScope.launch {
            repository.deletePersonalRecord(pr)
        }
    }

    fun addManualPersonalRecord(exerciseId: Long, exerciseName: String, type: String, value: Double, reps: Int) {
        viewModelScope.launch {
            repository.insertPersonalRecord(
                PersonalRecordEntity(
                    exerciseId = exerciseId,
                    exerciseName = exerciseName,
                    recordType = type,
                    value = value,
                    reps = reps,
                    achievedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    // --- Apply Overload Target to Planned Workouts ---
    fun applyOverloadToPlannedWorkouts(exerciseId: Long, newWeight: Double) {
        viewModelScope.launch {
            val plannedList = allPlannedWorkouts.value
            val exercise = allExercises.value.find { it.id == exerciseId } ?: return@launch
            for (planned in plannedList) {
                val ids = planned.exerciseIdsCsv.split(",").mapNotNull { it.trim().toLongOrNull() }
                if (ids.contains(exerciseId)) {
                    val updatedNotes = if (planned.targetNotes.isBlank()) {
                        "${exercise.name} Overload: ${newWeight.toInt()} lbs"
                    } else {
                        "${planned.targetNotes} | ${exercise.name}: ${newWeight.toInt()} lbs"
                    }
                    repository.updatePlannedWorkout(planned.copy(targetNotes = updatedNotes))
                }
            }
        }
    }

    fun applyOverloadTargetToRoutines(exerciseId: Long, newWeight: Double) {
        applyOverloadToPlannedWorkouts(exerciseId, newWeight)
    }

    fun updateTrainingGoal(exerciseId: Long, newGoal: String) {
        updateExerciseTrainingGoal(exerciseId, newGoal)
    }

    fun updateExerciseTrainingGoal(exerciseId: Long, newGoal: String) {
        viewModelScope.launch {
            val ex = repository.getExerciseById(exerciseId) ?: return@launch
            val updated = ex.copy(trainingGoal = newGoal)
            repository.updateExercise(updated)
            refreshProgressionMap(allExercises.value)
        }
    }

    fun addNewCustomExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.insertExercise(exercise)
        }
    }

    fun addNewExercise(exercise: ExerciseEntity) = addNewCustomExercise(exercise)

    fun saveAiMovementGuide(guide: AiMovementGuideEntity) {
        viewModelScope.launch {
            repository.insertAiGuide(guide)
        }
    }

    fun analyzeMovementWithGemini(name: String, category: String, equipment: String, userContext: String) {
        analyzeMovementWithAI("$name ($equipment, $category)", userContext)
    }

    // --- Body Weight Actions ---
    fun logBodyWeight(weightLbs: Double, note: String = "") {
        viewModelScope.launch {
            repository.insertBodyWeightLog(
                BodyWeightLogEntity(
                    weightLbs = weightLbs,
                    dateMillis = System.currentTimeMillis(),
                    note = note
                )
            )
        }
    }

    fun deleteBodyWeightLog(log: BodyWeightLogEntity) {
        viewModelScope.launch {
            repository.deleteBodyWeightLog(log)
        }
    }

    // --- AI Machine & Movement Analysis ---
    fun generateAiGuideForExercise(exerciseName: String, category: String) {
        viewModelScope.launch {
            try {
                val analysis = geminiService.analyzeMovementAndMachine(
                    machineDescription = "$exerciseName in $category category",
                    movementDescription = "Full setup and execution cues for $exerciseName"
                )
                saveAiGuide(analysis, "$exerciseName Machine/Equipment", "$exerciseName Movement")
            } catch (e: Exception) {
                // Handled gracefully
            }
        }
    }

    fun analyzeMovementWithAI(machineDesc: String, movementDesc: String) {
        _aiAnalysisState.value = AiAnalysisUiState.Loading
        viewModelScope.launch {
            try {
                val analysis = geminiService.analyzeMovementAndMachine(machineDesc, movementDesc)
                _aiAnalysisState.value = AiAnalysisUiState.Success(analysis)
            } catch (e: Exception) {
                _aiAnalysisState.value = AiAnalysisUiState.Error(e.message ?: "Failed to analyze movement.")
            }
        }
    }

    fun saveAiGuide(analysis: AiMovementAnalysis, machineDesc: String, movementDesc: String) {
        viewModelScope.launch {
            repository.insertAiGuide(
                AiMovementGuideEntity(
                    machineDescription = machineDesc,
                    movementDescription = movementDesc,
                    exerciseName = analysis.exerciseName,
                    primaryMuscles = analysis.primaryMuscles.joinToString(", "),
                    secondaryMuscles = analysis.secondaryMuscles.joinToString(", "),
                    setupInstructions = analysis.machineSetup,
                    executionSteps = analysis.executionSteps.joinToString("\n"),
                    formCues = analysis.formCues.joinToString("\n"),
                    commonMistakes = analysis.commonMistakes.joinToString("\n"),
                    suggestedStartingWeight = analysis.suggestedStartingWeight,
                    concentricPhaseCue = "Concentric: Drive weight powerfully through target muscle contraction.",
                    eccentricPhaseCue = "Eccentric: Resist load smoothly through full range of motion.",
                    createdAtMillis = System.currentTimeMillis()
                )
            )

            // Also insert into exercises list as custom exercise if not exists
            val customExercise = ExerciseEntity(
                name = analysis.exerciseName,
                category = when {
                    analysis.primaryMuscles.any { it.contains("Chest", true) } -> "Chest"
                    analysis.primaryMuscles.any { it.contains("Lat", true) || it.contains("Back", true) || it.contains("Rhomboid", true) } -> "Back"
                    analysis.primaryMuscles.any { it.contains("Quad", true) || it.contains("Hamstring", true) || it.contains("Glute", true) } -> "Legs"
                    analysis.primaryMuscles.any { it.contains("Delt", true) || it.contains("Shoulder", true) } -> "Shoulders"
                    analysis.primaryMuscles.any { it.contains("Bicep", true) || it.contains("Tricep", true) } -> "Arms"
                    else -> "Full Body"
                },
                equipment = "Machine",
                primaryMuscles = analysis.primaryMuscles.joinToString(", "),
                secondaryMuscles = analysis.secondaryMuscles.joinToString(", "),
                defaultRestSeconds = 90,
                notes = analysis.machineSetup,
                isCustom = true,
                trainingGoal = "Hypertrophy"
            )
            repository.insertExercise(customExercise)
        }
    }

    fun resetAiState() {
        _aiAnalysisState.value = AiAnalysisUiState.Idle
    }

    class Factory(private val repository: GymRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GymViewModel::class.java)) {
                return GymViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
