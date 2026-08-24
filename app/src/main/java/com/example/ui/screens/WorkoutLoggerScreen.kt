package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.ui.ActiveWorkoutState
import com.example.ui.components.RestTimerDialog
import com.example.ui.components.SetTypeInfoDialog
import com.example.ui.theme.DarkBackground
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
import com.example.ui.theme.RoyalBlue500
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.RoyalBlue800
import com.example.ui.theme.RoyalBlue900
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun WorkoutLoggerScreen(
    activeWorkout: ActiveWorkoutState?,
    allExercises: List<ExerciseEntity>,
    pastSessions: List<WorkoutSessionEntity>,
    onStartWorkout: (String, List<ExerciseEntity>) -> Unit,
    onAddExerciseToActiveWorkout: (ExerciseEntity) -> Unit,
    onAddSet: (Int) -> Unit,
    onRemoveSet: (Int, Int) -> Unit,
    onUpdateSet: (Int, Int, String, String, String?, Boolean, Boolean) -> Unit,
    onCycleSetType: (Int, Int) -> Unit = { _, _ -> },
    onSetExplicitSetType: (Int, Int, String) -> Unit = { _, _, _ -> },
    onToggleSetCompleted: (Int, Int) -> Unit,
    onFinishWorkout: () -> Unit,
    onDiscardWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showRestTimerDialog by remember { mutableStateOf(false) }
    var showFinishConfirmDialog by remember { mutableStateOf(false) }
    var selectedSetForTypeDialog by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    selectedSetForTypeDialog?.let { (exIdx, setIdx) ->
        val currentSet = activeWorkout?.exercises?.getOrNull(exIdx)?.sets?.getOrNull(setIdx)
        SetTypeInfoDialog(
            currentType = currentSet?.setType ?: "R",
            onDismiss = { selectedSetForTypeDialog = null },
            onSelectType = { type ->
                onSetExplicitSetType(exIdx, setIdx, type)
                selectedSetForTypeDialog = null
            }
        )
    }

    if (activeWorkout != null) {
        // --- ACTIVE WORKOUT IN PROGRESS ---
        var elapsedSeconds by remember { mutableIntStateOf(0) }

        LaunchedEffect(activeWorkout.startTimeMillis) {
            while (true) {
                elapsedSeconds = ((System.currentTimeMillis() - activeWorkout.startTimeMillis) / 1000).toInt()
                delay(1000L)
            }
        }

        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        val formattedTimer = String.format("%02d:%02d", minutes, seconds)

        // Calculate live volume and sets completed
        var liveVolume = 0.0
        var completedSetsCount = 0
        var totalSetsCount = 0
        activeWorkout.exercises.forEach { ex ->
            ex.sets.forEach { set ->
                totalSetsCount += 1
                if (set.isCompleted) {
                    completedSetsCount += 1
                    val w = set.weightLbsText.toDoubleOrNull() ?: 0.0
                    val r = set.repsText.toIntOrNull() ?: 0
                    liveVolume += (w * r)
                }
            }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Header Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ElectricCyan.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        RoyalBlue800,
                                        RoyalBlue900,
                                        Color(0xFF070D18)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = RoyalBlue700
                                    ) {
                                        Text(
                                            text = "LIVE WORKOUT",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 1.2.sp
                                            ),
                                            color = ElectricCyan,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = activeWorkout.name,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }

                                // Rest Timer quick open button
                                OutlinedButton(
                                    onClick = { showRestTimerDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue600)
                                ) {
                                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Timer", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stats row (Timer, Sets, Volume)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF070D18),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = formattedTimer,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                            color = ElectricCyan
                                        )
                                        Text("Duration", style = MaterialTheme.typography.labelSmall, color = RoyalBlue300)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF070D18),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "$completedSetsCount / $totalSetsCount",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                            color = Color.White
                                        )
                                        Text("Completed", style = MaterialTheme.typography.labelSmall, color = RoyalBlue300)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF070D18),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                                    modifier = Modifier.weight(1f).padding(start = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${liveVolume.toInt()}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                            color = EmeraldSuccess
                                        )
                                        Text("lbs Volume", style = MaterialTheme.typography.labelSmall, color = RoyalBlue300)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // List of exercises in active workout
            itemsIndexed(activeWorkout.exercises) { exIndex, activeEx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RoyalBlue700.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Exercise Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeEx.exercise.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${activeEx.exercise.category} • ${activeEx.exercise.equipment}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RoyalBlue300
                                )
                            }
                        }

                        // Last Session & Overload Starting Suggestion
                        if (activeEx.progressionSuggestion != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF070D18),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (activeEx.progressionSuggestion.lastTopWeight > 0)
                                                "Last: ${activeEx.progressionSuggestion.lastTopWeight.toInt()} lbs × ${activeEx.progressionSuggestion.lastTopReps}"
                                            else "Baseline: Find starting weight",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RoyalBlue200
                                        )
                                    }
                                    if (activeEx.progressionSuggestion.suggestedWeight > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = RoyalBlue700
                                        ) {
                                            Text(
                                                text = "Target: ${activeEx.progressionSuggestion.suggestedWeight.toInt()} lbs",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = ElectricCyan,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Table Header (SET, TYPE, LBS, REPS, COMPLETE)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SET",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue300,
                                modifier = Modifier.width(32.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TYPE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue300,
                                modifier = Modifier.width(52.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LBS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ElectricCyan,
                                modifier = Modifier.weight(1.1f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REPS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ElectricCyan,
                                modifier = Modifier.weight(1.1f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldSuccess,
                                modifier = Modifier.width(42.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Sets list rows
                        activeEx.sets.forEachIndexed { setIdx, s ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    s.isCompleted -> RoyalBlue800
                                    s.setType == "W" -> RoyalBlue900.copy(alpha = 0.6f)
                                    else -> Color(0xFF070D18)
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (s.isCompleted) ElectricCyan.copy(alpha = 0.8f) else RoyalBlue800
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 6.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Set Number
                                    Text(
                                        text = "${s.setNumber}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (s.isCompleted) ElectricCyan else Color.White,
                                        modifier = Modifier.width(32.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Set Type Pill (Click to cycle R -> W -> D -> S, Hold to show info dialog)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (s.setType) {
                                            "W" -> Color(0xFFF59E0B).copy(alpha = 0.25f)
                                            "D" -> Color(0xFF8B5CF6).copy(alpha = 0.25f)
                                            "S" -> EmeraldSuccess.copy(alpha = 0.25f)
                                            else -> RoyalBlue700
                                        },
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            when (s.setType) {
                                                "W" -> Color(0xFFF59E0B)
                                                "D" -> Color(0xFFA78BFA)
                                                "S" -> EmeraldSuccess
                                                else -> ElectricRoyalBlue
                                            }
                                        ),
                                        modifier = Modifier
                                            .width(52.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .combinedClickable(
                                                onClick = { onCycleSetType(exIndex, setIdx) },
                                                onLongClick = { selectedSetForTypeDialog = Pair(exIndex, setIdx) }
                                            )
                                    ) {
                                        Text(
                                            text = when (s.setType) {
                                                "W" -> "WARM"
                                                "D" -> "DROP"
                                                "S" -> "SUPER"
                                                else -> "WORK"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                                            color = when (s.setType) {
                                                "W" -> Color(0xFFF59E0B)
                                                "D" -> Color(0xFFA78BFA)
                                                "S" -> EmeraldSuccess
                                                else -> Color.White
                                            },
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Weight LBS TextField
                                    OutlinedTextField(
                                        value = s.weightLbsText,
                                        onValueChange = { newW ->
                                            onUpdateSet(exIndex, setIdx, newW, s.repsText, s.rpeText, s.setType == "W", s.isCompleted)
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ElectricCyan,
                                            unfocusedBorderColor = RoyalBlue700,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier
                                            .weight(1.1f)
                                            .height(48.dp)
                                            .testTag("set_weight_field_${exIndex}_$setIdx")
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Reps TextField
                                    OutlinedTextField(
                                        value = s.repsText,
                                        onValueChange = { newR ->
                                            onUpdateSet(exIndex, setIdx, s.weightLbsText, newR, s.rpeText, s.setType == "W", s.isCompleted)
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ElectricCyan,
                                            unfocusedBorderColor = RoyalBlue700,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier
                                            .weight(1.1f)
                                            .height(48.dp)
                                            .testTag("set_reps_field_${exIndex}_$setIdx")
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Complete Checkbox button
                                    IconButton(
                                        onClick = {
                                            onToggleSetCompleted(exIndex, setIdx)
                                            if (!s.isCompleted) {
                                                // Trigger rest timer
                                                showRestTimerDialog = true
                                            }
                                        },
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(
                                                if (s.isCompleted) EmeraldSuccess else RoyalBlue800,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .testTag("complete_set_btn_${exIndex}_$setIdx")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Complete Set",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add Set & Remove Set Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { onAddSet(exIndex) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue700)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add Set", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }

                            if (activeEx.sets.isNotEmpty()) {
                                TextButton(
                                    onClick = { onRemoveSet(exIndex, activeEx.sets.size - 1) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = RoyalBlue300)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove Set", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            // Add Exercise Button
            item {
                OutlinedButton(
                    onClick = { showAddExerciseDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricCyan.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("add_exercise_to_workout_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Exercise To Workout", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            // Finish & Discard Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDiscardWorkout,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF43F5E)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF43F5E).copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Discard")
                    }

                    Button(
                        onClick = { showFinishConfirmDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp)
                            .testTag("finish_workout_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Finish Workout",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    } else {
        // --- NO WORKOUT IN PROGRESS: ROUTINE STARTER & HISTORY ---
        val dateFormat = remember { SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()) }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Start Workout Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RoyalBlue600, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        RoyalBlue700,
                                        RoyalBlue900,
                                        Color(0xFF070D18)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(RoyalBlue600, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "LOG GYM EXERCISES",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.5.sp
                                        ),
                                        color = ElectricCyan
                                    )
                                    Text(
                                        text = "Start Today's Session",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Track sets, reps, weight progression, and automatically check progressive overload against last week.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = RoyalBlue200
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = { onStartWorkout("Custom Session", emptyList()) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("start_empty_workout_button")
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Start Empty Workout",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Routine Templates
            item {
                Text(
                    text = "WORKOUT ROUTINES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = ElectricCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val routines = listOf(
                        Triple("Push: Chest & Shoulders", "Barbell Bench Press, Incline DB Press, Overhead Shoulder Press, Lateral Raise", "Chest"),
                        Triple("Pull: Back & Biceps", "Lat Pulldown, Seated Cable Row, Barbell Bicep Curl, Cable Face Pull", "Back"),
                        Triple("Legs: Quads & Hamstrings", "Barbell Back Squat, Romanian Deadlift, Leg Press 45°, Hamstring Curl", "Legs"),
                        Triple("Arms & Delts Hypertrophy", "Barbell Bicep Curl, Tricep Rope Pushdown, Lateral Raise, Face Pull", "Arms")
                    )

                    routines.forEach { (name, desc, filterCat) ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = RoyalBlue900,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue700.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    val matchedExercises = allExercises.filter { it.category == filterCat || it.category == "Arms" || it.category == "Shoulders" }.take(4)
                                    onStartWorkout(name, matchedExercises)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RoyalBlue300,
                                        maxLines = 1
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(RoyalBlue700, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Start Routine",
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Past Workout Sessions History
            if (pastSessions.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RECENT WORKOUT SESSIONS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = ElectricCyan
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(pastSessions) { session ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = RoyalBlue900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = session.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = dateFormat.format(Date(session.dateMillis)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RoyalBlue300
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = RoyalBlue800
                                ) {
                                    Text(
                                        text = "${session.durationMinutes} min",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ElectricCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Sets: ${session.totalSets}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RoyalBlue200
                                )
                                Text(
                                    text = "Volume: ${session.totalVolumeLbs.toInt()} lbs",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldSuccess
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS ---
    if (showRestTimerDialog) {
        RestTimerDialog(
            initialSeconds = 90,
            onDismiss = { showRestTimerDialog = false }
        )
    }

    if (showFinishConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFinishConfirmDialog = false },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Finish Workout?", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "All completed sets will be recorded and updated in your progression engine and consistency calendar.",
                    color = RoyalBlue300
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishConfirmDialog = false
                        onFinishWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text("Save & Complete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishConfirmDialog = false }) {
                    Text("Cancel", color = RoyalBlue300)
                }
            }
        )
    }

    if (showAddExerciseDialog) {
        AddExerciseToWorkoutDialog(
            allExercises = allExercises,
            onSelectExercise = { ex ->
                onAddExerciseToActiveWorkout(ex)
                showAddExerciseDialog = false
            },
            onDismiss = { showAddExerciseDialog = false }
        )
    }
}

@Composable
private fun AddExerciseToWorkoutDialog(
    allExercises: List<ExerciseEntity>,
    onSelectExercise: (ExerciseEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms")

    val filteredExercises = allExercises.filter { ex ->
        val matchesCategory = (selectedCategory == "All" || ex.category.equals(selectedCategory, true))
        val matchesQuery = searchQuery.isEmpty() || ex.name.contains(searchQuery, true) || ex.primaryMuscles.contains(searchQuery, true)
        matchesCategory && matchesQuery
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = "Add Exercise",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name or muscle...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RoyalBlue300) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = RoyalBlue700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("search_exercise_field")
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(360.dp)) {
                // Category Filter Row
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) RoyalBlue600 else RoyalBlue800,
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else RoyalBlue300,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Exercises List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredExercises) { ex ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RoyalBlue900,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectExercise(ex) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ex.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${ex.category} • ${ex.equipment}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RoyalBlue300
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Select",
                                    tint = ElectricCyan
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = RoyalBlue300)
            }
        }
    )
}
