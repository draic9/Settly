package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        WorkoutExerciseEntity::class,
        ExerciseSetEntity::class,
        BodyWeightLogEntity::class,
        PersonalRecordEntity::class,
        AiMovementGuideEntity::class,
        PlannedWorkoutEntity::class,
        CustomRoutineEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class GymDatabase : RoomDatabase() {

    abstract fun gymDao(): GymDao

    companion object {
        @Volatile
        private var INSTANCE: GymDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GymDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymDatabase::class.java,
                    "settle_gym_database"
                )
                    .addCallback(GymDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class GymDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.gymDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: GymDao) {
            val initialExercises = listOf(
                ExerciseEntity(1, "Barbell Bench Press", "Chest", "Barbell", "Chest, Triceps", "Anterior Deltoid", 120, "Keep shoulder blades retracted and feet planted.", false, "Hypertrophy"),
                ExerciseEntity(2, "Incline Dumbbell Press", "Chest", "Dumbbell", "Upper Chest, Anterior Deltoid", "Triceps", 90, "30-degree incline for clavicular head.", false, "Hypertrophy"),
                ExerciseEntity(3, "Barbell Back Squat", "Legs", "Barbell", "Quadriceps, Glutes", "Hamstrings, Core, Spinal Erectors", 180, "Break at hips and knees simultaneously, depth below parallel.", false, "Strength"),
                ExerciseEntity(4, "Romanian Deadlift", "Legs", "Barbell", "Hamstrings, Glutes", "Lower Back, Forearms", 120, "Hinge at the hips, slight bend in knees, keep bar close to shins.", false, "Hypertrophy"),
                ExerciseEntity(5, "Lat Pulldown", "Back", "Cable", "Latissimus Dorsi, Biceps", "Rear Deltoid, Rhomboids", 90, "Pull to upper chest, drive elbows down and back.", false, "Hypertrophy"),
                ExerciseEntity(6, "Seated Cable Row", "Back", "Cable", "Rhomboids, Middle Traps, Lats", "Biceps, Rear Deltoids", 90, "Keep spine neutral, pull to lower rib cage with squeeze.", false, "Hypertrophy"),
                ExerciseEntity(7, "Overhead Shoulder Press", "Shoulders", "Barbell", "Anterior Deltoid, Triceps", "Lateral Deltoid, Upper Chest", 120, "Brace core, press in a straight vertical bar path.", false, "Strength"),
                ExerciseEntity(8, "Dumbbell Lateral Raise", "Shoulders", "Dumbbell", "Lateral Deltoid", "Trapezius", 60, "Lead with elbows, slight forward lean, controlled eccentric.", false, "Hypertrophy"),
                ExerciseEntity(9, "Barbell Bicep Curl", "Arms", "Barbell", "Biceps Brachii", "Brachialis, Forearms", 60, "Elbows tucked by sides, full range of motion.", false, "Hypertrophy"),
                ExerciseEntity(10, "Tricep Rope Pushdown", "Arms", "Cable", "Triceps (Lateral & Medial Head)", "Anconeus", 60, "Spread rope at bottom peak contraction.", false, "Hypertrophy"),
                ExerciseEntity(11, "Leg Press 45°", "Legs", "Machine", "Quadriceps, Glutes", "Adductors, Calves", 120, "Avoid locking knees at lockout, control weight on descent.", false, "Hypertrophy"),
                ExerciseEntity(12, "Leg Extension", "Legs", "Machine", "Quadriceps (Rectus Femoris)", "", 60, "Pause 1 second at top contraction.", false, "Hypertrophy"),
                ExerciseEntity(13, "Lying Hamstring Curl", "Legs", "Machine", "Hamstrings", "Gastrocnemius", 60, "Keep hips pressed down into pad.", false, "Hypertrophy"),
                ExerciseEntity(14, "Pec Deck Flye", "Chest", "Machine", "Pectoralis Major", "Anterior Deltoid", 75, "Slight elbow bend, focus on deep stretch and chest squeeze.", false, "Hypertrophy"),
                ExerciseEntity(15, "Cable Face Pull", "Shoulders", "Cable", "Rear Deltoid, Rotator Cuff", "Upper Traps, Rhomboids", 60, "Pull toward eyes with external rotation.", false, "Hypertrophy")
            )
            dao.insertExercises(initialExercises)

            // Populate Bodyweight logs (showing weight coming off over 4 weeks)
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L
            val weightLogs = listOf(
                BodyWeightLogEntity(1, 192.4, now - (28 * dayMillis), "Starting cut phase"),
                BodyWeightLogEntity(2, 191.0, now - (21 * dayMillis), "Good deficit adherence"),
                BodyWeightLogEntity(3, 189.6, now - (14 * dayMillis), "Feeling lighter, energy steady"),
                BodyWeightLogEntity(4, 188.2, now - (7 * dayMillis), "Great progress this week"),
                BodyWeightLogEntity(5, 186.8, now - (1 * dayMillis), "Weigh-in after rest day")
            )
            dao.insertBodyWeightLogs(weightLogs)

            // Populate starter workout sessions for consistency calendar (demonstrating weekly streak)
            val session1Id = dao.insertWorkoutSession(
                WorkoutSessionEntity(
                    id = 1,
                    name = "Push Day - Power",
                    dateMillis = now - (14 * dayMillis),
                    durationMinutes = 55,
                    notes = "Heavy bench felt strong, progressive overload on incline.",
                    totalVolumeLbs = 12450.0,
                    totalSets = 12,
                    completed = true
                )
            )
            val we1 = dao.insertWorkoutExercise(WorkoutExerciseEntity(1, session1Id, 1, "Barbell Bench Press", 0))
            dao.insertExerciseSets(listOf(
                ExerciseSetEntity(1, we1, 1, 1, 185.0, 8, 8.0, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (14 * dayMillis)),
                ExerciseSetEntity(2, we1, 1, 2, 185.0, 8, 8.5, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (14 * dayMillis)),
                ExerciseSetEntity(3, we1, 1, 3, 185.0, 7, 9.0, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (14 * dayMillis))
            ))

            val session2Id = dao.insertWorkoutSession(
                WorkoutSessionEntity(
                    id = 2,
                    name = "Pull Day - Hypertrophy",
                    dateMillis = now - (7 * dayMillis),
                    durationMinutes = 50,
                    notes = "Great mind-muscle connection on lat pulldowns.",
                    totalVolumeLbs = 10800.0,
                    totalSets = 11,
                    completed = true
                )
            )
            val we2 = dao.insertWorkoutExercise(WorkoutExerciseEntity(2, session2Id, 5, "Lat Pulldown", 0))
            dao.insertExerciseSets(listOf(
                ExerciseSetEntity(4, we2, 5, 1, 150.0, 10, 8.0, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (7 * dayMillis)),
                ExerciseSetEntity(5, we2, 5, 2, 150.0, 10, 8.5, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (7 * dayMillis)),
                ExerciseSetEntity(6, we2, 5, 3, 150.0, 9, 9.0, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (7 * dayMillis))
            ))

            val session3Id = dao.insertWorkoutSession(
                WorkoutSessionEntity(
                    id = 3,
                    name = "Leg Day - Strength",
                    dateMillis = now - (2 * dayMillis),
                    durationMinutes = 60,
                    notes = "Hit depth on all squat sets.",
                    totalVolumeLbs = 15200.0,
                    totalSets = 14,
                    completed = true
                )
            )
            val we3 = dao.insertWorkoutExercise(WorkoutExerciseEntity(3, session3Id, 3, "Barbell Back Squat", 0))
            dao.insertExerciseSets(listOf(
                ExerciseSetEntity(7, we3, 3, 1, 245.0, 8, 8.0, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (2 * dayMillis)),
                ExerciseSetEntity(8, we3, 3, 2, 245.0, 8, 8.5, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (2 * dayMillis)),
                ExerciseSetEntity(9, we3, 3, 3, 245.0, 8, 9.0, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (2 * dayMillis))
            ))

            // Bench Press recent set to show overload threshold
            val we1b = dao.insertWorkoutExercise(WorkoutExerciseEntity(4, session3Id, 1, "Barbell Bench Press", 1))
            dao.insertExerciseSets(listOf(
                ExerciseSetEntity(10, we1b, 1, 1, 185.0, 12, 8.5, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (2 * dayMillis)),
                ExerciseSetEntity(11, we1b, 1, 2, 185.0, 12, 9.0, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (2 * dayMillis)),
                ExerciseSetEntity(12, we1b, 1, 3, 185.0, 11, 9.5, setType = "R", isWarmup = false, isPersonalRecord = false, completedAtMillis = now - (2 * dayMillis))
            ))

            // Initial PRs
            val prs = listOf(
                PersonalRecordEntity(1, 1, "Barbell Bench Press", "MaxWeight", 225.0, 1, now - (14 * dayMillis)),
                PersonalRecordEntity(2, 1, "Barbell Bench Press", "Est1RM", 248.0, 12, now - (2 * dayMillis)),
                PersonalRecordEntity(3, 3, "Barbell Back Squat", "MaxWeight", 295.0, 1, now - (10 * dayMillis)),
                PersonalRecordEntity(4, 5, "Lat Pulldown", "MaxWeight", 170.0, 6, now - (5 * dayMillis))
            )
            dao.insertPersonalRecords(prs)

            // Initial Custom Routines
            val routines = listOf(
                CustomRoutineEntity(1, "Push Day - Power", "Chest, Shoulders, Triceps", "Push", "1,2,7,8,10"),
                CustomRoutineEntity(2, "Pull Day - Strength", "Back, Biceps, Rear Delts", "Pull", "5,6,9,15"),
                CustomRoutineEntity(3, "Leg Day - Hypertrophy", "Quads, Hamstrings, Glutes", "Legs", "3,4,11,12,13"),
                CustomRoutineEntity(4, "Upper Body Specialization", "Chest, Back, Arms", "Upper", "1,5,7,9,10")
            )
            dao.insertCustomRoutines(routines)

            // Planned Workouts for the week (Scheduling feature)
            val cal = Calendar.getInstance()
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sun, 2 = Mon ...
            val plannedWorkouts = listOf(
                PlannedWorkoutEntity(
                    id = 1,
                    routineName = "Push Day - Power",
                    dayOfWeek = 2, // Monday
                    dateMillis = now - (2 * dayMillis),
                    focusDescription = "Chest, Front Delts & Triceps",
                    exerciseIdsCsv = "1,2,7,8,10",
                    targetNotes = "Bench press overload target: 190 lbs × 8 reps",
                    isCompleted = true
                ),
                PlannedWorkoutEntity(
                    id = 2,
                    routineName = "Pull Day - Strength",
                    dayOfWeek = 4, // Wednesday
                    dateMillis = now + (1 * dayMillis),
                    focusDescription = "Lats, Rhomboids & Biceps",
                    exerciseIdsCsv = "5,6,9,15",
                    targetNotes = "Lat Pulldowns: Target 155 lbs for 3x10",
                    isCompleted = false
                ),
                PlannedWorkoutEntity(
                    id = 3,
                    routineName = "Leg Day - Hypertrophy",
                    dayOfWeek = 6, // Friday
                    dateMillis = now + (3 * dayMillis),
                    focusDescription = "Quads, Hamstrings & Calves",
                    exerciseIdsCsv = "3,4,11,12,13",
                    targetNotes = "Squat progressive overload target: 250 lbs",
                    isCompleted = false
                )
            )
            dao.insertPlannedWorkouts(plannedWorkouts)

            // Initial AI Movement Guide
            dao.insertAiGuide(
                AiMovementGuideEntity(
                    id = 1,
                    machineDescription = "Seated Cable Row with Neutral Close-Grip V-Bar Handle",
                    movementDescription = "Drive elbows backward horizontally while maintaining strict lumbar stability and chest proud.",
                    exerciseName = "Seated Close-Grip Cable Row",
                    primaryMuscles = "Latissimus Dorsi, Rhomboids, Middle Trapezius",
                    secondaryMuscles = "Biceps Brachii, Posterior Deltoid, Erector Spinae",
                    setupInstructions = "Set seat height so cable pulley aligns with lower chest. Place feet securely on footrests with soft knee bend.",
                    executionSteps = "1. Initiate pull by retracting scapulae.\n2. Drive elbows straight back close to torso.\n3. Squeeze back muscles for 1s at peak contraction.\n4. Control eccentric return over 2-3 seconds without rounding lower back.",
                    formCues = "Lead with the elbows, not hands. Avoid swinging momentum at hips.",
                    commonMistakes = "Excessive backward lean, shrugging shoulders up to ears.",
                    suggestedStartingWeight = "80 - 100 lbs for 3 sets of 10-12 reps",
                    concentricPhaseCue = "Concentric: Squeeze scapulae back and drive elbows tightly to ribs (1s pause).",
                    eccentricPhaseCue = "Eccentric: Lengthen lats smoothly over 2-3s without rounding upper back.",
                    createdAtMillis = now - (2 * dayMillis)
                )
            )
        }
    }
}
