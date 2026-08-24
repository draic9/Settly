package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomRoutineEntity
import com.example.data.model.ExerciseEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.data.repository.ExerciseProgressionSuggestion
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
import com.example.ui.theme.RoyalBlue100
import com.example.ui.theme.RoyalBlue200
import com.example.ui.theme.RoyalBlue300
import com.example.ui.theme.RoyalBlue400
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.RoyalBlue800
import com.example.ui.theme.RoyalBlue900
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun RestTimerDialog(
    initialSeconds: Int = 90,
    onDismiss: () -> Unit
) {
    var totalSeconds by remember { mutableIntStateOf(initialSeconds) }
    var secondsLeft by remember { mutableIntStateOf(initialSeconds) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
    }

    val progress = if (totalSeconds > 0) secondsLeft.toFloat() / totalSeconds.toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "timerProgress")

    val minutes = secondsLeft / 60
    val secs = secondsLeft % 60
    val formattedTime = String.format("%02d:%02d", minutes, secs)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = ElectricRoyalBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rest Interval",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(150.dp),
                        color = ElectricRoyalBlue,
                        trackColor = RoyalBlue800,
                        strokeWidth = 10.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                            color = TextPrimaryDark
                        )
                        Text(
                            text = if (secondsLeft == 0) "Rest Complete!" else "Rest & Recover",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (secondsLeft == 0) EmeraldSuccess else RoyalBlue300
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30, 60, 90, 120, 180).forEach { sec ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (totalSeconds == sec) ElectricRoyalBlue else RoyalBlue800,
                            modifier = Modifier.clickable {
                                totalSeconds = sec
                                secondsLeft = sec
                                isRunning = true
                            }
                        ) {
                            Text(
                                text = "${sec}s",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (totalSeconds == sec) Color.White else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) RoyalBlue700 else ElectricRoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isRunning) "Pause" else "Resume")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    secondsLeft += 30
                    totalSeconds += 30
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+30s")
            }
        }
    )
}

@Composable
fun LogWeightDialog(
    initialWeight: Double = 185.0,
    isKgUnit: Boolean = false,
    onDismiss: () -> Unit,
    onSaveWeight: (Double, String) -> Unit
) {
    val displayInitial = if (isKgUnit) initialWeight * 0.45359237 else initialWeight
    val formattedInitial = if (displayInitial > 0) String.format(Locale.US, "%.1f", displayInitial) else if (isKgUnit) "80.0" else "185.0"
    var weightText by remember { mutableStateOf(formattedInitial) }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(ElectricRoyalBlue.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.MonitorWeight, contentDescription = null, tint = ElectricRoyalBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Log Body Weight", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
            }
        },
        text = {
            Column {
                Text("Track your weight loss progress over time.", style = MaterialTheme.typography.bodySmall, color = TextMutedDark)
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (${if (isKgUnit) "kg" else "lbs"})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricRoyalBlue,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedLabelColor = ElectricRoyalBlue,
                        unfocusedLabelColor = TextMutedDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weight_input_field")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note (e.g. Morning fasted)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricRoyalBlue,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedLabelColor = ElectricRoyalBlue,
                        unfocusedLabelColor = TextMutedDark,
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
                    val w = weightText.toDoubleOrNull()
                    if (w != null && w > 0) {
                        val weightInLbs = if (isKgUnit) w / 0.45359237 else w
                        onSaveWeight(weightInLbs, noteText)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_log_weight_button")
            ) {
                Text("Save Weight")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMutedDark)
            }
        }
    )
}

