package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiMovementAnalysis
import com.example.data.model.AiMovementGuideEntity
import com.example.data.model.ExerciseEntity
import com.example.ui.AiAnalysisUiState
import com.example.ui.components.MuscleAnatomyDiagram
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricCyan
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiMovementVisualizerScreen(
    aiAnalysisState: AiAnalysisUiState,
    savedGuides: List<AiMovementGuideEntity>,
    onAnalyze: (String, String) -> Unit,
    onSaveGuide: (AiMovementAnalysis, String, String) -> Unit,
    onStartWorkoutWithMovement: (String) -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var machineInput by remember { mutableStateOf("") }
    var movementInput by remember { mutableStateOf("") }
    var hasSavedCurrent by remember { mutableStateOf(false) }

    val presetSamples = listOf(
        Pair("Seated Cable Row V-Bar", "Drive elbows backward horizontally pulling low to the navel with 1s squeeze"),
        Pair("Incline Smith Machine Press", "30-degree incline, elbows at 45 degrees, pressing to clavicle"),
        Pair("45° Leg Press Machine", "Feet high and wide on platform for glute and hamstring emphasis"),
        Pair("Standing Cable Lateral Raise", "Cables set at knee height, cross-body pull leading with elbows"),
        Pair("Pec Deck Flye Machine", "Neutral grip, slight elbow bend, deep stretch and hard pectoral squeeze"),
        Pair("Lat Pulldown Wide Grip", "Overhand grip, chest proud, pulling bar to upper sternum")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // --- 1. Header Banner ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                shape = RoundedCornerShape(22.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    RoyalBlue700.copy(alpha = 0.5f),
                                    RoyalBlue900,
                                    Color(0xFF070D18)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(RoyalBlue600, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "AI BIOMECHANICS & MACHINE ENGINE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.5.sp
                                        ),
                                        color = ElectricCyan
                                    )
                                    Text(
                                        text = "Movement visualizer",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }

                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Describe any machine and your movement style to generate a detailed anatomical visualization, highlighted prime movers, setup cues, and starting weight targets.",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoyalBlue200
                        )
                    }
                }
            }
        }

        // --- 2. Input Component ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RoyalBlue700.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "1. DESCRIBE THE MACHINE / EQUIPMENT",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = machineInput,
                        onValueChange = { machineInput = it },
                        placeholder = { Text("e.g. Seated Cable Row machine with close-grip handle") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = RoyalBlue700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("machine_input_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "2. DESCRIBE THE MOVEMENT / GRIP / PATH",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = movementInput,
                        onValueChange = { movementInput = it },
                        placeholder = { Text("e.g. Pulling low towards the navel with elbows tucked and scapular retraction") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = RoyalBlue700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("movement_input_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset Chips
                    Text(
                        text = "Quick Presets:",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoyalBlue300
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presetSamples) { (m, mv) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = RoyalBlue800,
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue700),
                                modifier = Modifier.clickable {
                                    machineInput = m
                                    movementInput = mv
                                }
                            ) {
                                Text(
                                    text = m,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RoyalBlue200,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Submit Action
                    Button(
                        onClick = {
                            if (machineInput.isNotBlank()) {
                                hasSavedCurrent = false
                                onAnalyze(machineInput, movementInput.ifBlank { "Standard full range of motion" })
                            }
                        },
                        enabled = machineInput.isNotBlank() && aiAnalysisState !is AiAnalysisUiState.Loading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_visualization_button")
                    ) {
                        if (aiAnalysisState is AiAnalysisUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generating Biomechanics Blueprint...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Generate Muscle Visualization & Blueprint",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // --- 3. AI Analysis Results ---
        when (aiAnalysisState) {
            is AiAnalysisUiState.Loading -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, RoyalBlue700, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = ElectricCyan,
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Analyzing Machine Biomechanics...",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mapping muscle activation vectors & generating technical blueprint...",
                                style = MaterialTheme.typography.bodySmall,
                                color = RoyalBlue300
                            )
                        }
                    }
                }
            }

            is AiAnalysisUiState.Success -> {
                val analysis = aiAnalysisState.analysis

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // 1. Anatomical Silhouette & Machine Blueprint Visualizer
                        MuscleAnatomyDiagram(
                            muscleActivationMap = analysis.muscleActivationMap,
                            primaryMuscles = analysis.primaryMuscles,
                            secondaryMuscles = analysis.secondaryMuscles,
                            blueprintType = analysis.blueprintType
                        )

                        // 2. Machine & Setup Instructions Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, RoyalBlue700.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                            colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = analysis.exerciseName,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF070D18),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.FitnessCenter,
                                                contentDescription = null,
                                                tint = ElectricCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "MACHINE SETUP & ERGONOMICS",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = ElectricCyan
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = analysis.machineSetup,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = RoyalBlue100
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Suggested Starting Weight
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = RoyalBlue800,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue600)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Suggested Starting Load:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = RoyalBlue200
                                        )
                                        Text(
                                            text = analysis.suggestedStartingWeight,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ElectricCyan
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Execution Steps
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, RoyalBlue700.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                            colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "STEP-BY-STEP EXECUTION",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = ElectricCyan
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                analysis.executionSteps.forEachIndexed { idx, step ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(RoyalBlue700, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${idx + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = ElectricCyan
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = step.removePrefix("${idx + 1}.").removePrefix("Step ${idx + 1}:").trim(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // 4. Form Cues & Common Mistakes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Cues
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, RoyalBlue700.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = GoldPR, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Key Cues", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = GoldPR)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    analysis.formCues.forEach { cue ->
                                        Text("• $cue", style = MaterialTheme.typography.bodySmall, color = RoyalBlue100, modifier = Modifier.padding(vertical = 2.dp))
                                    }
                                }
                            }

                            // Mistakes
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, RoyalBlue700.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Avoid", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFF43F5E))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    analysis.commonMistakes.forEach { mistake ->
                                        Text("• $mistake", style = MaterialTheme.typography.bodySmall, color = RoyalBlue100, modifier = Modifier.padding(vertical = 2.dp))
                                    }
                                }
                            }
                        }

                        // 5. Actions: Save Guide & Start Workout
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onSaveGuide(analysis, machineInput, movementInput)
                                    hasSavedCurrent = true
                                },
                                enabled = !hasSavedCurrent,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                                modifier = Modifier.weight(1f).height(50.dp)
                            ) {
                                Icon(
                                    imageVector = if (hasSavedCurrent) Icons.Default.Check else Icons.Default.Bookmark,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (hasSavedCurrent) "Saved!" else "Save Guide")
                            }

                            Button(
                                onClick = { onStartWorkoutWithMovement(analysis.exerciseName) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                                modifier = Modifier.weight(1.2f).height(50.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Start Workout", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            is AiAnalysisUiState.Error -> {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF3B1D28),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF43F5E))
                    ) {
                        Text(
                            text = "Error: ${(aiAnalysisState as AiAnalysisUiState.Error).message}",
                            color = Color(0xFFFCA5A5),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            else -> Unit
        }

        // --- 4. Saved Guides History ---
        if (savedGuides.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "SAVED MOVEMENT GUIDES (${savedGuides.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = ElectricCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(savedGuides) { guide ->
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
                            Text(
                                text = guide.exerciseName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = RoyalBlue800
                            ) {
                                Text(
                                    text = guide.primaryMuscles.split(",").firstOrNull() ?: "Target",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Machine: ${guide.machineDescription}",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoyalBlue300
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Setup: ${guide.setupInstructions}",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoyalBlue100,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
