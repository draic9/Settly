package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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

@Dao
interface GymDao {

    // --- Exercises ---
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Long): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY name ASC")
    fun getExercisesByCategory(category: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    // --- Workout Sessions ---
    @Query("SELECT * FROM workout_sessions ORDER BY dateMillis DESC")
    fun getAllWorkoutSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getWorkoutSessionById(id: Long): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis ORDER BY dateMillis ASC")
    fun getSessionsInDateRange(startMillis: Long, endMillis: Long): Flow<List<WorkoutSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateWorkoutSession(session: WorkoutSessionEntity)

    @Delete
    suspend fun deleteWorkoutSession(session: WorkoutSessionEntity)

    // --- Workout Exercises ---
    @Query("SELECT * FROM workout_exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    fun getWorkoutExercisesForSession(sessionId: Long): Flow<List<WorkoutExerciseEntity>>

    @Query("SELECT * FROM workout_exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    suspend fun getWorkoutExercisesListForSession(sessionId: Long): List<WorkoutExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(exercise: WorkoutExerciseEntity): Long

    @Delete
    suspend fun deleteWorkoutExercise(exercise: WorkoutExerciseEntity)

    // --- Exercise Sets ---
    @Query("SELECT * FROM exercise_sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    fun getSetsForWorkoutExercise(workoutExerciseId: Long): Flow<List<ExerciseSetEntity>>

    @Query("SELECT * FROM exercise_sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    suspend fun getSetsListForWorkoutExercise(workoutExerciseId: Long): List<ExerciseSetEntity>

    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY completedAtMillis DESC")
    fun getAllSetsForExercise(exerciseId: Long): Flow<List<ExerciseSetEntity>>

    @Query("""
        SELECT * FROM exercise_sets 
        WHERE exerciseId = :exerciseId AND workoutExerciseId IN (
            SELECT id FROM workout_exercises WHERE sessionId = (
                SELECT we.sessionId FROM workout_exercises we 
                INNER JOIN workout_sessions ws ON we.sessionId = ws.id 
                WHERE we.exerciseId = :exerciseId AND ws.completed = 1
                ORDER BY ws.dateMillis DESC LIMIT 1
            )
        )
        ORDER BY setNumber ASC
    """)
    suspend fun getLastSessionSetsForExercise(exerciseId: Long): List<ExerciseSetEntity>

    @Query("""
        SELECT * FROM exercise_sets 
        WHERE exerciseId = :exerciseId AND isWarmup = 0
        ORDER BY completedAtMillis DESC LIMIT :limit
    """)
    suspend fun getRecentWorkingSetsForExercise(exerciseId: Long, limit: Int = 10): List<ExerciseSetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseSet(set: ExerciseSetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseSets(sets: List<ExerciseSetEntity>)

    @Update
    suspend fun updateExerciseSet(set: ExerciseSetEntity)

    @Delete
    suspend fun deleteExerciseSet(set: ExerciseSetEntity)

    // --- Body Weight Logs ---
    @Query("SELECT * FROM bodyweight_logs ORDER BY dateMillis ASC")
    fun getAllBodyWeightLogs(): Flow<List<BodyWeightLogEntity>>

    @Query("SELECT * FROM bodyweight_logs ORDER BY dateMillis DESC LIMIT 1")
    fun getLatestBodyWeightLog(): Flow<BodyWeightLogEntity?>

    @Query("SELECT * FROM bodyweight_logs ORDER BY dateMillis ASC LIMIT 1")
    fun getEarliestBodyWeightLog(): Flow<BodyWeightLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyWeightLog(log: BodyWeightLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyWeightLogs(logs: List<BodyWeightLogEntity>)

    @Delete
    suspend fun deleteBodyWeightLog(log: BodyWeightLogEntity)

    // --- Personal Records ---
    @Query("SELECT * FROM personal_records ORDER BY achievedAtMillis DESC")
    fun getAllPersonalRecords(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId ORDER BY value DESC")
    fun getPRsForExercise(exerciseId: Long): Flow<List<PersonalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalRecord(pr: PersonalRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalRecords(prs: List<PersonalRecordEntity>)

    @Update
    suspend fun updatePersonalRecord(pr: PersonalRecordEntity)

    @Delete
    suspend fun deletePersonalRecord(pr: PersonalRecordEntity)

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId AND recordType = :type LIMIT 1")
    suspend fun getPR(exerciseId: Long, type: String): PersonalRecordEntity?

    // --- AI Movement Guides ---
    @Query("SELECT * FROM ai_movement_guides ORDER BY createdAtMillis DESC")
    fun getAllAiGuides(): Flow<List<AiMovementGuideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiGuide(guide: AiMovementGuideEntity): Long

    @Delete
    suspend fun deleteAiGuide(guide: AiMovementGuideEntity)

    // --- Planned Workouts (Schedule) ---
    @Query("SELECT * FROM planned_workouts ORDER BY dateMillis ASC")
    fun getAllPlannedWorkouts(): Flow<List<PlannedWorkoutEntity>>

    @Query("SELECT * FROM planned_workouts WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis ORDER BY dateMillis ASC")
    fun getPlannedWorkoutsForDateRange(startMillis: Long, endMillis: Long): Flow<List<PlannedWorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannedWorkout(planned: PlannedWorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannedWorkouts(plannedList: List<PlannedWorkoutEntity>)

    @Update
    suspend fun updatePlannedWorkout(planned: PlannedWorkoutEntity)

    @Delete
    suspend fun deletePlannedWorkout(planned: PlannedWorkoutEntity)

    // --- Custom Routines ---
    @Query("SELECT * FROM custom_routines ORDER BY isPinned DESC, usageCount DESC, createdAtMillis DESC")
    fun getAllCustomRoutines(): Flow<List<CustomRoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomRoutine(routine: CustomRoutineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomRoutines(routines: List<CustomRoutineEntity>)

    @Update
    suspend fun updateCustomRoutine(routine: CustomRoutineEntity)

    @Delete
    suspend fun deleteCustomRoutine(routine: CustomRoutineEntity)
}
