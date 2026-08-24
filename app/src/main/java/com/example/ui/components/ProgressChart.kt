package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorderBlue
import com.example.ui.theme.DarkSlateElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricRoyalBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldPR
import com.example.ui.theme.RoyalBlue200
import com.example.ui.theme.RoyalBlue300
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.RoyalBlue800
import com.example.ui.theme.RoyalBlue900
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChartDataPoint(
    val timestamp: Long,
    val value: Double,
    val label: String = ""
)

enum class TimePeriodFilter(val label: String, val days: Int) {
    WEEK("7d", 7),
    MONTH("1m", 30),
    THREE_MONTHS("3m", 90),
    SIX_MONTHS("6m", 180),
    ALL("all", 3650)
}

@Composable
fun WeightProgressChart(
    dataPoints: List<ChartDataPoint>,
    goalWeight: Double? = null,
    title: String = "bodyweight trend",
    unit: String = "lbs",
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(TimePeriodFilter.ALL) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var windowOffset by remember { mutableIntStateOf(0) } // For sliding window across data
    val windowSize = 8 // Maximum nodes shown per viewport to prevent compression crowding

    val dateFormat = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }

    // Filter data points by selected time period
    val now = System.currentTimeMillis()
    val filteredPoints = remember(dataPoints, selectedFilter) {
        if (selectedFilter == TimePeriodFilter.ALL) {
            dataPoints
        } else {
            val cutoff = now - (selectedFilter.days.toLong() * 86_400_000L)
            dataPoints.filter { it.timestamp >= cutoff }
        }
    }

    // Windowed subset of points for the chart to maintain readable spacing
    val maxOffset = (filteredPoints.size - windowSize).coerceAtLeast(0)
    val currentOffset = windowOffset.coerceIn(0, maxOffset)
    val visiblePoints = remember(filteredPoints, currentOffset) {
        if (filteredPoints.size <= windowSize) {
            filteredPoints
        } else {
            filteredPoints.drop(currentOffset).take(windowSize)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorderBlue, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSlateElevated),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header & Current Stat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title.lowercase(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    if (filteredPoints.isNotEmpty()) {
                        val latest = filteredPoints.last().value
                        val earliest = filteredPoints.first().value
                        val delta = earliest - latest
                        val isLoss = delta >= 0
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${String.format(Locale.US, "%.1f", latest)} $unit",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = ElectricCyan
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isLoss) EmeraldSuccess.copy(alpha = 0.2f) else RoyalBlue800
                            ) {
                                Text(
                                    text = if (isLoss) "-${String.format(Locale.US, "%.1f", delta)} $unit" else "+${String.format(Locale.US, "%.1f", -delta)} $unit",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isLoss) EmeraldSuccess else RoyalBlue300,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Selected Node Inspector
                if (selectedIndex != null && selectedIndex!! in visiblePoints.indices) {
                    val pt = visiblePoints[selectedIndex!!]
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "${String.format(Locale.US, "%.1f", pt.value)} $unit",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ElectricCyan
                            )
                            Text(
                                text = dateFormat.format(Date(pt.timestamp)).lowercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time Period Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TimePeriodFilter.values().forEach { filter ->
                    val isSel = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) ElectricRoyalBlue else RoyalBlue900,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) ElectricCyan else DarkCardBorderBlue
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedFilter = filter
                                windowOffset = 0
                                selectedIndex = null
                            }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSel) Color.White else TextMutedDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Window Navigation Controls (if total points > window size)
            if (filteredPoints.size > windowSize) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (windowOffset > 0) windowOffset -= 2 },
                        enabled = windowOffset > 0,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "scroll earlier",
                            tint = if (windowOffset > 0) ElectricCyan else TextMutedDark
                        )
                    }

                    Text(
                        text = "showing ${currentOffset + 1}–${(currentOffset + visiblePoints.size).coerceAtMost(filteredPoints.size)} of ${filteredPoints.size} entries",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark
                    )

                    IconButton(
                        onClick = { if (windowOffset < maxOffset) windowOffset += 2 },
                        enabled = windowOffset < maxOffset,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "scroll later",
                            tint = if (windowOffset < maxOffset) ElectricCyan else TextMutedDark
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Line Chart Canvas
            if (visiblePoints.size < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(0xFF070D18), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "log at least 2 entries to view weight trend",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedDark
                    )
                }
            } else {
                val minVal = (visiblePoints.minOf { it.value } - 1.5).coerceAtLeast(0.0)
                val maxVal = visiblePoints.maxOf { it.value } + 1.5
                val range = (maxVal - minVal).coerceAtLeast(1.0)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color(0xFF070D18), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(136.dp)
                            .pointerInput(visiblePoints) {
                                detectTapGestures { offset ->
                                    val stepX = size.width / (visiblePoints.size - 1)
                                    val tappedIndex = ((offset.x + stepX / 2) / stepX).toInt().coerceIn(0, visiblePoints.size - 1)
                                    selectedIndex = tappedIndex
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height
                        val stepX = w / (visiblePoints.size - 1)

                        // Draw Grid Horizontal Lines
                        val gridLines = 3
                        for (i in 0..gridLines) {
                            val y = h * (i.toFloat() / gridLines)
                            drawLine(
                                color = Color(0xFF1E3A8A).copy(alpha = 0.25f),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 1f
                            )
                        }

                        // Compute Points
                        val points = visiblePoints.mapIndexed { idx, dp ->
                            val x = idx * stepX
                            val normalizedY = ((dp.value - minVal) / range).toFloat()
                            val y = h - (normalizedY * h)
                            Offset(x, y)
                        }

                        // Draw Gradient Area Under Curve
                        val fillPath = Path().apply {
                            moveTo(points.first().x, h)
                            lineTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val curr = points[i]
                                val cx = (prev.x + curr.x) / 2f
                                cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                            }
                            lineTo(points.last().x, h)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ElectricCyan.copy(alpha = 0.3f),
                                    RoyalBlue600.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )

                        // Draw Smooth Stroke Curve
                        val strokePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val curr = points[i]
                                val cx = (prev.x + curr.x) / 2f
                                cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                            }
                        }

                        drawPath(
                            path = strokePath,
                            color = ElectricCyan,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw Circular Data Nodes
                        points.forEachIndexed { index, pt ->
                            val isSelected = selectedIndex == index
                            drawCircle(
                                color = if (isSelected) Color.White else RoyalBlue800,
                                radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = if (isSelected) ElectricCyan else Color.White,
                                radius = if (isSelected) 3.5.dp.toPx() else 2.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-Axis date labels
            if (visiblePoints.size >= 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dateFormat.format(Date(visiblePoints.first().timestamp)).lowercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark
                    )
                    if (visiblePoints.size > 2) {
                        Text(
                            text = dateFormat.format(Date(visiblePoints[visiblePoints.size / 2].timestamp)).lowercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark
                        )
                    }
                    Text(
                        text = dateFormat.format(Date(visiblePoints.last().timestamp)).lowercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark
                    )
                }
            }
        }
    }
}

