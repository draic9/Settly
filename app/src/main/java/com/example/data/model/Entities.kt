package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Chest, Back, Legs, Shoulders, Arms, Core, Full Body
    val equipment: String, // Barbell, Dumbbell, Machine, Cable, Bodyweight
    val primaryMuscles: String, // e.g. "Chest, Triceps"
    val secondaryMuscles: String = "", // e.g. "Anterior Deltoid"
    val defaultRestSeconds: Int = 90,
    val notes: String = "",
    val isCustom: Boolean = false,
    val trainingGoal: String = "Hypertrophy", // Hypertrophy, Strength, Endurance, Relaxed
    val imageUrl: String? = null
)

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // e.g. "Push Day - Hypertrophy"
    val dateMillis: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 45,
    val notes: String = "",
    val totalVolumeLbs: Double = 0.0,
    val totalSets: Int = 0,
    val completed: Boolean = true
)

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("exerciseId")]
)
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val orderIndex: Int = 0
)

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutExerciseId"), Index("exerciseId"), Index("completedAtMillis")]
)
data class ExerciseSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutExerciseId: Long,
    val exerciseId: Long,
    val setNumber: Int,
    val weightLbs: Double,
    val reps: Int,
    val rpe: Double? = null, // Rate of Perceived Exertion (1-10)
    val setType: String = "R", // R (Reps), W (Warmup), D (Drop set), S (Super set)
    val isWarmup: Boolean = false,
    val isPersonalRecord: Boolean = false,
    val completedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "bodyweight_logs")
data class BodyWeightLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weightLbs: Double,
    val dateMillis: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseId: Long,
    val exerciseName: String,
    val recordType: String, // "MaxWeight", "Est1RM", "MaxVolume"
    val value: Double,
    val reps: Int,
    val achievedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_movement_guides")
data class AiMovementGuideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val machineDescription: String,
    val movementDescription: String,
    val exerciseName: String,
    val primaryMuscles: String,
    val secondaryMuscles: String,
    val setupInstructions: String,
    val executionSteps: String,
    val formCues: String,
    val commonMistakes: String,
    val suggestedStartingWeight: String,
    val concentricPhaseCue: String = "Concentric Phase: Explode up with control, squeezing target muscles at peak.",
    val eccentricPhaseCue: String = "Eccentric Phase: Lower under 2-3s tempo, maintaining continuous tension.",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "planned_workouts")
data class PlannedWorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routineName: String,
    val dayOfWeek: Int, // 1 = Mon, 2 = Tue, ..., 7 = Sun
    val dateMillis: Long = System.currentTimeMillis(),
    val focusDescription: String = "Upper Body & Core",
    val exerciseIdsCsv: String = "1,2,7,10",
    val targetNotes: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "custom_routines")
data class CustomRoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val subtitle: String,
    val targetCategory: String, // Push, Pull, Legs, Upper, Lower, Full Body
    val exerciseIdsCsv: String,
    val isPinned: Boolean = false,
    val usageCount: Int = 0,
    val createdAtMillis: Long = System.currentTimeMillis()
)
