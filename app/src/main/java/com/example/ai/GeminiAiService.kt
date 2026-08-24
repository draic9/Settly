package com.example.ai

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiMovementAnalysis(
    val exerciseName: String,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val muscleActivationMap: Map<String, Int>, // e.g. "LATS" -> 90, "CHEST" -> 0, etc.
    val machineSetup: String,
    val executionSteps: List<String>,
    val formCues: List<String>,
    val commonMistakes: List<String>,
    val suggestedStartingWeight: String,
    val blueprintType: String // e.g., "CABLE_ROW", "BENCH", "SQUAT", "LAT_PULL", "LEG_PRESS", "DUMBBELL", "GENERIC"
)

class GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeMovementAndMachine(
        machineDescription: String,
        movementDescription: String
    ): AiMovementAnalysis = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val result = callGeminiApi(apiKey, machineDescription, movementDescription)
                if (result != null) return@withContext result
            } catch (e: Exception) {
                // Fallback to local expert biomechanics engine on network/API error
            }
        }

        // Offline / Fallback Intelligent Biomechanics Analyzer
        return@withContext generateLocalBiomechanicsAnalysis(machineDescription, movementDescription)
    }

    private fun callGeminiApi(
        apiKey: String,
        machine: String,
        movement: String
    ): AiMovementAnalysis? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val prompt = """
            You are an elite strength & biomechanics coach. Analyze the following gym machine and exercise movement description:
            Machine: "$machine"
            Movement/Grip/Stance: "$movement"

            Return a valid JSON object strictly matching this schema:
            {
              "exerciseName": "Standardized Exercise Name",
              "primaryMuscles": ["Muscle 1", "Muscle 2"],
              "secondaryMuscles": ["Muscle 3", "Muscle 4"],
              "activeMuscleIds": {
                "CHEST": 0 to 100,
                "UPPER_CHEST": 0 to 100,
                "LATS": 0 to 100,
                "TRAPS": 0 to 100,
                "RHOMBOIDS": 0 to 100,
                "FRONT_DELTS": 0 to 100,
                "SIDE_DELTS": 0 to 100,
                "REAR_DELTS": 0 to 100,
                "BICEPS": 0 to 100,
                "TRICEPS": 0 to 100,
                "FOREARMS": 0 to 100,
                "ABS": 0 to 100,
                "OBLIQUES": 0 to 100,
                "LOWER_BACK": 0 to 100,
                "GLUTES": 0 to 100,
                "QUADS": 0 to 100,
                "HAMSTRINGS": 0 to 100,
                "CALVES": 0 to 100
              },
              "machineSetup": "Precise seat/pad/pulley/pin setup instructions",
              "executionSteps": [
                "Step 1: Starting stance and grip",
                "Step 2: Concentric movement path",
                "Step 3: Peak contraction and squeeze",
                "Step 4: Controlled eccentric return"
              ],
              "formCues": ["Mind-muscle cue 1", "Cue 2"],
              "commonMistakes": ["Mistake 1 to avoid", "Mistake 2"],
              "suggestedStartingWeight": "Estimated beginner to intermediate starting load in lbs and rep range",
              "blueprintType": "One of: CABLE_ROW, BENCH_PRESS, SQUAT_RACK, LAT_PULLDOWN, LEG_PRESS, PEC_FLY, CABLE_PULLEY, DUMBBELL, SMITH_MACHINE, GENERIC"
            }
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.3)
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val bodyString = response.body?.string() ?: return null

        val responseJson = JSONObject(bodyString)
        val candidates = responseJson.optJSONArray("candidates") ?: return null
        val firstCandidate = candidates.optJSONObject(0) ?: return null
        val content = firstCandidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        val text = parts.optJSONObject(0)?.optString("text") ?: return null

        val parsed = JSONObject(text)
        val primary = mutableListOf<String>()
        val primaryArr = parsed.optJSONArray("primaryMuscles")
        if (primaryArr != null) {
            for (i in 0 until primaryArr.length()) primary.add(primaryArr.getString(i))
        }

        val secondary = mutableListOf<String>()
        val secondaryArr = parsed.optJSONArray("secondaryMuscles")
        if (secondaryArr != null) {
            for (i in 0 until secondaryArr.length()) secondary.add(secondaryArr.getString(i))
        }

        val steps = mutableListOf<String>()
        val stepsArr = parsed.optJSONArray("executionSteps")
        if (stepsArr != null) {
            for (i in 0 until stepsArr.length()) steps.add(stepsArr.getString(i))
        }

        val cues = mutableListOf<String>()
        val cuesArr = parsed.optJSONArray("formCues")
        if (cuesArr != null) {
            for (i in 0 until cuesArr.length()) cues.add(cuesArr.getString(i))
        }

        val mistakes = mutableListOf<String>()
        val mistakesArr = parsed.optJSONArray("commonMistakes")
        if (mistakesArr != null) {
            for (i in 0 until mistakesArr.length()) mistakes.add(mistakesArr.getString(i))
        }

        val muscleMap = mutableMapOf<String, Int>()
        val activeIdsObj = parsed.optJSONObject("activeMuscleIds")
        if (activeIdsObj != null) {
            val keys = activeIdsObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                muscleMap[key] = activeIdsObj.optInt(key, 0)
            }
        }

        return AiMovementAnalysis(
            exerciseName = parsed.optString("exerciseName", "Custom Movement"),
            primaryMuscles = if (primary.isEmpty()) listOf("Target Muscle Group") else primary,
            secondaryMuscles = secondary,
            muscleActivationMap = muscleMap,
            machineSetup = parsed.optString("machineSetup", "Adjust machine seat and pin to comfortable range of motion."),
            executionSteps = if (steps.isEmpty()) listOf("Engage core", "Perform controlled rep", "Return slowly") else steps,
            formCues = cues,
            commonMistakes = mistakes,
            suggestedStartingWeight = parsed.optString("suggestedStartingWeight", "Moderate weight (3 sets of 8-12 reps)"),
            blueprintType = parsed.optString("blueprintType", "GENERIC")
        )
    }

    private fun generateLocalBiomechanicsAnalysis(
        machine: String,
        movement: String
    ): AiMovementAnalysis {
        val input = "$machine $movement".lowercase()

        val muscleMap = mutableMapOf<String, Int>()

        val isChest = input.contains("chest") || input.contains("bench") || input.contains("press") && !input.contains("leg") && !input.contains("shoulder") || input.contains("fly") || input.contains("pec")
        val isBack = input.contains("row") || input.contains("lat") || input.contains("pulldown") || input.contains("pull") || input.contains("deadlift") || input.contains("back")
        val isLegs = input.contains("squat") || input.contains("leg") || input.contains("quad") || input.contains("hamstring") || input.contains("calf") || input.contains("calves") || input.contains("hack") || input.contains("lunge")
        val isShoulders = input.contains("shoulder") || input.contains("overhead") || input.contains("lateral") || input.contains("delt") || input.contains("military") || input.contains("face pull")
        val isArms = input.contains("bicep") || input.contains("tricep") || input.contains("curl") || input.contains("pushdown") || input.contains("extension") && !input.contains("leg")

        val name: String
        val primary = mutableListOf<String>()
        val secondary = mutableListOf<String>()
        val setup: String
        val steps = mutableListOf<String>()
        val cues = mutableListOf<String>()
        val mistakes = mutableListOf<String>()
        val startingWeight: String
        val blueprint: String

        when {
            isChest -> {
                name = if (input.contains("incline")) "Incline Chest Movement" else if (input.contains("fly")) "Chest Flye Machine" else "Machine Chest Press"
                primary.addAll(listOf("Pectoralis Major", "Anterior Deltoid"))
                secondary.addAll(listOf("Triceps Brachii", "Serratus Anterior"))
                muscleMap["CHEST"] = 90
                muscleMap["UPPER_CHEST"] = if (input.contains("incline")) 95 else 60
                muscleMap["FRONT_DELTS"] = 75
                muscleMap["TRICEPS"] = 65
                setup = "Set seat height so the handles align directly with mid-to-lower chest level. Plant feet firmly on the ground."
                steps.addAll(listOf(
                    "1. Retract scapulae and drive shoulders back against backrest.",
                    "2. Grip handles evenly with wrists neutral and elbows slightly tucked (45-degree angle).",
                    "3. Exhale and press forward smoothly without locking out elbows.",
                    "4. Inhale and control the eccentric return for 2-3 seconds until you feel a deep pectoral stretch."
                ))
                cues.addAll(listOf("Think of bringing your biceps together across your chest.", "Keep your chest high and collarbones proud throughout."))
                mistakes.addAll(listOf("Flaring elbows out 90 degrees which strains the rotator cuffs.", "Allowing shoulders to roll forward at the peak of the press."))
                startingWeight = "60 - 90 lbs (3 sets of 8 - 12 reps)"
                blueprint = if (input.contains("fly")) "PEC_FLY" else "BENCH_PRESS"
            }
            isBack -> {
                name = if (input.contains("pulldown") || input.contains("lat")) "Lat Pulldown Machine" else "Seated Cable Row"
                primary.addAll(listOf("Latissimus Dorsi", "Rhomboids", "Middle Trapezius"))
                secondary.addAll(listOf("Biceps Brachii", "Rear Deltoids", "Brachialis"))
                muscleMap["LATS"] = 95
                muscleMap["RHOMBOIDS"] = 85
                muscleMap["TRAPS"] = 70
                muscleMap["REAR_DELTS"] = 65
                muscleMap["BICEPS"] = 60
                setup = "Adjust thigh pads or footrests to lock your lower body firmly in place with a 90-degree knee bend."
                steps.addAll(listOf(
                    "1. Secure a firm grip with thumbs wrapped around the bar/handle.",
                    "2. Depress your shoulder blades down before initiating the pull with your elbows.",
                    "3. Drive elbows down and back towards your hip pockets.",
                    "4. Pause for a 1-second squeeze, then control the upward/forward stretch under tension."
                ))
                cues.addAll(listOf("Pull with your elbows, treat your hands simply as hooks.", "Maintain a slight arch in upper thoracic spine without excessive lumbar hyperextension."))
                mistakes.addAll(listOf("Using momentum or aggressive body swing to move the weight.", "Shrugging the traps up toward the neck."))
                startingWeight = "70 - 110 lbs (3 sets of 10 - 12 reps)"
                blueprint = if (input.contains("pulldown")) "LAT_PULLDOWN" else "CABLE_ROW"
            }
            isLegs -> {
                name = if (input.contains("hack")) "Hack Squat" else if (input.contains("press")) "45° Leg Press" else if (input.contains("extension")) "Leg Extension" else "Machine Squat / Leg Press"
                primary.addAll(listOf("Quadriceps (Vastus Medialis, Lateralis)", "Gluteus Maximus"))
                secondary.addAll(listOf("Hamstrings", "Adductors", "Calves"))
                muscleMap["QUADS"] = 95
                muscleMap["GLUTES"] = 80
                muscleMap["HAMSTRINGS"] = 60
                muscleMap["CALVES"] = 40
                setup = "Position back flush against the padded backrest. Place feet shoulder-width apart in middle-upper region of the platform."
                steps.addAll(listOf(
                    "1. Release safety handles while maintaining continuous foot contact through the heels and balls of feet.",
                    "2. Lower the carriage smoothly until knees reach at least a 90-degree angle without butt lifting off pad.",
                    "3. Drive through the midfoot and heel to press back up.",
                    "4. Stop just short of total knee lockout to maintain continuous quadriceps tension."
                ))
                cues.addAll(listOf("Keep knees tracking in line with your second toe.", "Drive your lower back firmly into the pad throughout."))
                mistakes.addAll(listOf("Locking out knees aggressively with high joint impact.", "Letting knees cave inward during the concentric drive."))
                startingWeight = "140 - 200 lbs (3 sets of 8 - 10 reps)"
                blueprint = "LEG_PRESS"
            }
            isShoulders -> {
                name = if (input.contains("lateral")) "Machine Lateral Raise" else "Machine Shoulder Press"
                primary.addAll(listOf("Lateral Deltoid", "Anterior Deltoid"))
                secondary.addAll(listOf("Upper Trapezius", "Triceps", "Rotator Cuff"))
                muscleMap["SIDE_DELTS"] = 95
                muscleMap["FRONT_DELTS"] = 75
                muscleMap["TRAPS"] = 50
                setup = "Set seat height so pivot axis aligns with the center of your shoulder joints."
                steps.addAll(listOf(
                    "1. Sit upright with spine supported and core braced.",
                    "2. Lead the movement with your elbows elevating in the scapular plane.",
                    "3. Raise until upper arms are parallel to floor.",
                    "4. Lower under strict 2-second eccentric control."
                ))
                cues.addAll(listOf("Pour the water out of the pitcher at peak elevation.", "Avoid shrugging shoulders to your neck."))
                mistakes.addAll(listOf("Swinging torso back and forth.", "Using excessive weight that forces trap compensation."))
                startingWeight = "20 - 45 lbs (3 sets of 12 - 15 reps)"
                blueprint = "CABLE_PULLEY"
            }
            else -> {
                name = "Custom Machine Movement"
                primary.addAll(listOf("Target Muscle Group", "Core Stabilizers"))
                secondary.addAll(listOf("Synergist Muscles"))
                muscleMap["CHEST"] = 40
                muscleMap["LATS"] = 40
                muscleMap["QUADS"] = 40
                setup = "Adjust machine seat, pad, and weight stack pin to match your natural anatomical limb lengths."
                steps.addAll(listOf(
                    "1. Position yourself with proper posture and firm anchor points.",
                    "2. Initiate the movement with the intended prime mover.",
                    "3. Squeeze for 1 second at peak contraction.",
                    "4. Return slowly along the machine track under full muscular control."
                ))
                cues.addAll(listOf("Maintain smooth breathing cadence (exhale on effort, inhale on return).", "Focus entirely on mind-muscle tension."))
                mistakes.addAll(listOf("Letting weights slam at the bottom of the stack.", "Rushing repetitions without eccentric control."))
                startingWeight = "Moderate starting load (3 sets of 10 - 12 reps)"
                blueprint = "GENERIC"
            }
        }

        return AiMovementAnalysis(
            exerciseName = name,
            primaryMuscles = primary,
            secondaryMuscles = secondary,
            muscleActivationMap = muscleMap,
            machineSetup = setup,
            executionSteps = steps,
            formCues = cues,
            commonMistakes = mistakes,
            suggestedStartingWeight = startingWeight,
            blueprintType = blueprint
        )
    }
}