@Composable
fun StrengthProgressionChart(
    exerciseName: String,
    dataPoints: List<ChartDataPoint>,
    unit: String = "lbs",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorderBlue, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSlateElevated),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "${exerciseName.lowercase()} strength curve",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (dataPoints.size < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color(0xFF070D18), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "no past logs recorded for this exercise yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedDark
                    )
                }
            } else {
                val minVal = (dataPoints.minOf { it.value } - 5.0).coerceAtLeast(0.0)
                val maxVal = dataPoints.maxOf { it.value } + 5.0
                val range = (maxVal - minVal).coerceAtLeast(1.0)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color(0xFF070D18), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                        val w = size.width
                        val h = size.height
                        val stepX = w / (dataPoints.size - 1)

                        val points = dataPoints.mapIndexed { idx, dp ->
                            val x = idx * stepX
                            val normalizedY = ((dp.value - minVal) / range).toFloat()
                            val y = h - (normalizedY * h)
                            Offset(x, y)
                        }

                        val strokePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val curr = points[i]
                                val cx = (prev.x + curr.x) / 2f
                                cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                            }
                        }

                        drawPath(
                            path = strokePath,
                            color = GoldPR,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        points.forEach { pt ->
                            drawCircle(color = GoldPR, radius = 3.5.dp.toPx(), center = pt)
                        }
                    }
                }
            }
        }
    }
}
