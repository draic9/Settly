package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomRoutineEntity
import com.example.data.model.ExerciseEntity
import com.example.data.model.PlannedWorkoutEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.ui.components.ConsistencyCalendarView
import com.example.ui.components.ScheduleWorkoutDialog
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardBorderBlue
import com.example.ui.theme.DarkSlateElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricRoyalBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldPR
import com.example.ui.theme.RoyalBlue300
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarAnalyticsScreen(
    workoutSessions: List<WorkoutSessionEntity>,
    plannedWorkouts: List<PlannedWorkoutEntity>,
    customRoutines: List<CustomRoutineEntity>,
    exercises: List<ExerciseEntity>,
    isKgUnit: Boolean,
    onScheduleWorkout: (String, Int, Long, String, String, String) -> Unit,
    onTogglePlannedCompleted: (PlannedWorkoutEntity) -> Unit,
    onDeletePlannedWorkout: (PlannedWorkoutEntity) -> Unit,
    onStartWorkout: (String, List<ExerciseEntity>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showScheduleDialogForDate by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()) }

    val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
    val selectedEpochDay = selectedCal.get(Calendar.YEAR) * 366 + selectedCal.get(Calendar.DAY_OF_YEAR)

    val dayCompletedWorkouts = workoutSessions.filter {
        val c = Calendar.getInstance().apply { timeInMillis = it.dateMillis }
        c.get(Calendar.YEAR) * 366 + c.get(Calendar.DAY_OF_YEAR) == selectedEpochDay
    }

    val dayPlannedWorkouts = plannedWorkouts.filter {
        val c = Calendar.getInstance().apply { timeInMillis = it.dateMillis }
        c.get(Calendar.YEAR) * 366 + c.get(Calendar.DAY_OF_YEAR) == selectedEpochDay
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Consistency Calendar Widget
        item {
            ConsistencyCalendarView(
                workoutSessions = workoutSessions,
                onSelectDate = { selectedDateMillis = it }
            )
        }

        // 2. Selected Date Header & Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    SectionKicker(icon = Icons.Default.CalendarMonth, text = "SELECTED DATE", tint = ElectricCyan)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateFormat.format(Date(selectedDateMillis)),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                }

                Button(
                    onClick = { showScheduleDialogForDate = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Schedule", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // 3. Completed Workouts for Selected Day
        if (dayCompletedWorkouts.isNotEmpty()) {
            item {
                Text(
                    text = "Completed workouts (${dayCompletedWorkouts.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldSuccess
                )
            }
            items(dayCompletedWorkouts) { session ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSlateElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(EmeraldSuccess.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(session.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                                val volDisplay = if (isKgUnit) "${(session.totalVolumeLbs * 0.45359237).toInt()} kg" else "${session.totalVolumeLbs.toInt()} lbs"
                                Text(
                                    "${session.durationMinutes} mins · ${session.totalSets} sets · $volDisplay volume",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Planned Workouts for Selected Day
        if (dayPlannedWorkouts.isNotEmpty()) {
            item {
                Text(
                    text = "Scheduled routine",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = ElectricCyan
                )
            }
            items(dayPlannedWorkouts) { planned ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSlateElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorderBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(planned.routineName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                                Text(planned.focusDescription, style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                if (planned.targetNotes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Focus: ${planned.targetNotes}", style = MaterialTheme.typography.labelSmall, color = ElectricCyan)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = {
                                        val ids = planned.exerciseIdsCsv.split(",").mapNotNull { it.trim().toLongOrNull() }
                                        val matched = exercises.filter { ids.contains(it.id) }
                                        onStartWorkout(planned.routineName, matched)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Start", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }

                                IconButton(onClick = { onDeletePlannedWorkout(planned) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMutedDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (dayCompletedWorkouts.isEmpty() && dayPlannedWorkouts.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSlateElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No workout recorded or scheduled for this date.", style = MaterialTheme.typography.bodySmall, color = TextMutedDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { showScheduleDialogForDate = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricRoyalBlue)
                        ) {
                            Text("Plan workout for this day")
                        }
                    }
                }
            }
        }

        // 5. Recent Workout History Section (Scrollable list with clean styling)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionKicker(icon = Icons.Default.History, text = "RECENT TRAINING SESSIONS", tint = ElectricCyan)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Workout history log",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        }

        if (workoutSessions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSlateElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No workout sessions logged yet. Start a workout from the routines tab or dashboard.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedDark,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(workoutSessions.take(10)) { session ->
                val sessionDateFormat = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSlateElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(session.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                            Text(
                                sessionDateFormat.format(Date(session.dateMillis)),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark
                            )
                            val volDisplay = if (isKgUnit) "${(session.totalVolumeLbs * 0.45359237).toInt()} kg" else "${session.totalVolumeLbs.toInt()} lbs"
                            Text(
                                "${session.durationMinutes} mins · ${session.totalSets} sets · $volDisplay",
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan
                            )
                        }

                        if (session.completed) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Completed",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldSuccess,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showScheduleDialogForDate) {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        ScheduleWorkoutDialog(
            initialDayOfWeek = dayOfWeek,
            exercises = exercises,
            customRoutines = customRoutines,
            onDismiss = { showScheduleDialogForDate = false },
            onSchedule = { name, dOfWeek, _, exCsv, focus, notes ->
                onScheduleWorkout(name, dOfWeek, selectedDateMillis, exCsv, focus, notes)
            }
        )
    }
}
