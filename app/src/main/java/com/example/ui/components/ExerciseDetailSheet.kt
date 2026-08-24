package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiMovementGuideEntity
import com.example.data.model.ExerciseEntity
import com.example.data.model.PersonalRecordEntity
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
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailSheet(
    exercise: ExerciseEntity,
    progression: ExerciseProgressionSuggestion?,
    prs: List<PersonalRecordEntity>,
    aiGuides: List<AiMovementGuideEntity>,
    isKgUnit: Boolean,
    onDismiss: () -> Unit,
    onGenerateAiGuide: (String, String) -> Unit,
    onStartWorkoutWithExercise: (ExerciseEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val matchingGuide = remember(aiGuides, exercise) {
        aiGuides.firstOrNull { it.exerciseName.equals(exercise.name, ignoreCase = true) }
    }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    var isGeneratingGuide by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "${exercise.category} · ${exercise.equipment}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMutedDark)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Muscle Target Card
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSlateElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Muscle engagement",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ElectricCyan
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Primary muscles", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                    Text(text = exercise.primaryMuscles, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimaryDark)
                                }
                                if (exercise.secondaryMuscles.isNotBlank()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "Secondary muscles", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                        Text(text = exercise.secondaryMuscles, style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Surface(shape = RoundedCornerShape(8.dp), color = RoyalBlue900) {
                                    Text(
                                        text = "Goal: ${exercise.trainingGoal}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RoyalBlue300,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(shape = RoundedCornerShape(8.dp), color = RoyalBlue900) {
                                    Text(
                                        text = "Rest: ${exercise.defaultRestSeconds}s",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RoyalBlue300,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Progression Target Card
                if (progression != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSlateElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (progression.isReadyToOverload) EmeraldSuccess.copy(alpha = 0.5f) else RoyalBlue800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Progressive overload target",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (progression.isReadyToOverload) EmeraldSuccess else ElectricCyan
                                    )
                                    if (progression.isReadyToOverload) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldSuccess.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Ready to overload",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = EmeraldSuccess,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "Next target load", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                        val displayWeight = if (isKgUnit) "${(progression.suggestedWeight * 0.45359237).toInt()} kg" else "${progression.suggestedWeight.toInt()} lbs"
                                        Text(text = displayWeight, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ElectricCyan)
                                    }
                                    Column {
                                        Text(text = "Target reps", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                        Text(text = progression.suggestedReps, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                                    }
                                    Column {
                                        Text(text = "Est 1RM", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                        val display1RM = if (isKgUnit) "${(progression.estimated1RM * 0.45359237).toInt()} kg" else "${progression.estimated1RM.toInt()} lbs"
                                        Text(text = display1RM, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPR)
                                    }
                                }

                                if (progression.progressionNote.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = progression.progressionNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondaryDark
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Biomechanics & Form Guide
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSlateElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI movement & form guide",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ElectricCyan
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (matchingGuide != null) {
                                Text(
                                    text = "Setup instructions",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = RoyalBlue300
                                )
                                Text(
                                    text = matchingGuide.setupInstructions,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryDark
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Biomechanical form cues",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = RoyalBlue300
                                )
                                Text(
                                    text = matchingGuide.formCues,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryDark
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = matchingGuide.concentricPhaseCue,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = EmeraldSuccess
                                )
                                Text(
                                    text = matchingGuide.eccentricPhaseCue,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = RoyalBlue300
                                )
                            } else {
                                Text(
                                    text = "Generate intelligent biomechanical setup cues, tempo instructions, and form cues for this exercise with Gemini.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMutedDark
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        isGeneratingGuide = true
                                        onGenerateAiGuide(exercise.equipment, exercise.name)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate movement guide", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                // Personal Records
                if (prs.isNotEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSlateElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = GoldPR, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Personal records",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldPR
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                prs.forEach { pr ->
                                    val valText = if (isKgUnit) "${(pr.value * 0.45359237).toInt()} kg" else "${pr.value.toInt()} lbs"
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "${pr.recordType} (${pr.reps} reps)", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
                                        Text(text = valText, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button: Start routine with exercise
            Button(
                onClick = {
                    onStartWorkoutWithExercise(exercise)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricRoyalBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start workout with this exercise", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