@Composable
fun ScheduleWorkoutDialog(
    initialDayOfWeek: Int = 2, // 1 = Sun, 2 = Mon ...
    exercises: List<ExerciseEntity>,
    customRoutines: List<CustomRoutineEntity>,
    onDismiss: () -> Unit,
    onSchedule: (routineName: String, dayOfWeek: Int, dateMillis: Long, exerciseIdsCsv: String, focus: String, notes: String) -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(initialDayOfWeek) }
    var selectedRoutineName by remember { mutableStateOf("Push Day - Power") }
    var focusText by remember { mutableStateOf("Chest, Front Delts & Triceps") }
    var targetNotes by remember { mutableStateOf("Progressive Overload Target") }

    val daysOfWeek = listOf(
        Pair(2, "Mon"),
        Pair(3, "Tue"),
        Pair(4, "Wed"),
        Pair(5, "Thu"),
        Pair(6, "Fri"),
        Pair(7, "Sat"),
        Pair(1, "Sun")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(ElectricRoyalBlue.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = ElectricRoyalBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Schedule Weekly Workout", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Select target day of the week:", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEach { (dInt, dName) ->
                        val isSelected = selectedDay == dInt
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) ElectricRoyalBlue else RoyalBlue800,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricCyan else DarkCardBorder),
                            modifier = Modifier.clickable { selectedDay = dInt }
                        ) {
                            Text(
                                text = dName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Choose Routine / Split:", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                Spacer(modifier = Modifier.height(6.dp))

                val presetRoutines = listOf("Push Day - Power", "Pull Day - Strength", "Leg Day - Hypertrophy", "Upper Specialization", "Custom Routine")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    presetRoutines.take(3).forEach { routine ->
                        val isSel = selectedRoutineName == routine
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) RoyalBlue700 else RoyalBlue900,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ElectricRoyalBlue else DarkCardBorder),
                            modifier = Modifier.clickable {
                                selectedRoutineName = routine
                                focusText = when (routine) {
                                    "Push Day - Power" -> "Chest, Delts & Triceps"
                                    "Pull Day - Strength" -> "Back, Lats & Biceps"
                                    else -> "Quads, Hamstrings & Glutes"
                                }
                            }
                        ) {
                            Text(
                                text = routine.split(" - ").first(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (isSel) Color.White else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = selectedRoutineName,
                    onValueChange = { selectedRoutineName = it },
                    label = { Text("Routine Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricRoyalBlue,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = focusText,
                    onValueChange = { focusText = it },
                    label = { Text("Target Focus / Muscles") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricRoyalBlue,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = targetNotes,
                    onValueChange = { targetNotes = it },
                    label = { Text("Target Notes & Overload Cue") },
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
                    val cal = Calendar.getInstance()
                    val curDay = cal.get(Calendar.DAY_OF_WEEK)
                    var offset = selectedDay - curDay
                    if (offset < 0) offset += 7
                    cal.add(Calendar.DAY_OF_YEAR, offset)

                    onSchedule(selectedRoutineName, selectedDay, cal.timeInMillis, "1,2,7", focusText, targetNotes)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMutedDark)
            }
        }
    )
}

@Composable
fun CreateRoutineDialog(
    exercises: List<ExerciseEntity>,
    onDismiss: () -> Unit,
    onCreate: (name: String, subtitle: String, category: String, exerciseIdsCsv: String) -> Unit,
    onAddCustomExercise: (ExerciseEntity) -> Unit = {}
) {
    var routineName by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Push") }
    val selectedExerciseIds = remember { mutableStateOf(mutableSetOf<Long>()) }

    var showExercisePicker by remember { mutableStateOf(false) }
    var showAddCustomExDialog by remember { mutableStateOf(false) }
    var showDiscardConfirmDialog by remember { mutableStateOf(false) }

    if (showDiscardConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmDialog = false },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Discard Routine?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            },
            text = {
                Text(
                    text = "You have unsaved changes. Progress cannot be resumed if you discard this routine.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscardConfirmDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Discard Routine")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirmDialog = false }) {
                    Text("Keep Editing", color = TextMutedDark)
                }
            }
        )
    }

    if (showAddCustomExDialog) {
        AddCustomExerciseDialog(
            onDismiss = { showAddCustomExDialog = false },
            onAddExercise = { newEx ->
                onAddCustomExercise(newEx)
                showAddCustomExDialog = false
            }
        )
    }

    if (showExercisePicker) {
        ExercisePickerMultiSelectDialog(
            exercises = exercises,
            selectedIds = selectedExerciseIds.value,
            onDismiss = { showExercisePicker = false },
            onToggleExercise = { exId ->
                val newSet = selectedExerciseIds.value.toMutableSet()
                if (newSet.contains(exId)) newSet.remove(exId) else newSet.add(exId)
                selectedExerciseIds.value = newSet
            },
            onOpenCreateCustomExercise = {
                showAddCustomExDialog = true
            }
        )
    }

    Dialog(
        onDismissRequest = {
            if (routineName.isNotBlank() || selectedExerciseIds.value.isNotEmpty()) {
                showDiscardConfirmDialog = true
            } else {
                onDismiss()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            if (routineName.isNotBlank() || selectedExerciseIds.value.isNotEmpty()) {
                                showDiscardConfirmDialog = true
                            } else {
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Discard", color = Color(0xFFEF4444), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }

                    Text(
                        text = "NEW ROUTINE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = TextPrimaryDark
                    )

                    Button(
                        onClick = {
                            if (routineName.isNotBlank()) {
                                val csv = selectedExerciseIds.value.joinToString(",")
                                onCreate(routineName, subtitle.ifBlank { selectedCategory }, selectedCategory, csv)
                                onDismiss()
                            }
                        },
                        enabled = routineName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Routine")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = routineName,
                            onValueChange = { routineName = it },
                            label = { Text("Routine Name (e.g. Push Hypertrophy)") },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricRoyalBlue,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = subtitle,
                            onValueChange = { subtitle = it },
                            label = { Text("Target Focus (e.g. Chest & Triceps)") },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricRoyalBlue,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Column {
                            Text("Split Category", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextMutedDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Push", "Pull", "Legs", "Upper", "Full Body").forEach { cat ->
                                    val isSel = selectedCategory == cat
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSel) ElectricRoyalBlue else RoyalBlue800,
                                        modifier = Modifier.clickable { selectedCategory = cat }
                                    ) {
                                        Text(
                                            text = cat,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSel) Color.White else TextSecondaryDark,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Included Exercises (${selectedExerciseIds.value.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action card to add/browse exercises
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = RoyalBlue900,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showExercisePicker = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(ElectricRoyalBlue, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Add exercises...",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimaryDark
                                    )
                                    Text(
                                        text = "Browse complete library or create custom movements",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMutedDark
                                    )
                                }
                            }
                        }
                    }

                    if (selectedExerciseIds.value.isNotEmpty()) {
                        val selectedList = exercises.filter { selectedExerciseIds.value.contains(it.id) }
                        items(selectedList) { ex ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = RoyalBlue800,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorderBlue),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(ex.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimaryDark)
                                        Text("${ex.category} · ${ex.equipment}", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                    }
                                    IconButton(
                                        onClick = {
                                            val newSet = selectedExerciseIds.value.toMutableSet()
                                            newSet.remove(ex.id)
                                            selectedExerciseIds.value = newSet
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = TextMutedDark, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExercisePickerMultiSelectDialog(
    exercises: List<ExerciseEntity>,
    selectedIds: Set<Long>,
    onDismiss: () -> Unit,
    onToggleExercise: (Long) -> Unit,
    onOpenCreateCustomExercise: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredList = remember(exercises, searchQuery, selectedFilter) {
        exercises.filter { ex ->
            val matchesQuery = ex.name.contains(searchQuery, ignoreCase = true) || ex.category.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter.lowercase()) {
                "chest" -> ex.category.equals("Chest", true)
                "back" -> ex.category.equals("Back", true)
                "legs" -> ex.category.equals("Legs", true)
                "shoulders" -> ex.category.equals("Shoulders", true)
                "arms" -> ex.category.equals("Arms", true)
                "core" -> ex.category.equals("Core", true)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Select Exercises", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search movement...", color = TextMutedDark, style = MaterialTheme.typography.bodySmall) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricRoyalBlue,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core").forEach { tag ->
                        val isSel = selectedFilter == tag
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) ElectricRoyalBlue else RoyalBlue800,
                            modifier = Modifier.clickable { selectedFilter = tag }
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                color = if (isSel) Color.White else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onOpenCreateCustomExercise,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue800),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create New Exercise Movement", color = ElectricCyan, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredList) { ex ->
                        val isSelected = selectedIds.contains(ex.id)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) RoyalBlue700 else RoyalBlue900,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricCyan else DarkCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleExercise(ex.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { onToggleExercise(ex.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = ElectricRoyalBlue,
                                        uncheckedColor = TextMutedDark
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(ex.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = TextPrimaryDark)
                                    Text("${ex.category} · ${ex.equipment}", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done (${selectedIds.size} Selected)")
            }
        }
    )
}

@Composable
fun SetTypeInfoDialog(
    currentType: String,
    onDismiss: () -> Unit,
    onSelectType: (String) -> Unit
) {
    val types = listOf(
        Triple("R", "Regular (Working Set)", "Standard working set with prescribed target reps and weight load to stimulate strength & hypertrophy."),
        Triple("W", "Warmup Set", "Light preparation set to prime joints, activate motor units, and groove biomechanical form without fatigue."),
        Triple("D", "Drop Set", "Perform reps to technical failure, then immediately reduce load by 20-30% without resting to extend time under tension."),
        Triple("S", "Super Set", "Pair two opposing or complementary movements executed back-to-back with minimal transitional rest.")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(ElectricRoyalBlue.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = ElectricRoyalBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Select Set Type",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                types.forEach { (code, name, desc) ->
                    val isSelected = currentType.equals(code, ignoreCase = true)
                    val tagColor = when (code) {
                        "W" -> Color(0xFFFF9800)
                        "D" -> Color(0xFFAB47BC)
                        "S" -> EmeraldSuccess
                        else -> ElectricRoyalBlue
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) RoyalBlue800 else RoyalBlue900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) tagColor else DarkCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectType(code)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(tagColor.copy(alpha = 0.2f), CircleShape)
                                    .border(1.5.dp, tagColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = code,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = tagColor
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimaryDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
                                    color = TextMutedDark
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextMutedDark)
            }
        }
    )
}

@Composable
fun WorkoutSessionSummaryDialog(
    session: WorkoutSessionEntity,
    isKgUnit: Boolean = false,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d, yyyy · h:mm a", Locale.getDefault()) }
    val formattedDate = remember(session.dateMillis) { dateFormat.format(Date(session.dateMillis)) }
    val displayVolume = if (isKgUnit) session.totalVolumeLbs * 0.45359237 else session.totalVolumeLbs
    val unitLabel = if (isKgUnit) "kg" else "lbs"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(ElectricRoyalBlue.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = ElectricRoyalBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = session.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Key metrics row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = RoyalBlue900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Duration",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${session.durationMinutes} min",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = RoyalBlue900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Volume",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${displayVolume.toInt()} $unitLabel",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ElectricCyan
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = RoyalBlue900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Sets",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${session.totalSets}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                        }
                    }
                }

                if (session.notes.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RoyalBlue900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Session Notes",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue300
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = session.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimaryDark
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DiscardEmptyWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirmDiscard: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Empty Workout",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        },
        text = {
            Text(
                text = "No completed sets were recorded in this workout. Finishing now will discard the session.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDiscard,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Discard Session")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Logging", color = TextMutedDark)
            }
        }
    )
}

@Composable
fun IncompleteWorkoutConfirmDialog(
    uncompletedCount: Int,
    onDismiss: () -> Unit,
    onConfirmFinish: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Uncompleted Sets Remaining",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        },
        text = {
            Text(
                text = "You still have $uncompletedCount uncompleted sets. Are you sure you are done? The log will be trimmed to save only your completed sets.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmFinish,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Finish & Save Completed")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Resume Workout", color = TextMutedDark)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseSelectorSheetDialog(
    exercises: List<ExerciseEntity>,
    progressionMap: Map<Long, ExerciseProgressionSuggestion>,
    selectedExerciseId: Long,
    onDismiss: () -> Unit,
    onSelectExercise: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") } // all, ready to overload, needs attention, most executed, chest, back, legs, shoulders, arms

    val filteredList = remember(exercises, searchQuery, selectedFilter, progressionMap) {
        exercises.filter { ex ->
            val matchesQuery = ex.name.contains(searchQuery, ignoreCase = true) || ex.category.contains(searchQuery, ignoreCase = true)
            val prog = progressionMap[ex.id]

            val matchesFilter = when (selectedFilter.lowercase()) {
                "ready to overload" -> prog?.isReadyToOverload == true
                "needs attention" -> prog?.isStagnant == true
                "most executed" -> (prog?.executionCount ?: 0) > 0
                "chest" -> ex.category.equals("Chest", true)
                "back" -> ex.category.equals("Back", true)
                "legs" -> ex.category.equals("Legs", true)
                "shoulders" -> ex.category.equals("Shoulders", true)
                "arms" -> ex.category.equals("Arms", true)
                else -> true
            }

            matchesQuery && matchesFilter
        }.sortedWith(
            compareByDescending<ExerciseEntity> { progressionMap[it.id]?.isReadyToOverload == true }
                .thenByDescending { progressionMap[it.id]?.isStagnant == true }
                .thenByDescending { progressionMap[it.id]?.executionCount ?: 0 }
                .thenBy { it.name }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(ElectricRoyalBlue.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = ElectricRoyalBlue, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("select overload exercise", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "close", tint = TextMutedDark)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Search Bar - standard body size
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("search movement or machine...", color = TextMutedDark, style = MaterialTheme.typography.bodySmall) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricRoyalBlue,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("all", "ready to overload", "needs attention", "chest", "back", "legs", "shoulders", "arms").forEach { tag ->
                        val isSel = selectedFilter == tag
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) ElectricRoyalBlue else RoyalBlue800,
                            modifier = Modifier.clickable { selectedFilter = tag }
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                color = if (isSel) Color.White else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList) { ex ->
                        val isSelected = ex.id == selectedExerciseId
                        val prog = progressionMap[ex.id]

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) RoyalBlue700 else RoyalBlue900,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when {
                                    isSelected -> ElectricCyan
                                    prog?.isReadyToOverload == true -> EmeraldSuccess
                                    prog?.isStagnant == true -> GoldPR
                                    else -> DarkCardBorder
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectExercise(ex.id)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = ex.name.lowercase(),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimaryDark
                                        )
                                        if (prog?.isReadyToOverload == true) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = EmeraldSuccess.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "+ overload",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                    color = EmeraldSuccess,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        if (prog?.isStagnant == true) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GoldPR.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "plateau",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                    color = GoldPR,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${ex.category.lowercase()} · ${ex.equipment.lowercase()} · ${ex.trainingGoal.lowercase()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMutedDark
                                    )
                                }

                                if (prog != null && prog.lastTopWeight > 0) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${prog.lastTopWeight.toInt()} lbs",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ElectricRoyalBlue
                                        )
                                        Text(
                                            text = "next: ${prog.suggestedWeight.toInt()} lbs",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EmeraldSuccess
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("close", color = TextMutedDark)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddCustomExerciseDialog(
    onDismiss: () -> Unit,
    onAddExercise: (ExerciseEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Chest") }
    var equipment by remember { mutableStateOf("Barbell") }
    var primaryMuscles by remember { mutableStateOf("Upper & Mid Pecs") }
    var goal by remember { mutableStateOf("Hypertrophy") }

    val categories = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Full Body")
    val equipmentOptions = listOf("Barbell", "Dumbbell", "Cable", "Machine", "Bodyweight", "Smith Machine", "Kettlebell")
    val goalOptions = listOf("Hypertrophy", "Strength", "Power", "Endurance")

    val suggestedMusclesMap = mapOf(
        "Chest" to listOf("Upper Pecs", "Mid / Lower Pecs", "Inner Pecs", "Anterior Delts & Chest"),
        "Back" to listOf("Lats", "Upper Back / Rhomboids", "Lower Back (Erectors)", "Traps"),
        "Legs" to listOf("Quads", "Hamstrings", "Glutes", "Calves", "Adductors"),
        "Shoulders" to listOf("Lateral Delts", "Anterior Delts", "Rear Delts", "Rotator Cuff"),
        "Arms" to listOf("Biceps (Short/Long Head)", "Triceps (Lateral/Long Head)", "Forearms / Brachialis"),
        "Core" to listOf("Rectus Abdominis", "Obliques", "Transverse Abdominis / Core"),
        "Full Body" to listOf("Posterior Chain", "Full Body Compound", "Cardiovascular & Core")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(ElectricRoyalBlue.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Add Custom Exercise",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Exercise Name (e.g. Incline DB Press)") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricRoyalBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Column {
                        Text("Target Muscle Group", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue300)
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSel = category == cat
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) ElectricRoyalBlue else RoyalBlue800,
                                    modifier = Modifier.clickable {
                                        category = cat
                                        primaryMuscles = suggestedMusclesMap[cat]?.firstOrNull() ?: cat
                                    }
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                        color = if (isSel) Color.White else TextSecondaryDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text("Primary Trained Muscle", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue300)
                        Spacer(modifier = Modifier.height(4.dp))
                        val suggestions = suggestedMusclesMap[category] ?: listOf(category)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            suggestions.forEach { muscle ->
                                val isSel = primaryMuscles == muscle
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSel) ElectricCyan.copy(alpha = 0.25f) else RoyalBlue900,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ElectricCyan else DarkCardBorder),
                                    modifier = Modifier.clickable { primaryMuscles = muscle }
                                ) {
                                    Text(
                                        text = muscle,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                        color = if (isSel) ElectricCyan else TextSecondaryDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = primaryMuscles,
                            onValueChange = { primaryMuscles = it },
                            placeholder = { Text("Specific muscle detail...") },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricRoyalBlue,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Column {
                        Text("Equipment", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue300)
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            equipmentOptions.forEach { eq ->
                                val isSel = equipment == eq
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) ElectricRoyalBlue else RoyalBlue800,
                                    modifier = Modifier.clickable { equipment = eq }
                                ) {
                                    Text(
                                        text = eq,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                        color = if (isSel) Color.White else TextSecondaryDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text("Training Goal", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue300)
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            goalOptions.forEach { g ->
                                val isSel = goal == g
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) ElectricRoyalBlue else RoyalBlue800,
                                    modifier = Modifier.clickable { goal = g }
                                ) {
                                    Text(
                                        text = g,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                        color = if (isSel) Color.White else TextSecondaryDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val newEx = ExerciseEntity(
                            name = name.trim(),
                            category = category.trim().ifBlank { "Full Body" },
                            equipment = equipment.trim().ifBlank { "Barbell" },
                            primaryMuscles = primaryMuscles.trim().ifBlank { category.trim() },
                            secondaryMuscles = "",
                            defaultRestSeconds = 90,
                            notes = "Custom exercise added by user",
                            isCustom = true,
                            trainingGoal = goal
                        )
                        onAddExercise(newEx)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Movement")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMutedDark)
            }
        }
    )
}

@Composable
fun LockerSettingsDialog(
    isKgUnit: Boolean,
    personalRecords: List<PersonalRecordEntity>,
    onToggleUnit: () -> Unit,
    onUpdatePR: (PersonalRecordEntity, Double, Int) -> Unit,
    onDeletePR: (PersonalRecordEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var editingRecord by remember { mutableStateOf<PersonalRecordEntity?>(null) }
    var editValueText by remember { mutableStateOf("") }
    var editRepsText by remember { mutableStateOf("") }

    if (editingRecord != null) {
        AlertDialog(
            onDismissRequest = { editingRecord = null },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Edit personal record",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = editingRecord?.exerciseName ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ElectricCyan
                    )
                    OutlinedTextField(
                        value = editValueText,
                        onValueChange = { editValueText = it },
                        label = { Text("Weight (${if (isKgUnit) "kg" else "lbs"})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                        value = editRepsText,
                        onValueChange = { editRepsText = it },
                        label = { Text("Reps") },
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
                        val v = editValueText.toDoubleOrNull() ?: 0.0
                        val rawV = if (isKgUnit) v / 0.45359237 else v
                        val r = editRepsText.toIntOrNull() ?: 1
                        editingRecord?.let { onUpdatePR(it, rawV, r) }
                        editingRecord = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue)
                ) {
                    Text("Save changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingRecord = null }) {
                    Text("Cancel", color = TextMutedDark)
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Locker & settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Unit preference toggle
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = RoyalBlue900,
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
                            Text(
                                text = "WEIGHT UNITS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                                color = RoyalBlue300
                            )
                            Text(
                                text = if (isKgUnit) "Kilograms (kg)" else "Pounds (lbs)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                        }
                        Button(
                            onClick = onToggleUnit,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isKgUnit) "Switch to LBS" else "Switch to KG")
                        }
                    }
                }

                // Personal Records List
                Text(
                    text = "PERSONAL RECORDS (${personalRecords.size})",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                    color = RoyalBlue300
                )

                if (personalRecords.isEmpty()) {
                    Text(
                        text = "No personal records recorded yet. Complete workout sets to automatically earn PRs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedDark
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(personalRecords) { pr ->
                            val dispVal = if (isKgUnit) "${(pr.value * 0.45359237).toInt()} kg" else "${pr.value.toInt()} lbs"
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = RoyalBlue900,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pr.exerciseName,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimaryDark
                                        )
                                        Text(
                                            text = "$dispVal · ${pr.reps} reps · ${pr.recordType}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GoldPR
                                        )
                                    }
                                    Row {
                                        TextButton(onClick = {
                                            editingRecord = pr
                                            editValueText = if (isKgUnit) "${(pr.value * 0.45359237).toInt()}" else "${pr.value.toInt()}"
                                            editRepsText = "${pr.reps}"
                                        }) {
                                            Text("Edit", color = ElectricCyan, style = MaterialTheme.typography.labelSmall)
                                        }
                                        TextButton(onClick = { onDeletePR(pr) }) {
                                            Text("Delete", color = Color(0xFFEF4444), style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun PRCelebrationDialog(
    record: PersonalRecordEntity,
    isKgUnit: Boolean = false,
    onDismiss: () -> Unit
) {
    val displayVal = if (isKgUnit) "${(record.value * 0.45359237).toInt()} kg" else "${record.value.toInt()} lbs"
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = GoldPR, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("NEW PERSONAL RECORD!", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = GoldPR)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(record.exerciseName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = displayVal,
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                    color = ElectricRoyalBlue
                )
                Text(
                    text = if (record.recordType == "Est1RM") "Estimated 1-Rep Max from ${record.reps} reps" else "Max Top Weight Completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = RoyalBlue300
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Keep Crushing It!")
            }
        }
    )
}
