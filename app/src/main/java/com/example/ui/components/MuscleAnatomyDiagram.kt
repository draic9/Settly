package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.RoyalBlue400
import com.example.ui.theme.RoyalBlue500
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.RoyalBlue800
import com.example.ui.theme.RoyalBlue900

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MuscleAnatomyDiagram(
    muscleActivationMap: Map<String, Int>,
    primaryMuscles: List<String>,
    secondaryMuscles: List<String>,
    blueprintType: String = "GENERIC",
    modifier: Modifier = Modifier
) {
    var selectedView by remember { mutableStateOf(0) } // 0 = Front, 1 = Back, 2 = Machine Blueprint

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, RoyalBlue700.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = RoyalBlue900.copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(RoyalBlue600.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI BIOMECHANICS VISUALIZER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = ElectricCyan
                    )
                }

                // Unified Style Indicator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RoyalBlue800.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "Royal Blue Blueprint",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoyalBlue300,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // View Tabs (Front / Back / Machine)
            TabRow(
                selectedTabIndex = selectedView,
                containerColor = Color(0xFF070D18),
                contentColor = ElectricCyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedView == 0,
                    onClick = { selectedView = 0 },
                    text = {
                        Text(
                            "Front Anatomy",
                            fontWeight = if (selectedView == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedView == 1,
                    onClick = { selectedView = 1 },
                    text = {
                        Text(
                            "Posterior Anatomy",
                            fontWeight = if (selectedView == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedView == 2,
                    onClick = { selectedView = 2 },
                    text = {
                        Text(
                            "Machine Blueprint",
                            fontWeight = if (selectedView == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Canvas Component
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                RoyalBlue700.copy(alpha = 0.25f),
                                Color(0xFF070D18)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, RoyalBlue800.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                when (selectedView) {
                    0 -> AnatomicalBodyCanvas(
                        isFront = true,
                        activationMap = muscleActivationMap,
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> AnatomicalBodyCanvas(
                        isFront = false,
                        activationMap = muscleActivationMap,
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> MachineBlueprintCanvas(
                        blueprintType = blueprintType,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend & Active Muscles Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(ElectricCyan, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Primary Target (80-100%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(RoyalBlue400, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Secondary (40-79%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoyalBlue300
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Muscles Breakdown
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                primaryMuscles.forEach { muscle ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RoyalBlue600.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.7f))
                    ) {
                        Text(
                            text = "⚡ $muscle",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                secondaryMuscles.forEach { muscle ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RoyalBlue800.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue600.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = muscle,
                            style = MaterialTheme.typography.labelSmall,
                            color = RoyalBlue200,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

val RoyalBlue300 = Color(0xFF93C5FD)
val RoyalBlue200 = Color(0xFFBFDBFE)

@Composable
private fun AnatomicalBodyCanvas(
    isFront: Boolean,
    activationMap: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.padding(16.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val scale = size.height / 320f

        // Gridlines backdrop (Technical Royal Blue Blueprint effect)
        val gridColor = Color(0xFF1E3A8A).copy(alpha = 0.25f)
        var gx = 0f
        while (gx < size.width) {
            drawLine(gridColor, Offset(gx, 0f), Offset(gx, size.height), strokeWidth = 1f)
            gx += 24f
        }
        var gy = 0f
        while (gy < size.height) {
            drawLine(gridColor, Offset(0f, gy), Offset(size.width, gy), strokeWidth = 1f)
            gy += 24f
        }

        // Helper to get muscle color based on activation intensity
        fun getMuscleColor(key: String): Color {
            val activation = activationMap[key] ?: 0
            return when {
                activation >= 75 -> ElectricCyan
                activation >= 45 -> RoyalBlue400
                activation >= 20 -> RoyalBlue600.copy(alpha = 0.8f)
                else -> Color(0xFF1E293B) // Base inactive slate
            }
        }

        val baseOutlineColor = Color(0xFF334155)
        val bodyBaseColor = Color(0xFF131F33)

        // Draw Head & Neck
        drawCircle(
            color = bodyBaseColor,
            radius = 16f * scale,
            center = Offset(cx, 32f * scale)
        )
        drawCircle(
            color = baseOutlineColor,
            radius = 16f * scale,
            center = Offset(cx, 32f * scale),
            style = Stroke(1.5f * scale)
        )

        // Trapezius / Neck base
        val trapColor = getMuscleColor("TRAPS")
        val trapPath = Path().apply {
            moveTo(cx - 10f * scale, 44f * scale)
            lineTo(cx + 10f * scale, 44f * scale)
            lineTo(cx + 28f * scale, 60f * scale)
            lineTo(cx - 28f * scale, 60f * scale)
            close()
        }
        drawPath(trapPath, trapColor)
        drawPath(trapPath, baseOutlineColor, style = Stroke(1f * scale))

        // Deltoids (Shoulders Left & Right)
        val frontDeltColor = getMuscleColor(if (isFront) "FRONT_DELTS" else "REAR_DELTS")
        val sideDeltColor = getMuscleColor("SIDE_DELTS")
        val deltColor = if (sideDeltColor != Color(0xFF1E293B)) sideDeltColor else frontDeltColor

        // Left Shoulder
        drawOval(
            color = deltColor,
            topLeft = Offset(cx - 48f * scale, 56f * scale),
            size = Size(20f * scale, 28f * scale)
        )
        drawOval(
            color = baseOutlineColor,
            topLeft = Offset(cx - 48f * scale, 56f * scale),
            size = Size(20f * scale, 28f * scale),
            style = Stroke(1f * scale)
        )

        // Right Shoulder
        drawOval(
            color = deltColor,
            topLeft = Offset(cx + 28f * scale, 56f * scale),
            size = Size(20f * scale, 28f * scale)
        )
        drawOval(
            color = baseOutlineColor,
            topLeft = Offset(cx + 28f * scale, 56f * scale),
            size = Size(20f * scale, 28f * scale),
            style = Stroke(1f * scale)
        )

        if (isFront) {
            // --- FRONT VIEW ---
            // Chest (Pectorals)
            val chestColor = getMuscleColor("CHEST")
            val upperChestColor = getMuscleColor("UPPER_CHEST")
            val effectiveChestColor = if (upperChestColor != Color(0xFF1E293B)) upperChestColor else chestColor

            // Left Pec
            drawRoundRect(
                color = effectiveChestColor,
                topLeft = Offset(cx - 27f * scale, 62f * scale),
                size = Size(24f * scale, 22f * scale),
                cornerRadius = CornerRadius(6f * scale, 6f * scale)
            )
            // Right Pec
            drawRoundRect(
                color = effectiveChestColor,
                topLeft = Offset(cx + 3f * scale, 62f * scale),
                size = Size(24f * scale, 22f * scale),
                cornerRadius = CornerRadius(6f * scale, 6f * scale)
            )

            // Abdominals (Six-pack)
            val absColor = getMuscleColor("ABS")
            for (i in 0..2) {
                // Left ab brick
                drawRoundRect(
                    color = absColor,
                    topLeft = Offset(cx - 15f * scale, (90f + i * 14f) * scale),
                    size = Size(13f * scale, 10f * scale),
                    cornerRadius = CornerRadius(3f * scale, 3f * scale)
                )
                // Right ab brick
                drawRoundRect(
                    color = absColor,
                    topLeft = Offset(cx + 2f * scale, (90f + i * 14f) * scale),
                    size = Size(13f * scale, 10f * scale),
                    cornerRadius = CornerRadius(3f * scale, 3f * scale)
                )
            }

            // Obliques (Sides)
            val obliquesColor = getMuscleColor("OBLIQUES")
            drawOval(
                color = obliquesColor,
                topLeft = Offset(cx - 28f * scale, 92f * scale),
                size = Size(10f * scale, 36f * scale)
            )
            drawOval(
                color = obliquesColor,
                topLeft = Offset(cx + 18f * scale, 92f * scale),
                size = Size(10f * scale, 36f * scale)
            )

            // Biceps (Front Arms)
            val bicepColor = getMuscleColor("BICEPS")
            drawOval(
                color = bicepColor,
                topLeft = Offset(cx - 46f * scale, 88f * scale),
                size = Size(16f * scale, 32f * scale)
            )
            drawOval(
                color = bicepColor,
                topLeft = Offset(cx + 30f * scale, 88f * scale),
                size = Size(16f * scale, 32f * scale)
            )

            // Forearms
            val forearmColor = getMuscleColor("FOREARMS")
            drawOval(
                color = forearmColor,
                topLeft = Offset(cx - 48f * scale, 124f * scale),
                size = Size(14f * scale, 36f * scale)
            )
            drawOval(
                color = forearmColor,
                topLeft = Offset(cx + 34f * scale, 124f * scale),
                size = Size(14f * scale, 36f * scale)
            )

            // Quadriceps (Front Thighs)
            val quadColor = getMuscleColor("QUADS")
            // Left Quad
            drawRoundRect(
                color = quadColor,
                topLeft = Offset(cx - 28f * scale, 146f * scale),
                size = Size(24f * scale, 66f * scale),
                cornerRadius = CornerRadius(10f * scale, 10f * scale)
            )
            // Right Quad
            drawRoundRect(
                color = quadColor,
                topLeft = Offset(cx + 4f * scale, 146f * scale),
                size = Size(24f * scale, 66f * scale),
                cornerRadius = CornerRadius(10f * scale, 10f * scale)
            )

            // Calves (Front Shin / Gastrocnemius edges)
            val calfColor = getMuscleColor("CALVES")
            drawRoundRect(
                color = calfColor,
                topLeft = Offset(cx - 24f * scale, 222f * scale),
                size = Size(18f * scale, 54f * scale),
                cornerRadius = CornerRadius(8f * scale, 8f * scale)
            )
            drawRoundRect(
                color = calfColor,
                topLeft = Offset(cx + 6f * scale, 222f * scale),
                size = Size(18f * scale, 54f * scale),
                cornerRadius = CornerRadius(8f * scale, 8f * scale)
            )

        } else {
            // --- POSTERIOR / BACK VIEW ---
            // Latissimus Dorsi & Rhomboids
            val latColor = getMuscleColor("LATS")
            val rhomboidColor = getMuscleColor("RHOMBOIDS")

            // Upper & Middle Back (Lats V-Taper)
            val latPathLeft = Path().apply {
                moveTo(cx - 4f * scale, 62f * scale)
                lineTo(cx - 30f * scale, 68f * scale)
                lineTo(cx - 18f * scale, 126f * scale)
                lineTo(cx - 4f * scale, 130f * scale)
                close()
            }
            val latPathRight = Path().apply {
                moveTo(cx + 4f * scale, 62f * scale)
                lineTo(cx + 30f * scale, 68f * scale)
                lineTo(cx + 18f * scale, 126f * scale)
                lineTo(cx + 4f * scale, 130f * scale)
                close()
            }
            drawPath(latPathLeft, latColor)
            drawPath(latPathRight, latColor)

            // Rhomboids / Middle Spine diamond
            val rhomboidPath = Path().apply {
                moveTo(cx, 62f * scale)
                lineTo(cx - 14f * scale, 82f * scale)
                lineTo(cx, 104f * scale)
                lineTo(cx + 14f * scale, 82f * scale)
                close()
            }
            drawPath(rhomboidPath, rhomboidColor)

            // Lower Back / Spinal Erectors
            val lowerBackColor = getMuscleColor("LOWER_BACK")
            drawRoundRect(
                color = lowerBackColor,
                topLeft = Offset(cx - 14f * scale, 120f * scale),
                size = Size(28f * scale, 20f * scale),
                cornerRadius = CornerRadius(4f * scale, 4f * scale)
            )

            // Triceps (Back Arms)
            val tricepColor = getMuscleColor("TRICEPS")
            drawOval(
                color = tricepColor,
                topLeft = Offset(cx - 48f * scale, 86f * scale),
                size = Size(16f * scale, 34f * scale)
            )
            drawOval(
                color = tricepColor,
                topLeft = Offset(cx + 32f * scale, 86f * scale),
                size = Size(16f * scale, 34f * scale)
            )

            // Forearms (Back)
            val forearmColor = getMuscleColor("FOREARMS")
            drawOval(
                color = forearmColor,
                topLeft = Offset(cx - 48f * scale, 124f * scale),
                size = Size(14f * scale, 36f * scale)
            )
            drawOval(
                color = forearmColor,
                topLeft = Offset(cx + 34f * scale, 124f * scale),
                size = Size(14f * scale, 36f * scale)
            )

            // Glutes (Gluteus Maximus)
            val gluteColor = getMuscleColor("GLUTES")
            drawOval(
                color = gluteColor,
                topLeft = Offset(cx - 28f * scale, 142f * scale),
                size = Size(26f * scale, 30f * scale)
            )
            drawOval(
                color = gluteColor,
                topLeft = Offset(cx + 2f * scale, 142f * scale),
                size = Size(26f * scale, 30f * scale)
            )

            // Hamstrings (Back Thighs)
            val hamstringColor = getMuscleColor("HAMSTRINGS")
            drawRoundRect(
                color = hamstringColor,
                topLeft = Offset(cx - 27f * scale, 174f * scale),
                size = Size(23f * scale, 44f * scale),
                cornerRadius = CornerRadius(8f * scale, 8f * scale)
            )
            drawRoundRect(
                color = hamstringColor,
                topLeft = Offset(cx + 4f * scale, 174f * scale),
                size = Size(23f * scale, 44f * scale),
                cornerRadius = CornerRadius(8f * scale, 8f * scale)
            )

            // Calves (Gastrocnemius & Soleus)
            val calfColor = getMuscleColor("CALVES")
            drawOval(
                color = calfColor,
                topLeft = Offset(cx - 25f * scale, 224f * scale),
                size = Size(20f * scale, 42f * scale)
            )
            drawOval(
                color = calfColor,
                topLeft = Offset(cx + 5f * scale, 224f * scale),
                size = Size(20f * scale, 42f * scale)
            )
        }
    }
}

@Composable
private fun MachineBlueprintCanvas(
    blueprintType: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.padding(16.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val strokeColor = ElectricCyan
        val secondaryLine = RoyalBlue400
        val frameColor = Color(0xFF1D4ED8)

        // Technical Grid
        val gridColor = Color(0xFF1E3A8A).copy(alpha = 0.3f)
        var gx = 0f
        while (gx < size.width) {
            drawLine(gridColor, Offset(gx, 0f), Offset(gx, size.height), strokeWidth = 1f)
            gx += 24f
        }
        var gy = 0f
        while (gy < size.height) {
            drawLine(gridColor, Offset(0f, gy), Offset(size.width, gy), strokeWidth = 1f)
            gy += 24f
        }

        // Draw machine blueprint based on type
        when (blueprintType.uppercase()) {
            "CABLE_ROW", "LAT_PULLDOWN", "CABLE_PULLEY" -> {
                // Frame Uprights
                drawLine(frameColor, Offset(cx - 80f, cy + 90f), Offset(cx - 80f, cy - 90f), strokeWidth = 8f)
                drawLine(frameColor, Offset(cx + 80f, cy + 90f), Offset(cx + 80f, cy - 90f), strokeWidth = 8f)
                drawLine(frameColor, Offset(cx - 90f, cy - 90f), Offset(cx + 90f, cy - 90f), strokeWidth = 8f)
                drawLine(frameColor, Offset(cx - 90f, cy + 90f), Offset(cx + 90f, cy + 90f), strokeWidth = 8f)

                // Pulley wheels
                drawCircle(ElectricCyan, radius = 12f, center = Offset(cx, cy - 80f))
                drawCircle(ElectricCyan, radius = 12f, center = Offset(cx, cy + 10f))

                // Cable path (Glowing Cyan line)
                drawLine(
                    color = ElectricCyan,
                    start = Offset(cx - 50f, cy + 50f),
                    end = Offset(cx, cy - 80f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = ElectricCyan,
                    start = Offset(cx, cy - 80f),
                    end = Offset(cx, cy + 10f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = ElectricCyan,
                    start = Offset(cx, cy + 10f),
                    end = Offset(cx + 40f, cy - 20f),
                    strokeWidth = 3f
                )

                // Weight Stack Plates
                for (i in 0..5) {
                    drawRoundRect(
                        color = RoyalBlue500,
                        topLeft = Offset(cx - 65f, cy + (10f + i * 12f)),
                        size = Size(30f, 8f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                }

                // Handle attachment
                drawCircle(Color.White, radius = 5f, center = Offset(cx + 40f, cy - 20f))
            }
            "LEG_PRESS" -> {
                // Incline 45 degree track
                drawLine(frameColor, Offset(cx - 100f, cy + 80f), Offset(cx + 80f, cy - 80f), strokeWidth = 8f)
                drawLine(frameColor, Offset(cx - 60f, cy + 100f), Offset(cx + 100f, cy - 60f), strokeWidth = 8f)

                // Sled Platform
                drawRoundRect(
                    color = ElectricCyan,
                    topLeft = Offset(cx + 30f, cy - 50f),
                    size = Size(40f, 40f),
                    cornerRadius = CornerRadius(4f, 4f)
                )

                // Backrest seat
                drawLine(RoyalBlue400, Offset(cx - 80f, cy + 60f), Offset(cx - 40f, cy + 80f), strokeWidth = 10f)
                drawLine(RoyalBlue400, Offset(cx - 80f, cy + 60f), Offset(cx - 100f, cy + 10f), strokeWidth = 8f)
            }
            "BENCH_PRESS", "PEC_FLY" -> {
                // Flat Bench
                drawRoundRect(
                    color = frameColor,
                    topLeft = Offset(cx - 80f, cy + 20f),
                    size = Size(160f, 16f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                // Legs
                drawLine(frameColor, Offset(cx - 60f, cy + 36f), Offset(cx - 60f, cy + 80f), strokeWidth = 8f)
                drawLine(frameColor, Offset(cx + 60f, cy + 36f), Offset(cx + 60f, cy + 80f), strokeWidth = 8f)

                // Upright Rack
                drawLine(frameColor, Offset(cx - 50f, cy + 80f), Offset(cx - 50f, cy - 60f), strokeWidth = 6f)
                drawLine(frameColor, Offset(cx + 50f, cy + 80f), Offset(cx + 50f, cy - 60f), strokeWidth = 6f)

                // Barbell
                drawLine(ElectricCyan, Offset(cx - 110f, cy - 50f), Offset(cx + 110f, cy - 50f), strokeWidth = 4f)
                // Plates
                drawRoundRect(RoyalBlue500, Offset(cx - 95f, cy - 70f), Size(12f, 40f), CornerRadius(2f, 2f))
                drawRoundRect(RoyalBlue500, Offset(cx + 83f, cy - 70f), Size(12f, 40f), CornerRadius(2f, 2f))
            }
            else -> {
                // Generic Barbell / Dumbbell Blueprint
                drawLine(ElectricCyan, Offset(cx - 100f, cy), Offset(cx + 100f, cy), strokeWidth = 6f)
                // Left Plates
                drawRoundRect(RoyalBlue500, Offset(cx - 90f, cy - 35f), Size(16f, 70f), CornerRadius(3f, 3f))
                drawRoundRect(RoyalBlue400, Offset(cx - 70f, cy - 25f), Size(12f, 50f), CornerRadius(3f, 3f))
                // Right Plates
                drawRoundRect(RoyalBlue400, Offset(cx + 58f, cy - 25f), Size(12f, 50f), CornerRadius(3f, 3f))
                drawRoundRect(RoyalBlue500, Offset(cx + 74f, cy - 35f), Size(16f, 70f), CornerRadius(3f, 3f))

                // Knurling Center
                drawLine(Color.White, Offset(cx - 25f, cy), Offset(cx + 25f, cy), strokeWidth = 8f)
            }
        }

        // Blueprint technical markers
        drawCircle(ElectricCyan, radius = 3f, center = Offset(cx - 110f, cy - 100f))
        drawCircle(ElectricCyan, radius = 3f, center = Offset(cx + 110f, cy - 100f))
        drawCircle(ElectricCyan, radius = 3f, center = Offset(cx - 110f, cy + 100f))
        drawCircle(ElectricCyan, radius = 3f, center = Offset(cx + 110f, cy + 100f))
    }
}
