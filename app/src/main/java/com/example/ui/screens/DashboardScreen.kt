package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiMovementGuideEntity
import com.example.data.model.BodyWeightLogEntity
import com.example.data.model.CustomRoutineEntity
import com.example.data.model.ExerciseEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.PlannedWorkoutEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.data.repository.BodyWeightSummary
import com.example.data.repository.ExerciseProgressionSuggestion
import com.example.data.repository.StreakInfo
import com.example.ui.components.ChartDataPoint
import com.example.ui.components.CreateRoutineDialog
import com.example.ui.components.ExerciseDetailSheet
import com.example.ui.components.ExerciseSelectorSheetDialog
import com.example.ui.components.RotaryBarrelMuscleSelector
import com.example.ui.components.ScheduleWorkoutDialog
import com.example.ui.components.WeightProgressChart
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardBorderBlue
import com.example.ui.theme.DarkSlateElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricRoyalBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldPR
import com.example.ui.theme.RoyalBlue200
import com.example.ui.theme.RoyalBlue300
import com.example.ui.theme.RoyalBlue400
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.RoyalBlue800
import com.example.ui.theme.RoyalBlue900
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    exercises: List<ExerciseEntity>,
    recentSessions: List<WorkoutSessionEntity>,
    plannedWorkouts: List<PlannedWorkoutEntity>,
    customRoutines: List<CustomRoutineEntity>,
    streakInfo: StreakInfo,
    bodyWeightSummary: BodyWeightSummary,
    bodyWeightLogs: List<BodyWeightLogEntity>,
    progressionMap: Map<Long, ExerciseProgressionSuggestion>,
    personalRecords: List<PersonalRecordEntity>,
    aiGuides: List<AiMovementGuideEntity> = emptyList(),
    isKgUnit: Boolean,
    onStartWorkout: (String, List<ExerciseEntity>) -> Unit,
    onNavigateToLogger: () -> Unit,
    onNavigateToAiVisualizer: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToRoutinesTab: () -> Unit = {},
    onOpenWeightLogDialog: () -> Unit,
    onScheduleWorkout: (String, Int, Long, String, String, String) -> Unit,
    onCreateCustomRoutine: (String, String, String, String) -> Unit,
    onTogglePinRoutine: (CustomRoutineEntity) -> Unit = {},
    onUpdatePR: (PersonalRecordEntity, Double, Int) -> Unit = { _, _, _ -> },
    onDeletePR: (PersonalRecordEntity) -> Unit = {},
    onGenerateAiGuide: (String, String) -> Unit = { _, _ -> },
    onApplyOverloadTarget: (Long, Double) -> Unit,
    onUpdateTrainingGoal: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedExerciseId by remember(exercises) {
        mutableStateOf(exercises.firstOrNull()?.id ?: 1L)
    }

    var selectedMuscleGroup by remember { mutableStateOf("all movements") }
    var showExerciseSelectorDialog by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var showCreateRoutineDialog by remember { mutableStateOf(false) }
    var currentHintIndex by remember(selectedExerciseId) { mutableIntStateOf(0) }
    var showGoalDropdown by remember { mutableStateOf(false) }
    var appliedSuccessExerciseId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(appliedSuccessExerciseId) {
        if (appliedSuccessExerciseId != null) {
            kotlinx.coroutines.delay(2000L)
            appliedSuccessExerciseId = null
        }
    }

    // Detail sheet & PR editor states
    var selectedExerciseForDetail by remember { mutableStateOf<ExerciseEntity?>(null) }
    var editingPr by remember { mutableStateOf<PersonalRecordEntity?>(null) }

    // Day tap inspection dialog state
    var selectedDayForInspection by remember { mutableStateOf<WeeklyDayState?>(null) }

    val filteredExercisesByMuscle = remember(exercises, selectedMuscleGroup) {
        if (selectedMuscleGroup.equals("all movements", ignoreCase = true)) {
            exercises
        } else {
            exercises.filter { it.category.equals(selectedMuscleGroup, ignoreCase = true) }
        }
    }

    // Keep selectedExercise in sync
    val selectedExercise = exercises.find { it.id == selectedExerciseId } ?: exercises.firstOrNull()
    val progression = if (selectedExercise != null) progressionMap[selectedExercise.id] else null

    // Weekly consistency days (Mon to Sun of current week)
    val weekDays = remember(recentSessions, plannedWorkouts) {
        val cal = Calendar.getInstance()
        val currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val days = mutableListOf<WeeklyDayState>()

        val completedEpochs = recentSessions.filter { it.completed }.map {
            val c = Calendar.getInstance().apply { timeInMillis = it.dateMillis }
            c.get(Calendar.YEAR) * 366 + c.get(Calendar.DAY_OF_YEAR)
        }.toSet()

        val plannedByDayOfWeek = plannedWorkouts.groupBy { it.dayOfWeek }

        val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")
        val dayNumbers = listOf(2, 3, 4, 5, 6, 7, 1) // Calendar.MONDAY = 2, SUNDAY = 1
        val fullDayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val todayEpochDay = cal.get(Calendar.YEAR) * 366 + cal.get(Calendar.DAY_OF_YEAR)

        val firstDayOffset = if (currentDayOfWeek == Calendar.SUNDAY) -6 else 2 - currentDayOfWeek
        cal.add(Calendar.DAY_OF_YEAR, firstDayOffset)

        for (i in 0..6) {
            val dEpoch = cal.get(Calendar.YEAR) * 366 + cal.get(Calendar.DAY_OF_YEAR)
            val isToday = dEpoch == todayEpochDay
            val dayNum = dayNumbers[i]
            val hasCompleted = completedEpochs.contains(dEpoch)
            val dayPlannedList = plannedByDayOfWeek[dayNum] ?: emptyList()
            days.add(
                WeeklyDayState(
                    name = dayNames[i],
                    fullName = fullDayNames[i],
                    dayNumber = dayNum,
                    isToday = isToday,
                    hasCompleted = hasCompleted,
                    hasPlanned = dayPlannedList.isNotEmpty(),
                    plannedWorkouts = dayPlannedList
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        days
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- 1. Top Section: Weekly Training Plan Header with Streak Badge in same row ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkCardBorderBlue, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSlateElevated),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SectionKicker(
                        icon = Icons.Default.CalendarMonth,
                        text = "WEEKLY OVERVIEW",
                        tint = ElectricCyan
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Title & Streak Badge in the same row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly training plan",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )

                        // Compact Streak Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RoyalBlue800,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (streakInfo.activeWeeksCount > 0) Color(0xFFFF9800) else RoyalBlue700)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = if (streakInfo.activeWeeksCount > 0) Color(0xFFFF9800) else RoyalBlue400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = streakInfo.streakLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (streakInfo.activeWeeksCount > 0) Color.White else TextMutedDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Interactive Mon-Sun Clickable Circles with centered letters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        weekDays.forEach { dayState ->
                            val circleBg = when {
                                dayState.hasCompleted -> EmeraldSuccess
                                dayState.isToday -> ElectricRoyalBlue
                                dayState.hasPlanned -> RoyalBlue700
                                else -> RoyalBlue900
                            }
                            val circleBorder = when {
                                dayState.hasCompleted -> EmeraldSuccess
                                dayState.isToday -> ElectricCyan
                                dayState.hasPlanned -> ElectricRoyalBlue
                                else -> DarkCardBorder
                            }
                            val textColor = when {
                                dayState.hasCompleted -> Color.Black
                                dayState.isToday -> Color.White
                                dayState.hasPlanned -> ElectricCyan
                                else -> TextSecondaryDark
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(circleBg, CircleShape)
                                    .border(1.5.dp, circleBorder, CircleShape)
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedDayForInspection = dayState
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayState.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (dayState.isToday || dayState.hasCompleted) FontWeight.Black else FontWeight.Bold
                                    ),
                                    color = textColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Consistency subtext & Schedule Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${streakInfo.workoutsThisWeek} workouts completed this week",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedDark
                        )

                        OutlinedButton(
                            onClick = { showScheduleDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricRoyalBlue),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Schedule workout", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // --- 2. Upcoming Scheduled Workouts Preview ---
        if (plannedWorkouts.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkCardBorderBlue, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateElevated),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                SectionKicker(
                                    icon = Icons.Default.CalendarMonth,
                                    text = "coming up",
                                    tint = ElectricCyan
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "upcoming schedule",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimaryDark
                                )
                            }

                            TextButton(onClick = onNavigateToCalendar) {
                                Text("view calendar", color = ElectricCyan, style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(plannedWorkouts) { planned ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = RoyalBlue900,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                                    modifier = Modifier
                                        .width(220.dp)
                                        .clickable {
                                            val exIds = planned.exerciseIdsCsv.split(",").mapNotNull { it.trim().toLongOrNull() }
                                            val matched = exercises.filter { exIds.contains(it.id) }
                                            onStartWorkout(planned.routineName, matched)
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = getDayName(planned.dayOfWeek),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = ElectricCyan
                                            )
                                            if (planned.isCompleted) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = EmeraldSuccess.copy(alpha = 0.25f)
                                                ) {
                                                    Text(
                                                        text = "done",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                        color = EmeraldSuccess,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = planned.routineName.lowercase(),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimaryDark,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = planned.focusDescription.lowercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark,
                                            maxLines = 1
                                        )

                                        if (planned.targetNotes.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = planned.targetNotes.lowercase(),
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                color = RoyalBlue300,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Progressive Overload Planner with Rotary Barrel Selector ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkCardBorderBlue, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSlateElevated),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SectionKicker(
                        icon = Icons.Default.TrendingUp,
                        text = "where to start next week",
                        tint = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "progressive overload planner",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Muscle Group Filter with Rotary Barrel Selector & Filter modal trigger
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RotaryBarrelMuscleSelector(
                            selectedMuscle = selectedMuscleGroup,
                            onMuscleChanged = {
                                selectedMuscleGroup = it
                                val firstMatch = if (it.equals("all movements", ignoreCase = true)) {
                                    exercises.firstOrNull()
                                } else {
                                    exercises.firstOrNull { ex -> ex.category.equals(it, ignoreCase = true) }
                                }
                                if (firstMatch != null) {
                                    selectedExerciseId = firstMatch.id
                                    currentHintIndex = 0
                                }
                            }
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RoyalBlue800,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricRoyalBlue),
                            modifier = Modifier.clickable { showExerciseSelectorDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = "filter", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("all (${exercises.size})", style = MaterialTheme.typography.labelSmall, color = TextPrimaryDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Exercise Chips filtered by muscle
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredExercisesByMuscle) { ex ->
                            val isSelected = ex.id == selectedExerciseId
                            val prog = progressionMap[ex.id]
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ElectricRoyalBlue else RoyalBlue900,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ElectricCyan else DarkCardBorder
                                ),
                                modifier = Modifier.clickable {
                                    selectedExerciseId = ex.id
                                    currentHintIndex = 0
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ex.name.lowercase(),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) Color.White else TextSecondaryDark
                                    )
                                    if (prog?.isReadyToOverload == true) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = EmeraldSuccess.copy(alpha = 0.25f)
                                        ) {
                                            Text(
                                                text = "+",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                                color = EmeraldSuccess,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progression Breakdown Card
                    if (progression != null && selectedExercise != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF0B0E14),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorderBlue)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Exercise Title & Training Goal Dropdown Menu + Info Button for Sheet
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { selectedExerciseForDetail = selectedExercise }
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = selectedExercise.name.lowercase(),
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = TextPrimaryDark
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Info,
                                                    contentDescription = "exercise details",
                                                    tint = ElectricCyan,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Text(
                                                text = "${selectedExercise.category.lowercase()} · ${selectedExercise.equipment.lowercase()}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMutedDark
                                            )
                                        }
                                    }

                                    // Goal Dropdown Selector (Hypertrophy, Strength, Endurance, Relaxed)
                                    Box {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = RoyalBlue800,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue700),
                                            modifier = Modifier.clickable { showGoalDropdown = true }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = selectedExercise.trainingGoal.lowercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = ElectricCyan
                                                )
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = showGoalDropdown,
                                            onDismissRequest = { showGoalDropdown = false }
                                        ) {
                                            listOf("Hypertrophy", "Strength", "Endurance", "Relaxed").forEach { goalOption ->
                                                DropdownMenuItem(
                                                    text = { Text(goalOption.lowercase()) },
                                                    onClick = {
                                                        onUpdateTrainingGoal(selectedExercise.id, goalOption)
                                                        showGoalDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Last Logged vs Next Target Metrics
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Previous Log
                                    Column {
                                        Text(
                                            text = "last logged",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        if (progression.lastTopWeight > 0) {
                                            val displayLastWeight = if (isKgUnit) "${(progression.lastTopWeight * 0.45359237).toInt()} kg" else "${progression.lastTopWeight.toInt()} lbs"
                                            Text(
                                                text = "$displayLastWeight × ${progression.lastTopReps} reps",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimaryDark
                                            )
                                            Text(
                                                text = "${progression.lastSets.size} sets recorded",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = RoyalBlue300
                                            )
                                        } else {
                                            Text(
                                                text = "no history yet",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextMutedDark
                                            )
                                        }
                                    }

                                    // Progression Indicator Icon
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                if (progression.isReadyToOverload) EmeraldSuccess.copy(alpha = 0.2f) else ElectricRoyalBlue.copy(alpha = 0.2f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = if (progression.isReadyToOverload) EmeraldSuccess else ElectricCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Next Target
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "next target",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ElectricCyan
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val displayTargetWeight = if (isKgUnit) "${(progression.suggestedWeight * 0.45359237).toInt()} kg" else "${progression.suggestedWeight.toInt()} lbs"
                                        Text(
                                            text = displayTargetWeight,
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = ElectricCyan
                                        )
                                        Text(
                                            text = progression.suggestedReps.lowercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (progression.isReadyToOverload) EmeraldSuccess else RoyalBlue200
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Trainer Notes / Trajectory Hints with Distinguishable Lighter Background
                                if (progression.hints.isNotEmpty()) {
                                    val safeHintIndex = currentHintIndex.coerceIn(0, progression.hints.lastIndex)
                                    val hint = progression.hints[safeHintIndex]

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = DarkSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue700)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = ElectricRoyalBlue.copy(alpha = 0.4f)
                                                    ) {
                                                        Text(
                                                            text = hint.tag.lowercase(),
                                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                            color = ElectricCyan,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = hint.title.lowercase(),
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = TextPrimaryDark
                                                    )
                                                }

                                                // Hints Carousel Arrows
                                                if (progression.hints.size > 1) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        IconButton(
                                                            onClick = {
                                                                currentHintIndex = if (currentHintIndex > 0) currentHintIndex - 1 else progression.hints.lastIndex
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(Icons.Default.ChevronLeft, contentDescription = "previous hint", tint = RoyalBlue300)
                                                        }
                                                        Text(
                                                            text = "${safeHintIndex + 1}/${progression.hints.size}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = TextMutedDark
                                                        )
                                                        IconButton(
                                                            onClick = {
                                                                currentHintIndex = if (currentHintIndex < progression.hints.lastIndex) currentHintIndex + 1 else 0
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(Icons.Default.ChevronRight, contentDescription = "next hint", tint = RoyalBlue300)
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = hint.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondaryDark
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Action: ${hint.actionSuggestion}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = ElectricCyan
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Action Row with "Apply to routine" that flashes green with "Applied successfully"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (progression.estimated1RM > 0) {
                                        val display1RM = if (isKgUnit) "${(progression.estimated1RM * 0.45359237).toInt()} kg" else "${progression.estimated1RM.toInt()} lbs"
                                        Text(
                                            text = "Est 1RM: $display1RM",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.width(1.dp))
                                    }

                                    val isApplied = appliedSuccessExerciseId == selectedExercise.id
                                    OutlinedButton(
                                        onClick = {
                                            onApplyOverloadTarget(selectedExercise.id, progression.suggestedWeight)
                                            appliedSuccessExerciseId = selectedExercise.id
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isApplied) EmeraldSuccess.copy(alpha = 0.2f) else Color.Transparent,
                                            contentColor = if (isApplied) EmeraldSuccess else ElectricCyan
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isApplied) EmeraldSuccess else DarkCardBorderBlue
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isApplied) Icons.Default.Check else Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (isApplied) EmeraldSuccess else ElectricCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isApplied) "Applied successfully" else "Apply to routine",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Personal Records Spotlight (Hall of Fame) with Direct Tap-to-Edit ---
        if (personalRecords.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionKicker(
                            icon = Icons.Default.EmojiEvents,
                            text = "PERSONAL RECORDS",
                            tint = GoldPR
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hall of fame PRs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(personalRecords) { pr ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkSlateElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPR.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .width(190.dp)
                                    .clickable { editingPr = pr }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = GoldPR,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = GoldPR.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = if (pr.recordType == "Est1RM") "1rm" else "max",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = GoldPR,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    val prValDisplay = if (isKgUnit) "${(pr.value * 0.45359237).toInt()} kg" else "${pr.value.toInt()} lbs"
                                    Text(
                                        text = prValDisplay,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = ElectricCyan
                                    )
                                    Text(
                                        text = pr.exerciseName.lowercase(),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = TextPrimaryDark,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${pr.reps} reps · tap to edit",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMutedDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 5. Body Weight Loss Tracker with Windowed View & Time Period Filters ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        SectionKicker(
                            icon = Icons.Default.MonitorWeight,
                            text = "BODYWEIGHT TRACKING",
                            tint = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Bodyweight trend",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                    }

                    Button(
                        onClick = onOpenWeightLogDialog,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("log_weight_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log weight", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val chartPoints = bodyWeightLogs.map {
                    val valDisplay = if (isKgUnit) it.weightLbs * 0.45359237 else it.weightLbs
                    ChartDataPoint(timestamp = it.dateMillis, value = valDisplay, label = it.note)
                }

                WeightProgressChart(
                    dataPoints = chartPoints,
                    unit = if (isKgUnit) "kg" else "lbs"
                )
            }
        }

        // --- 6. Consolidated Pinned & Top Routines with 'See All' action ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        SectionKicker(
                            icon = Icons.Default.FitnessCenter,
                            text = "ROUTINES & QUICK START",
                            tint = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pinned & top routines",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = onNavigateToRoutinesTab,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("See all", color = ElectricCyan, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        IconButton(
                            onClick = { showCreateRoutineDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add routine", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Routines Display: Pinned first, otherwise sorted by most repeated (usage count)
                val pinnedCustom = remember(customRoutines) { customRoutines.filter { it.isPinned } }
                val unpinnedCustom = remember(customRoutines) { customRoutines.filter { !it.isPinned }.sortedByDescending { it.usageCount } }
                val displayCustomRoutines = remember(pinnedCustom, unpinnedCustom) {
                    (pinnedCustom + unpinnedCustom).take(4)
                }

                if (displayCustomRoutines.isNotEmpty()) {
                    displayCustomRoutines.forEach { cr ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSlateElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (cr.isPinned) ElectricCyan else DarkCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .combinedClickable(
                                    onClick = {
                                        val exIds = cr.exerciseIdsCsv.split(",").mapNotNull { it.trim().toLongOrNull() }
                                        val matched = exercises.filter { exIds.contains(it.id) }
                                        onStartWorkout(cr.name, matched)
                                    },
                                    onLongClick = {
                                        onTogglePinRoutine(cr)
                                    }
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                if (cr.isPinned) ElectricCyan.copy(alpha = 0.2f) else ElectricRoyalBlue.copy(alpha = 0.2f),
                                                RoundedCornerShape(10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (cr.isPinned) Icons.Default.PushPin else Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = if (cr.isPinned) ElectricCyan else ElectricRoyalBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = cr.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimaryDark
                                            )
                                            if (cr.isPinned) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = ElectricCyan.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "PINNED",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                        color = ElectricCyan,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${cr.subtitle} · ${cr.usageCount} times completed",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark
                                        )
                                    }
                                }

                                Icon(Icons.Default.PlayArrow, contentDescription = "Start", tint = ElectricCyan, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Default Quick Start Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RoutineCard(
                        title = "Push Day",
                        subtitle = "Chest, delts & triceps",
                        icon = Icons.Default.FitnessCenter,
                        onClick = {
                            val pushExercises = exercises.filter { it.category == "Chest" || it.category == "Shoulders" }.take(4)
                            onStartWorkout("Push Day - Hypertrophy", pushExercises)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    RoutineCard(
                        title = "Pull Day",
                        subtitle = "Back, biceps & rear delts",
                        icon = Icons.Default.FitnessCenter,
                        onClick = {
                            val pullExercises = exercises.filter { it.category == "Back" || it.category == "Arms" }.take(4)
                            onStartWorkout("Pull Day - Strength", pullExercises)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RoutineCard(
                        title = "Leg Day",
                        subtitle = "Quads, hamstrings & glutes",
                        icon = Icons.Default.FitnessCenter,
                        onClick = {
                            val legExercises = exercises.filter { it.category == "Legs" }.take(4)
                            onStartWorkout("Leg Day - Power", legExercises)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    RoutineCard(
                        title = "Custom Session",
                        subtitle = "Select any movements",
                        icon = Icons.Default.PlayArrow,
                        onClick = {
                            onStartWorkout("Custom Workout", emptyList())
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // --- 7. Embedded Scrollable Recent Workout Sessions Container ---
        if (recentSessions.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionKicker(icon = Icons.Default.History, text = "RECENT SESSIONS", tint = ElectricCyan)
                        Text(
                            text = "${recentSessions.size} logged",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Workout activity log",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fixed-height embedded scrollable view so it doesn't shift the entire page
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSlateElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorderBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 210.dp)
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(recentSessions.take(8)) { session ->
                                val sessionDateFormat = SimpleDateFormat("MMM d · h:mm a", Locale.getDefault())
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = RoyalBlue900,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = session.name,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimaryDark
                                            )
                                            Text(
                                                text = sessionDateFormat.format(Date(session.dateMillis)),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMutedDark
                                            )
                                        }

                                        val volDisplay = if (isKgUnit) "${(session.totalVolumeLbs * 0.45359237).toInt()} kg" else "${session.totalVolumeLbs.toInt()} lbs"
                                        Text(
                                            text = "${session.durationMinutes}m · $volDisplay",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ElectricCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Exercise Detail Sheet Dialog
    if (selectedExerciseForDetail != null) {
        val ex = selectedExerciseForDetail!!
        ExerciseDetailSheet(
            exercise = ex,
            progression = progressionMap[ex.id],
            prs = personalRecords.filter { it.exerciseId == ex.id },
            aiGuides = aiGuides,
            isKgUnit = isKgUnit,
            onDismiss = { selectedExerciseForDetail = null },
            onGenerateAiGuide = onGenerateAiGuide,
            onStartWorkoutWithExercise = { exercise ->
                selectedExerciseForDetail = null
                onStartWorkout("custom workout", listOf(exercise))
            }
        )
    }

    // Edit Personal Record Dialog
    if (editingPr != null) {
        val pr = editingPr!!
        var weightInput by remember { mutableStateOf(if (isKgUnit) "${(pr.value * 0.45359237).toInt()}" else "${pr.value.toInt()}") }
        var repsInput by remember { mutableStateOf("${pr.reps}") }

        AlertDialog(
            onDismissRequest = { editingPr = null },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "edit pr · ${pr.exerciseName.lowercase()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("weight (${if (isKgUnit) "kg" else "lbs"})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricRoyalBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = repsInput,
                        onValueChange = { repsInput = it },
                        label = { Text("reps achieved") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricRoyalBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedWeight = weightInput.toDoubleOrNull()
                        val parsedReps = repsInput.toIntOrNull() ?: 1
                        if (parsedWeight != null) {
                            val standardWeightLbs = if (isKgUnit) parsedWeight / 0.45359237 else parsedWeight
                            onUpdatePR(pr, standardWeightLbs, parsedReps)
                            editingPr = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue)
                ) {
                    Text("save pr")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            onDeletePR(pr)
                            editingPr = null
                        }
                    ) {
                        Text("delete", color = Color(0xFFEF4444))
                    }
                    TextButton(onClick = { editingPr = null }) {
                        Text("cancel", color = TextMutedDark)
                    }
                }
            }
        )
    }

    // Day Inspection Dialog (Tap to schedule or view routine)
    if (selectedDayForInspection != null) {
        val targetDay = selectedDayForInspection!!
        AlertDialog(
            onDismissRequest = { selectedDayForInspection = null },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "${targetDay.fullName} schedule",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (targetDay.plannedWorkouts.isEmpty()) {
                        Text(
                            text = "no workouts scheduled for ${targetDay.fullName}. would you like to schedule one?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                    } else {
                        Text(
                            text = "scheduled routines for ${targetDay.fullName}:",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        targetDay.plannedWorkouts.forEach { pw ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSlateElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = pw.routineName.lowercase(), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                                    Text(text = pw.focusDescription.lowercase(), style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                    if (pw.targetNotes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = pw.targetNotes.lowercase(), style = MaterialTheme.typography.bodySmall, color = RoyalBlue300)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (targetDay.plannedWorkouts.isEmpty()) {
                    Button(
                        onClick = {
                            selectedDayForInspection = null
                            showScheduleDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue)
                    ) {
                        Text("schedule now")
                    }
                } else {
                    Button(
                        onClick = {
                            val firstP = targetDay.plannedWorkouts.first()
                            val exIds = firstP.exerciseIdsCsv.split(",").mapNotNull { it.trim().toLongOrNull() }
                            val matched = exercises.filter { exIds.contains(it.id) }
                            selectedDayForInspection = null
                            onStartWorkout(firstP.routineName, matched)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue)
                    ) {
                        Text("start workout")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDayForInspection = null }) {
                    Text("close", color = TextMutedDark)
                }
            }
        )
    }

    // Dialogs
    if (showExerciseSelectorDialog) {
        ExerciseSelectorSheetDialog(
            exercises = exercises,
            progressionMap = progressionMap,
            selectedExerciseId = selectedExerciseId,
            onDismiss = { showExerciseSelectorDialog = false },
            onSelectExercise = { selectedExerciseId = it }
        )
    }

    if (showScheduleDialog) {
        ScheduleWorkoutDialog(
            exercises = exercises,
            customRoutines = customRoutines,
            onDismiss = { showScheduleDialog = false },
            onSchedule = onScheduleWorkout
        )
    }

    if (showCreateRoutineDialog) {
        CreateRoutineDialog(
            exercises = exercises,
            onDismiss = { showCreateRoutineDialog = false },
            onCreate = onCreateCustomRoutine
        )
    }
}

data class WeeklyDayState(
    val name: String,
    val fullName: String,
    val dayNumber: Int,
    val isToday: Boolean,
    val hasCompleted: Boolean,
    val hasPlanned: Boolean,
    val plannedWorkouts: List<PlannedWorkoutEntity> = emptyList()
)

@Composable
fun SectionKicker(
    icon: ImageVector,
    text: String,
    tint: Color = ElectricCyan,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = tint
        )
    }
}

@Composable
fun RoutineCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSlateElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(ElectricRoyalBlue.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = ElectricRoyalBlue, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextMutedDark, maxLines = 1)
        }
    }
}

private fun getDayName(dayInt: Int): String {
    return when (dayInt) {
        2 -> "Monday"
        3 -> "Tuesday"
        4 -> "Wednesday"
        5 -> "Thursday"
        6 -> "Friday"
        7 -> "Saturday"
        else -> "Sunday"
    }
}
