package com.example.data.repository

import com.example.data.dao.GymDao
import com.example.data.model.AiMovementGuideEntity
import com.example.data.model.BodyWeightLogEntity
import com.example.data.model.CustomRoutineEntity
import com.example.data.model.ExerciseEntity
import com.example.data.model.ExerciseSetEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.PlannedWorkoutEntity
import com.example.data.model.WorkoutExerciseEntity
import com.example.data.model.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

data class OverloadTrajectoryHint(
    val title: String,
    val description: String,
    val tag: String, // "Overload", "Hypertrophy", "Plateau Breaker", "Deload/Form"
    val actionSuggestion: String
)

data class ExerciseProgressionSuggestion(
    val exerciseId: Long,
    val exerciseName: String,
    val trainingGoal: String, // Hypertrophy (8-12), Strength (4-8), Endurance (15-20)
    val lastSessionDate: Long?,
    val lastSets: List<ExerciseSetEntity>,
    val lastTopWeight: Double,
    val lastTopReps: Int,
    val suggestedWeight: Double,
    val suggestedReps: String,
    val weightDeltaLbs: Double,
    val isReadyToOverload: Boolean,
    val isStagnant: Boolean,
    val executionCount: Int,
    val progressionNote: String,
    val hints: List<OverloadTrajectoryHint>,
    val estimated1RM: Double
)

data class BodyWeightSummary(
    val startingWeight: Double,
    val currentWeight: Double,
    val totalLostLbs: Double,
    val isLosing: Boolean,
    val logCount: Int
)

data class StreakInfo(
    val activeWeeksCount: Int,
    val isCurrentWeekActive: Boolean,
    val workoutsThisWeek: Int,
    val streakLabel: String // e.g. "4 Weeks", "2 Months"
)

class GymRepository(private val dao: GymDao) {

    // --- Exercises ---
    val allExercises: Flow<List<ExerciseEntity>> = dao.getAllExercises()

    suspend fun getExerciseById(id: Long) = dao.getExerciseById(id)

    suspend fun insertExercise(exercise: ExerciseEntity): Long = dao.insertExercise(exercise)

