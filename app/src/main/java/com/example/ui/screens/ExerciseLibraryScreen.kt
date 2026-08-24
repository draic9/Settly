package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.AiMovementGuideEntity
import com.example.data.model.CustomRoutineEntity
import com.example.data.model.ExerciseEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.repository.ExerciseProgressionSuggestion
import com.example.ui.components.AddCustomExerciseDialog
import com.example.ui.components.CreateRoutineDialog
import com.example.ui.components.ExerciseDetailSheet
import com.example.ui.components.RotaryBarrelMuscleSelector
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSlateElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricRoyalBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldPR
import com.example.ui.theme.RoyalBlue200
import com.example.ui.theme.RoyalBlue300
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.RoyalBlue800
import com.example.ui.theme.RoyalBlue900
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseLibraryScreen(
    exercises: List<ExerciseEntity>,
    customRoutines: List<CustomRoutineEntity>,
    personalRecords: List<PersonalRecordEntity>,
    aiGuides: List<AiMovementGuideEntity>,
    progressionMap: Map<Long, ExerciseProgressionSuggestion>,
    isKgUnit: Boolean,
    onStartWorkout: (String, List<ExerciseEntity>) -> Unit,
    onCreateRoutine: (String, String, String, String) -> Unit,
    onTogglePinRoutine: (CustomRoutineEntity) -> Unit,
    onDeleteRoutine: (CustomRoutineEntity) -> Unit,
    onAddNewExercise: (ExerciseEntity) -> Unit,
    onGenerateAiGuide: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSectionTab by remember { mutableIntStateOf(0) } // 0: routines, 1: exercise library
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf("All Movements") }
    var selectedExerciseForDetail by remember { mutableStateOf<ExerciseEntity?>(null) }
    var showCreateRoutineDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    val filteredExercises = remember(exercises, searchQuery, selectedMuscleGroup) {
        exercises.filter { ex ->
            val matchesQuery = ex.name.contains(searchQuery, ignoreCase = true) ||
                    ex.category.contains(searchQuery, ignoreCase = true) ||
                    ex.equipment.contains(searchQuery, ignoreCase = true)
            val matchesMuscle = when (selectedMuscleGroup.lowercase()) {
                "all movements" -> true
                "chest" -> ex.category.equals("Chest", true)
                "back" -> ex.category.equals("Back", true)
                "legs" -> ex.category.equals("Legs", true)
                "shoulders" -> ex.category.equals("Shoulders", true)
                "arms" -> ex.category.equals("Arms", true)
                "core" -> ex.category.equals("Core", true)
                "full body" -> ex.category.equals("Full Body", true)
                else -> true
            }
            matchesQuery && matchesMuscle
        }
    }

    val sortedRoutines = remember(customRoutines) {
        customRoutines.sortedWith(
            compareByDescending<CustomRoutineEntity> { it.isPinned }
                .thenByDescending { it.usageCount }
                .thenByDescending { it.createdAtMillis }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Section Switcher Tab
        TabRow(
            selectedTabIndex = selectedSectionTab,
            containerColor = DarkSlateElevated,
            contentColor = ElectricCyan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedSectionTab == 0,
                onClick = { selectedSectionTab = 0 },
                text = {
                    Text(
                        text = "Routines (${customRoutines.size})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedSectionTab == 0) FontWeight.Bold else FontWeight.Normal),
                        color = if (selectedSectionTab == 0) ElectricCyan else TextMutedDark
                    )
                }
            )
            Tab(
                selected = selectedSectionTab == 1,
                onClick = { selectedSectionTab = 1 },
                text = {
                    Text(
                        text = "Exercise directory (${exercises.size})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedSectionTab == 1) FontWeight.Bold else FontWeight.Normal),
                        color = if (selectedSectionTab == 1) ElectricCyan else TextMutedDark
                    )
                }
            )
        }

        // Main Content Area
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (selectedSectionTab == 0) {
                // --- ROUTINES VIEW ---
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Training splits & routines",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Press and hold to pin top routines to your dashboard",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedDark
                            )
                        }

                        Button(
                            onClick = { showCreateRoutineDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create routine", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                if (sortedRoutines.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSlateElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No custom routines created yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimaryDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Create your first workout split to organize exercises and log sessions effortlessly.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMutedDark
                                )
                            }
                        }
                    }
                } else {
                    items(sortedRoutines, key = { it.id }) { routine ->
                        val routineExIds = routine.exerciseIdsCsv.split(",").mapNotNull { it.trim().toLongOrNull() }
                        val routineExercises = exercises.filter { routineExIds.contains(it.id) }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSlateElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (routine.isPinned) ElectricCyan.copy(alpha = 0.5f) else RoyalBlue800
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        onStartWorkout(routine.name, routineExercises)
                                    },
                                    onLongClick = {
                                        onTogglePinRoutine(routine)
                                    }
                                )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (routine.isPinned) {
                                            Icon(
                                                imageVector = Icons.Default.PushPin,
                                                contentDescription = "Pinned",
                                                tint = ElectricCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = routine.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimaryDark
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { onTogglePinRoutine(routine) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (routine.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                                contentDescription = "Pin routine",
                                                tint = if (routine.isPinned) ElectricCyan else TextMutedDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteRoutine(routine) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete routine",
                                                tint = RoyalBlue300,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = routine.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElectricCyan
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "${routineExercises.size} exercises: ${routineExercises.joinToString(", ") { it.name }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMutedDark,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = RoyalBlue900
                                    ) {
                                        Text(
                                            text = "Used ${routine.usageCount} times",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RoyalBlue300,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            onStartWorkout(routine.name, routineExercises)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Start routine", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // --- EXERCISE DIRECTORY VIEW ---
                item {
                    // Search & Muscle Dial
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search movements or equipment...", color = TextMutedDark, style = MaterialTheme.typography.bodySmall) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(18.dp)) },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricRoyalBlue,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RotaryBarrelMuscleSelector(
                                selectedMuscle = selectedMuscleGroup,
                                onMuscleChanged = { selectedMuscleGroup = it },
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { showAddExerciseDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add exercise", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                items(filteredExercises, key = { it.id }) { exercise ->
                    val prog = progressionMap[exercise.id]
                    val exercisePrs = personalRecords.filter { it.exerciseId == exercise.id }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSlateElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (prog?.isReadyToOverload == true) EmeraldSuccess.copy(alpha = 0.4f) else RoyalBlue800
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedExerciseForDetail = exercise
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = exercise.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimaryDark
                                    )
                                    if (prog?.isReadyToOverload == true) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = EmeraldSuccess.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "+ Overload",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                                color = EmeraldSuccess,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${exercise.category} · ${exercise.equipment} · ${exercise.primaryMuscles}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMutedDark
                                )

                                if (prog != null && prog.lastTopWeight > 0) {
                                    val topWeightDisplay = if (isKgUnit) "${(prog.lastTopWeight * 0.45359237).toInt()} kg" else "${prog.lastTopWeight.toInt()} lbs"
                                    val nextWeightDisplay = if (isKgUnit) "${(prog.suggestedWeight * 0.45359237).toInt()} kg" else "${prog.suggestedWeight.toInt()} lbs"
                                    Text(
                                        text = "Current: $topWeightDisplay · Next: $nextWeightDisplay",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectricCyan
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = "View exercise detail",
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Detail Bottom Sheet
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
                onStartWorkout("custom workout", listOf(exercise))
            }
        )
    }

    // Dialogs
    if (showCreateRoutineDialog) {
        CreateRoutineDialog(
            exercises = exercises,
            onDismiss = { showCreateRoutineDialog = false },
            onCreate = onCreateRoutine
        )
    }

    if (showAddExerciseDialog) {
        AddCustomExerciseDialog(
            onDismiss = { showAddExerciseDialog = false },
            onAddExercise = onAddNewExercise
        )
    }
}
