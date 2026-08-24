package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.GymDatabase
import com.example.data.repository.GymRepository
import com.example.ui.GymViewModel
import com.example.ui.components.LockerSettingsDialog
import com.example.ui.components.LogWeightDialog
import com.example.ui.components.PRCelebrationDialog
import com.example.ui.screens.AiMovementVisualizerScreen
import com.example.ui.screens.CalendarAnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExerciseLibraryScreen
import com.example.ui.screens.WorkoutLoggerScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSlateElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricRoyalBlue
import com.example.ui.theme.GoldPR
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoyalBlue300
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.RoyalBlue800
import com.example.ui.theme.RoyalBlue900
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = GymDatabase.getDatabase(this, kotlinx.coroutines.GlobalScope)
        val repository = GymRepository(database.gymDao())
        val viewModelFactory = GymViewModel.Factory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: GymViewModel by viewModels { viewModelFactory }
                GymApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymApp(viewModel: GymViewModel) {
    var currentTab by remember { mutableIntStateOf(0) } // 0: Dashboard, 1: Exercise Hub, 2: Logger, 3: Calendar
    var showWeightLogDialog by remember { mutableStateOf(false) }
    var showLockerSettingsDialog by remember { mutableStateOf(false) }
    var showAiVisualizerScreen by remember { mutableStateOf(false) }

    // States from ViewModel
    val exercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val workoutSessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val plannedWorkouts by viewModel.allPlannedWorkouts.collectAsStateWithLifecycle()
    val customRoutines by viewModel.allCustomRoutines.collectAsStateWithLifecycle()
    val streakInfo by viewModel.streakInfo.collectAsStateWithLifecycle()
    val bodyWeightLogs by viewModel.allBodyWeightLogs.collectAsStateWithLifecycle()
    val bodyWeightSummary by viewModel.bodyWeightSummary.collectAsStateWithLifecycle()
    val personalRecords by viewModel.allPRs.collectAsStateWithLifecycle()
    val savedAiGuides by viewModel.allAiGuides.collectAsStateWithLifecycle()
    val activeWorkout by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val aiAnalysisState by viewModel.aiAnalysisState.collectAsStateWithLifecycle()
    val latestPR by viewModel.latestNewPR.collectAsStateWithLifecycle()
    val progressionMap by viewModel.progressionMap.collectAsStateWithLifecycle()
    val isKgUnit by viewModel.isKgUnit.collectAsStateWithLifecycle()

    // Live Badge pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "livePulseTransition")
    val livePulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "livePulseAlpha"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Settle",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = TextPrimaryDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(ElectricCyan, CircleShape)
                            )
                        }
                        Text(
                            text = "Intelligent progressive overload",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark
                        )
                    }
                },
                actions = {
                    // AI Movement Visualizer
                    IconButton(onClick = { showAiVisualizerScreen = true }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI movement visualizer",
                            tint = ElectricCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Locker & Settings
                    IconButton(onClick = { showLockerSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locker & settings",
                            tint = RoyalBlue300,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = RoyalBlue900,
                contentColor = ElectricCyan,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        RoyalBlue800,
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                // Tab 0: Dashboard
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard", fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = RoyalBlue300,
                        unselectedTextColor = RoyalBlue300,
                        indicatorColor = RoyalBlue700
                    ),
                    modifier = Modifier.testTag("nav_dashboard_tab")
                )

                // Tab 1: Exercise & Routine Library
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = "Exercises"
                        )
                    },
                    label = { Text("Exercises", fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = RoyalBlue300,
                        unselectedTextColor = RoyalBlue300,
                        indicatorColor = RoyalBlue700
                    ),
                    modifier = Modifier.testTag("nav_exercises_tab")
                )

                // Tab 2: Workout Logger
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        if (activeWorkout != null) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = ElectricCyan.copy(alpha = livePulseAlpha),
                                        contentColor = Color.Black,
                                        modifier = Modifier.offset(x = 6.dp, y = (-2).dp)
                                    ) {
                                        Text(
                                            text = "LIVE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.5.sp
                                            )
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = "Logger"
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = "Logger"
                            )
                        }
                    },
                    label = { Text("Active Log", fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = RoyalBlue300,
                        unselectedTextColor = RoyalBlue300,
                        indicatorColor = RoyalBlue700
                    ),
                    modifier = Modifier.testTag("nav_logger_tab")
                )

                // Tab 3: Calendar & Schedule
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar"
                        )
                    },
                    label = { Text("Calendar", fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        unselectedIconColor = RoyalBlue300,
                        unselectedTextColor = RoyalBlue300,
                        indicatorColor = RoyalBlue700
                    ),
                    modifier = Modifier.testTag("nav_calendar_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(
                    exercises = exercises,
                    recentSessions = workoutSessions,
                    plannedWorkouts = plannedWorkouts,
                    customRoutines = customRoutines,
                    streakInfo = streakInfo,
                    bodyWeightSummary = bodyWeightSummary,
                    bodyWeightLogs = bodyWeightLogs,
                    progressionMap = progressionMap,
                    personalRecords = personalRecords,
                    aiGuides = savedAiGuides,
                    isKgUnit = isKgUnit,
                    onStartWorkout = { name, selectedExs ->
                        viewModel.startNewWorkout(name, selectedExs)
                        currentTab = 2
                    },
                    onNavigateToLogger = { currentTab = 2 },
                    onNavigateToAiVisualizer = { showAiVisualizerScreen = true },
                    onNavigateToCalendar = { currentTab = 3 },
                    onNavigateToRoutinesTab = { currentTab = 1 },
                    onOpenWeightLogDialog = { showWeightLogDialog = true },
                    onScheduleWorkout = { name, dayOfWeek, dateMillis, exCsv, focus, notes ->
                        viewModel.scheduleWorkout(name, dayOfWeek, dateMillis, exCsv, focus, notes)
                    },
                    onCreateCustomRoutine = { name, sub, exCsv, focus ->
                        viewModel.createCustomRoutine(name, sub, exCsv, focus)
                    },
                    onTogglePinRoutine = { routine ->
                        viewModel.togglePinCustomRoutine(routine)
                    },
                    onUpdatePR = { pr, weight, reps ->
                        viewModel.updatePersonalRecord(pr, weight, reps)
                    },
                    onDeletePR = { pr ->
                        viewModel.deletePersonalRecord(pr)
                    },
                    onGenerateAiGuide = { name, category ->
                        viewModel.generateAiGuideForExercise(name, category)
                    },
                    onApplyOverloadTarget = { exId, targetWeight ->
                        viewModel.applyOverloadTargetToRoutines(exId, targetWeight)
                    },
                    onUpdateTrainingGoal = { exId, goal ->
                        viewModel.updateTrainingGoal(exId, goal)
                    }
                )
                1 -> ExerciseLibraryScreen(
                    exercises = exercises,
                    customRoutines = customRoutines,
                    personalRecords = personalRecords,
                    aiGuides = savedAiGuides,
                    progressionMap = progressionMap,
                    isKgUnit = isKgUnit,
                    onStartWorkout = { name, selectedExs ->
                        viewModel.startNewWorkout(name, selectedExs)
                        currentTab = 2
                    },
                    onCreateRoutine = { name, sub, exCsv, focus ->
                        viewModel.createCustomRoutine(name, sub, exCsv, focus)
                    },
                    onTogglePinRoutine = { routine ->
                        viewModel.togglePinCustomRoutine(routine)
                    },
                    onDeleteRoutine = { routine ->
                        viewModel.deleteCustomRoutine(routine)
                    },
                    onAddNewExercise = { exercise ->
                        viewModel.addNewExercise(exercise)
                    },
                    onGenerateAiGuide = { machine, movement ->
                        viewModel.generateAiGuideForExercise(machine, movement)
                    }
                )
                2 -> WorkoutLoggerScreen(
                    activeWorkout = activeWorkout,
                    allExercises = exercises,
                    pastSessions = workoutSessions,
                    onStartWorkout = { name, selectedExs ->
                        viewModel.startNewWorkout(name, selectedExs)
                    },
                    onAddExerciseToActiveWorkout = { ex ->
                        viewModel.addExerciseToActiveWorkout(ex)
                    },
                    onAddSet = { exIdx ->
                        viewModel.addSetToExercise(exIdx)
                    },
                    onRemoveSet = { exIdx, setIdx ->
                        viewModel.removeSetFromExercise(exIdx, setIdx)
                    },
                    onUpdateSet = { exIdx, setIdx, w, r, rpe, isWarmup, isComp ->
                        viewModel.updateSetValues(exIdx, setIdx, w, r, rpe, isWarmup = isWarmup, isCompleted = isComp)
                    },
                    onCycleSetType = { exIdx, setIdx ->
                        viewModel.cycleSetType(exIdx, setIdx)
                    },
                    onSetExplicitSetType = { exIdx, setIdx, type ->
                        viewModel.setExplicitSetType(exIdx, setIdx, type)
                    },
                    onToggleSetCompleted = { exIdx, setIdx ->
                        viewModel.toggleSetCompleted(exIdx, setIdx)
                    },
                    onFinishWorkout = {
                        viewModel.finishActiveWorkout()
                    },
                    onDiscardWorkout = {
                        viewModel.discardActiveWorkout()
                    }
                )
                3 -> CalendarAnalyticsScreen(
                    workoutSessions = workoutSessions,
                    plannedWorkouts = plannedWorkouts,
                    customRoutines = customRoutines,
                    exercises = exercises,
                    isKgUnit = isKgUnit,
                    onScheduleWorkout = { name, dayOfWeek, dateMillis, exCsv, focus, notes ->
                        viewModel.scheduleWorkout(name, dayOfWeek, dateMillis, exCsv, focus, notes)
                    },
                    onTogglePlannedCompleted = { planned ->
                        viewModel.togglePlannedWorkoutCompleted(planned)
                    },
                    onDeletePlannedWorkout = { planned ->
                        viewModel.deletePlannedWorkout(planned)
                    },
                    onStartWorkout = { name, selectedExs ->
                        viewModel.startNewWorkout(name, selectedExs)
                        currentTab = 2
                    }
                )
            }
        }
    }

    // AI Movement Visualizer Fullscreen Modal
    if (showAiVisualizerScreen) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkBackground
        ) {
            AiMovementVisualizerScreen(
                aiAnalysisState = aiAnalysisState,
                savedGuides = savedAiGuides,
                onAnalyze = { machine, movement ->
                    viewModel.analyzeMovementWithAI(machine, movement)
                },
                onSaveGuide = { analysis, machine, movement ->
                    viewModel.saveAiGuide(analysis, machine, movement)
                },
                onStartWorkoutWithMovement = { movementName ->
                    val matchingEx = exercises.find { it.name.equals(movementName, true) }
                    if (matchingEx != null) {
                        viewModel.startNewWorkout("AI: $movementName", listOf(matchingEx))
                    } else {
                        viewModel.startNewWorkout("AI: $movementName", emptyList())
                    }
                    showAiVisualizerScreen = false
                    currentTab = 2
                },
                onDismiss = { showAiVisualizerScreen = false }
            )
        }
    }

    // Locker & Settings Dialog (Units, PR Editor)
    if (showLockerSettingsDialog) {
        LockerSettingsDialog(
            isKgUnit = isKgUnit,
            personalRecords = personalRecords,
            onToggleUnit = { viewModel.toggleWeightUnit() },
            onUpdatePR = { pr, newWeight, newReps ->
                viewModel.updatePersonalRecord(pr, newWeight, newReps)
            },
            onDeletePR = { pr ->
                viewModel.deletePersonalRecord(pr)
            },
            onDismiss = { showLockerSettingsDialog = false }
        )
    }

    // Body Weight Dialog
    if (showWeightLogDialog) {
        LogWeightDialog(
            isKgUnit = isKgUnit,
            onSaveWeight = { weight, note ->
                viewModel.logBodyWeight(weight, note)
                showWeightLogDialog = false
            },
            onDismiss = { showWeightLogDialog = false }
        )
    }

    // PR Celebration Pop-up
    latestPR?.let { pr ->
        PRCelebrationDialog(
            record = pr,
            isKgUnit = isKgUnit,
            onDismiss = { viewModel.dismissPRCelebration() }
        )
    }
}