    suspend fun updateExercise(exercise: ExerciseEntity) = dao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: ExerciseEntity) = dao.deleteExercise(exercise)

    // --- Workouts ---
    val allSessions: Flow<List<WorkoutSessionEntity>> = dao.getAllWorkoutSessions()

    fun getSessionsInDateRange(startMillis: Long, endMillis: Long): Flow<List<WorkoutSessionEntity>> =
        dao.getSessionsInDateRange(startMillis, endMillis)

    suspend fun getSessionById(id: Long): WorkoutSessionEntity? = dao.getWorkoutSessionById(id)

    suspend fun insertWorkoutSession(session: WorkoutSessionEntity): Long = dao.insertWorkoutSession(session)

    suspend fun updateWorkoutSession(session: WorkoutSessionEntity) = dao.updateWorkoutSession(session)

    suspend fun deleteWorkoutSession(session: WorkoutSessionEntity) = dao.deleteWorkoutSession(session)

    // --- Planned Workouts (Schedule) ---
    val allPlannedWorkouts: Flow<List<PlannedWorkoutEntity>> = dao.getAllPlannedWorkouts()

    fun getPlannedWorkoutsForDateRange(startMillis: Long, endMillis: Long): Flow<List<PlannedWorkoutEntity>> =
        dao.getPlannedWorkoutsForDateRange(startMillis, endMillis)

    suspend fun insertPlannedWorkout(planned: PlannedWorkoutEntity): Long = dao.insertPlannedWorkout(planned)

    suspend fun updatePlannedWorkout(planned: PlannedWorkoutEntity) = dao.updatePlannedWorkout(planned)

    suspend fun deletePlannedWorkout(planned: PlannedWorkoutEntity) = dao.deletePlannedWorkout(planned)

    // --- Custom Routines ---
    val allCustomRoutines: Flow<List<CustomRoutineEntity>> = dao.getAllCustomRoutines()

    suspend fun insertCustomRoutine(routine: CustomRoutineEntity): Long = dao.insertCustomRoutine(routine)

    suspend fun deleteCustomRoutine(routine: CustomRoutineEntity) = dao.deleteCustomRoutine(routine)

    // --- Workout Exercises & Sets ---
    fun getWorkoutExercisesForSession(sessionId: Long): Flow<List<WorkoutExerciseEntity>> =
        dao.getWorkoutExercisesForSession(sessionId)

    suspend fun getWorkoutExercisesListForSession(sessionId: Long) =
        dao.getWorkoutExercisesListForSession(sessionId)

    suspend fun insertWorkoutExercise(exercise: WorkoutExerciseEntity): Long =
        dao.insertWorkoutExercise(exercise)

    suspend fun deleteWorkoutExercise(exercise: WorkoutExerciseEntity) =
        dao.deleteWorkoutExercise(exercise)

    fun getSetsForWorkoutExercise(weId: Long): Flow<List<ExerciseSetEntity>> =
        dao.getSetsForWorkoutExercise(weId)

    suspend fun getSetsListForWorkoutExercise(weId: Long) =
        dao.getSetsListForWorkoutExercise(weId)

    fun getAllSetsForExercise(exerciseId: Long): Flow<List<ExerciseSetEntity>> =
        dao.getAllSetsForExercise(exerciseId)

    suspend fun getLastSessionSetsForExercise(exerciseId: Long): List<ExerciseSetEntity> =
        dao.getLastSessionSetsForExercise(exerciseId)

    suspend fun logSet(
        set: ExerciseSetEntity,
        exerciseName: String
    ): Pair<Long, PersonalRecordEntity?> {
        val setId = dao.insertExerciseSet(set)
        var newPR: PersonalRecordEntity? = null

        if (!set.isWarmup && set.weightLbs > 0 && set.reps > 0) {
            // Calculate 1RM (Epley formula: weight * (1 + reps / 30))
            val est1RM = if (set.reps == 1) set.weightLbs else set.weightLbs * (1.0 + (set.reps / 30.0))

            val existingMaxWeight = dao.getPR(set.exerciseId, "MaxWeight")
            if (existingMaxWeight == null || set.weightLbs > existingMaxWeight.value) {
                val pr = PersonalRecordEntity(
                    exerciseId = set.exerciseId,
                    exerciseName = exerciseName,
                    recordType = "MaxWeight",
                    value = set.weightLbs,
                    reps = set.reps,
                    achievedAtMillis = System.currentTimeMillis()
                )
                dao.insertPersonalRecord(pr)
                newPR = pr
            }

            val existingEst1RM = dao.getPR(set.exerciseId, "Est1RM")
            if (existingEst1RM == null || est1RM > existingEst1RM.value) {
                val pr = PersonalRecordEntity(
                    exerciseId = set.exerciseId,
                    exerciseName = exerciseName,
                    recordType = "Est1RM",
                    value = Math.round(est1RM * 10.0) / 10.0,
                    reps = set.reps,
                    achievedAtMillis = System.currentTimeMillis()
                )
                dao.insertPersonalRecord(pr)
                if (newPR == null) newPR = pr
            }
        }

        return Pair(setId, newPR)
    }

    suspend fun updateSet(set: ExerciseSetEntity) = dao.updateExerciseSet(set)

    suspend fun deleteSet(set: ExerciseSetEntity) = dao.deleteExerciseSet(set)

    // --- Progressive Overload Algorithm ---
    suspend fun getProgressionForExercise(exercise: ExerciseEntity): ExerciseProgressionSuggestion {
        val lastSets = dao.getLastSessionSetsForExercise(exercise.id)
        val recentWorkingSets = dao.getRecentWorkingSetsForExercise(exercise.id, 15)
        val executionCount = recentWorkingSets.size

        if (lastSets.isEmpty()) {
            return ExerciseProgressionSuggestion(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                trainingGoal = exercise.trainingGoal,
                lastSessionDate = null,
                lastSets = emptyList(),
                lastTopWeight = 0.0,
                lastTopReps = 0,
                suggestedWeight = 0.0,
                suggestedReps = when (exercise.trainingGoal) {
                    "Strength" -> "4 - 6 reps"
                    "Endurance" -> "15 - 20 reps"
                    else -> "8 - 12 reps"
                },
                weightDeltaLbs = 0.0,
                isReadyToOverload = false,
                isStagnant = false,
                executionCount = 0,
                progressionNote = "No logged history yet. Start with a moderate warmup set to establish your baseline.",
                hints = listOf(
                    OverloadTrajectoryHint(
                        title = "Establish Baseline",
                        description = "Find a working weight where the final 2 reps feel challenging with clean tempo.",
                        tag = "Setup",
                        actionSuggestion = "Log 3 sets of ${if (exercise.trainingGoal == "Strength") "5" else "10"} reps."
                    )
                ),
                estimated1RM = 0.0
            )
        }

        val workingSets = lastSets.filter { !it.isWarmup }
        val topSet = workingSets.maxByOrNull { it.weightLbs } ?: lastSets.first()
        val topWeight = topSet.weightLbs
        val topReps = topSet.reps
        val est1RM = if (topReps == 1) topWeight else topWeight * (1.0 + (topReps / 30.0))

        // Analyze history to detect stagnation
        val weightHistory = recentWorkingSets.take(6).map { it.weightLbs }
        val isStagnant = weightHistory.size >= 4 && weightHistory.all { it == topWeight } && topReps < 10

        // Increment size
        val defaultInc = when {
            exercise.equipment.equals("Dumbbell", true) || exercise.equipment.equals("Cable", true) -> 2.5
            exercise.trainingGoal.equals("Strength", true) -> 5.0
            else -> 5.0
        }

        var suggestedWeight = topWeight
        var suggestedReps = "8 - 12 reps"
        var isReadyToOverload = false
        var progressionNote = ""
        val hints = mutableListOf<OverloadTrajectoryHint>()

        when (exercise.trainingGoal) {
            "Strength" -> { // Target 4 - 8 reps
                if (topReps >= 8) {
                    isReadyToOverload = true
                    val inc = if (exercise.category == "Legs") 10.0 else 5.0
                    suggestedWeight = topWeight + inc
                    suggestedReps = "4 - 6 reps"
                    progressionNote = "Strength target reached (${topReps} reps). Increase weight by +${inc.toInt()} lbs and reset to 4-6 reps."
                    hints.add(
                        OverloadTrajectoryHint(
                            title = "Strength Overload Triggered",
                            description = "You completed ${topReps} reps at ${topWeight.toInt()} lbs. Moving to ${suggestedWeight.toInt()} lbs builds peak neuromuscular recruitment.",
                            tag = "Strength",
                            actionSuggestion = "Take 3 minutes rest between sets for maximum ATP replenishment."
                        )
                    )
                } else {
                    suggestedWeight = topWeight
                    suggestedReps = "${topReps + 1} - 8 reps"
                    progressionNote = "Keep ${topWeight.toInt()} lbs and push for ${topReps + 1} reps before bumping load."
                    hints.add(
                        OverloadTrajectoryHint(
                            title = "Rep Consolidation",
                            description = "Build volume at current load. Hitting 8 clean reps unlocks the next 5 lb jump.",
                            tag = "Volume",
                            actionSuggestion = "Focus on explosive concentric drive and braced core."
                        )
                    )
                }
            }
            "Endurance" -> { // Target 15 - 20 reps
                if (topReps >= 20) {
                    isReadyToOverload = true
                    suggestedWeight = topWeight + defaultInc
                    suggestedReps = "15 - 16 reps"
                    progressionNote = "Endurance ceiling hit (${topReps} reps). Increase weight to ${suggestedWeight.toInt()} lbs and reset to 15 reps."
                    hints.add(
                        OverloadTrajectoryHint(
                            title = "Endurance Progression",
                            description = "Max metabolic threshold achieved. Up the resistance to maintain high muscle fiber recruitment.",
                            tag = "Endurance",
                            actionSuggestion = "Maintain 45-60s rest intervals."
                        )
                    )
                } else {
                    suggestedWeight = topWeight
                    suggestedReps = "${topReps + 1} - 20 reps"
                    progressionNote = "Stay at ${topWeight.toInt()} lbs and aim for ${topReps + 2} reps to build endurance."
                    hints.add(
                        OverloadTrajectoryHint(
                            title = "Lactate Threshold",
                            description = "Push closer to 20 reps to trigger mitochondrial and capillary adaptations.",
                            tag = "Hypertrophy",
                            actionSuggestion = "Keep steady cadence with continuous tension."
                        )
                    )
                }
            }
            else -> { // Hypertrophy (8 - 12 reps)
                if (topReps >= 12) {
                    isReadyToOverload = true
                    suggestedWeight = topWeight + defaultInc
                    suggestedReps = "8 - 10 reps"
                    progressionNote = "Hypertrophy threshold reached (${topReps} reps). Add +${defaultInc.toInt()} lbs and reset to 8-10 reps."
                    hints.add(
                        OverloadTrajectoryHint(
                            title = "Hypertrophy Progression",
                            description = "Hitting 12 reps indicates current mechanical tension is sub-maximal. Bumping to ${suggestedWeight.toInt()} lbs stimulates new myofibrillar growth.",
                            tag = "Overload",
                            actionSuggestion = "Control the 3-second eccentric lower on the new weight."
                        )
                    )
                } else if (topReps >= 10) {
                    suggestedWeight = topWeight
                    suggestedReps = "11 - 12 reps"
                    progressionNote = "Almost at overload threshold. Push for 12 clean reps next session."
                    hints.add(
                        OverloadTrajectoryHint(
                            title = "Close to Overload",
                            description = "Just 1-2 more reps needed before advancing to ${(topWeight + defaultInc).toInt()} lbs.",
                            tag = "Trajectory",
                            actionSuggestion = "Ensure adequate pre-workout carbohydrates and 2-min rest."
                        )
                    )
                } else {
                    suggestedWeight = topWeight
                    suggestedReps = "${topReps + 1} - 12 reps"
                    progressionNote = "Solid foundation at ${topWeight.toInt()} lbs. Strive for ${topReps + 1} reps next session."
                    hints.add(
                        OverloadTrajectoryHint(
                            title = "Progressive Rep Addition",
                            description = "Adding reps is the most reliable way to progress before increasing external load.",
                            tag = "Progression",
                            actionSuggestion = "Keep shoulder blades locked and maintain consistent range of motion."
                        )
                    )
                }
            }
        }

        if (isStagnant) {
            hints.add(
                OverloadTrajectoryHint(
                    title = "Plateau Breaker",
                    description = "Weight has been steady at ${topWeight.toInt()} lbs. Consider a 10% back-off set or adding an extra rest day before training.",
                    tag = "Plateau Breaker",
                    actionSuggestion = "Perform a drop set on your last set to fatigue stubborn motor units."
                )
            )
        }

        return ExerciseProgressionSuggestion(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            trainingGoal = exercise.trainingGoal,
            lastSessionDate = topSet.completedAtMillis,
            lastSets = lastSets,
            lastTopWeight = topWeight,
            lastTopReps = topReps,
            suggestedWeight = suggestedWeight,
            suggestedReps = suggestedReps,
            weightDeltaLbs = suggestedWeight - topWeight,
            isReadyToOverload = isReadyToOverload,
            isStagnant = isStagnant,
            executionCount = executionCount,
            progressionNote = progressionNote,
            hints = hints,
            estimated1RM = Math.round(est1RM * 10.0) / 10.0
        )
    }

    // --- Streak & Consistency Calculation ---
    val streakInfo: Flow<StreakInfo> = dao.getAllWorkoutSessions().map { sessions ->
        calculateWeeklyStreak(sessions)
    }

    private fun calculateWeeklyStreak(sessions: List<WorkoutSessionEntity>): StreakInfo {
        if (sessions.isEmpty()) {
            return StreakInfo(0, false, 0, "0 Weeks")
        }

        val completedSessions = sessions.filter { it.completed }
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)

        val sessionsByWeek = mutableMapOf<Pair<Int, Int>, Int>()
        for (session in completedSessions) {
            val c = Calendar.getInstance().apply { timeInMillis = session.dateMillis }
            val y = c.get(Calendar.YEAR)
            val w = c.get(Calendar.WEEK_OF_YEAR)
            val key = Pair(y, w)
            sessionsByWeek[key] = (sessionsByWeek[key] ?: 0) + 1
        }

        val currentWeekWorkouts = sessionsByWeek[Pair(currentYear, currentWeek)] ?: 0
        val isCurrentWeekActive = currentWeekWorkouts > 0

        // Calculate consecutive active weeks (at least 1 workout per week to sustain streak)
        var streakWeeks = 0
        var checkCal = Calendar.getInstance()

        // Check current week
        if (isCurrentWeekActive) {
            streakWeeks++
            checkCal.add(Calendar.WEEK_OF_YEAR, -1)
        } else {
            // Give grace period for current ongoing week: check if previous week was active
            checkCal.add(Calendar.WEEK_OF_YEAR, -1)
        }

        for (i in 0..52) {
            val y = checkCal.get(Calendar.YEAR)
            val w = checkCal.get(Calendar.WEEK_OF_YEAR)
            val count = sessionsByWeek[Pair(y, w)] ?: 0
            if (count > 0) {
                streakWeeks++
                checkCal.add(Calendar.WEEK_OF_YEAR, -1)
            } else {
                break
            }
        }

        val label = when {
            streakWeeks >= 8 -> "${streakWeeks / 4} Months"
            streakWeeks == 1 -> "1 Week"
            else -> "$streakWeeks Weeks"
        }

        return StreakInfo(
            activeWeeksCount = streakWeeks,
            isCurrentWeekActive = isCurrentWeekActive,
            workoutsThisWeek = currentWeekWorkouts,
            streakLabel = label
        )
    }

    // --- Body Weight Logs ---
    val allBodyWeightLogs: Flow<List<BodyWeightLogEntity>> = dao.getAllBodyWeightLogs()

    val bodyWeightSummary: Flow<BodyWeightSummary> = allBodyWeightLogs.map { logs ->
        if (logs.isEmpty()) {
            BodyWeightSummary(0.0, 0.0, 0.0, false, 0)
        } else {
            val start = logs.first().weightLbs
            val current = logs.last().weightLbs
            val diff = start - current
            BodyWeightSummary(
                startingWeight = Math.round(start * 10.0) / 10.0,
                currentWeight = Math.round(current * 10.0) / 10.0,
                totalLostLbs = Math.round(diff * 10.0) / 10.0,
                isLosing = diff >= 0,
                logCount = logs.size
            )
        }
    }

    suspend fun insertBodyWeightLog(log: BodyWeightLogEntity): Long = dao.insertBodyWeightLog(log)

    suspend fun deleteBodyWeightLog(log: BodyWeightLogEntity) = dao.deleteBodyWeightLog(log)

    // --- Personal Records ---
    val allPersonalRecords: Flow<List<PersonalRecordEntity>> = dao.getAllPersonalRecords()

    fun getPRsForExercise(exerciseId: Long): Flow<List<PersonalRecordEntity>> =
        dao.getPRsForExercise(exerciseId)

    suspend fun insertPersonalRecord(pr: PersonalRecordEntity): Long = dao.insertPersonalRecord(pr)

    suspend fun updatePersonalRecord(pr: PersonalRecordEntity) = dao.updatePersonalRecord(pr)

    suspend fun deletePersonalRecord(pr: PersonalRecordEntity) = dao.deletePersonalRecord(pr)

    suspend fun updateCustomRoutine(routine: CustomRoutineEntity) = dao.updateCustomRoutine(routine)

    // --- AI Guides ---
    val allAiGuides: Flow<List<AiMovementGuideEntity>> = dao.getAllAiGuides()

    suspend fun insertAiGuide(guide: AiMovementGuideEntity): Long = dao.insertAiGuide(guide)

    suspend fun deleteAiGuide(guide: AiMovementGuideEntity) = dao.deleteAiGuide(guide)
}
