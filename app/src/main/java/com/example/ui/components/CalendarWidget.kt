package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkoutSessionEntity
import com.example.ui.theme.CobaltAccent
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldPR
import com.example.ui.theme.RoyalBlue400
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.RoyalBlue800
import com.example.ui.theme.RoyalBlue900
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ConsistencyCalendarView(
    workoutSessions: List<WorkoutSessionEntity>,
    onSelectDate: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var calendarMonth by remember {
        mutableStateOf(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        })
    }

    var selectedDayEpochDay by remember {
        val today = Calendar.getInstance()
        mutableStateOf(today.get(Calendar.DAY_OF_YEAR) + today.get(Calendar.YEAR) * 366)
    }

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val dayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")

    // Map workouts by (Year * 366 + DayOfYear)
    val workoutDayMap = remember(workoutSessions) {
        val map = mutableMapOf<Int, MutableList<WorkoutSessionEntity>>()
        val cal = Calendar.getInstance()
        workoutSessions.forEach { session ->
            cal.timeInMillis = session.dateMillis
            val key = cal.get(Calendar.DAY_OF_YEAR) + cal.get(Calendar.YEAR) * 366
            map.getOrPut(key) { mutableListOf() }.add(session)
        }
        map
    }

    // Calculate Consistency Stats
    val totalWorkoutsThisMonth = remember(workoutSessions, calendarMonth) {
        val curYear = calendarMonth.get(Calendar.YEAR)
        val curMonth = calendarMonth.get(Calendar.MONTH)
        val cal = Calendar.getInstance()
        workoutSessions.count {
            cal.timeInMillis = it.dateMillis
            cal.get(Calendar.YEAR) == curYear && cal.get(Calendar.MONTH) == curMonth
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, RoyalBlue700.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with month navigation & Streak pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val newCal = calendarMonth.clone() as Calendar
                            newCal.add(Calendar.MONTH, -1)
                            calendarMonth = newCal
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = RoyalBlue300,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = monthYearFormat.format(calendarMonth.time),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = {
                            val newCal = calendarMonth.clone() as Calendar
                            newCal.add(Calendar.MONTH, 1)
                            calendarMonth = newCal
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = RoyalBlue300,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RoyalBlue800,
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue600)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = GoldPR,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalWorkoutsThisMonth sessions",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElectricCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day of Week Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dayHeaders.forEach { header ->
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = RoyalBlue300,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid computation
            val firstDayOfWeek = calendarMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0-based Sunday
            val daysInMonth = calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
            val todayCal = Calendar.getInstance()
            val todayYear = todayCal.get(Calendar.YEAR)
            val todayDayOfYear = todayCal.get(Calendar.DAY_OF_YEAR)
            val todayKey = todayDayOfYear + todayYear * 366

            val currentMonth = calendarMonth.get(Calendar.MONTH)
            val currentYear = calendarMonth.get(Calendar.YEAR)

            val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until (totalCells / 7)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (col in 0..6) {
                            val cellIndex = row * 7 + col
                            val dayNumber = cellIndex - firstDayOfWeek + 1

                            if (dayNumber in 1..daysInMonth) {
                                val cellCal = Calendar.getInstance().apply {
                                    set(Calendar.YEAR, currentYear)
                                    set(Calendar.MONTH, currentMonth)
                                    set(Calendar.DAY_OF_MONTH, dayNumber)
                                }
                                val cellKey = cellCal.get(Calendar.DAY_OF_YEAR) + currentYear * 366
                                val isToday = cellKey == todayKey
                                val isSelected = cellKey == selectedDayEpochDay
                                val dayWorkouts = workoutDayMap[cellKey] ?: emptyList()
                                val hasWorkout = dayWorkouts.isNotEmpty()

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            when {
                                                isSelected -> RoyalBlue600
                                                hasWorkout -> RoyalBlue800
                                                isToday -> RoyalBlue700.copy(alpha = 0.3f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .border(
                                            width = if (isToday && !isSelected) 1.5.dp else if (isSelected) 1.5.dp else 0.dp,
                                            color = if (isSelected) ElectricCyan else if (isToday) ElectricCyan.copy(alpha = 0.7f) else Color.Transparent,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedDayEpochDay = cellKey
                                            onSelectDate(cellCal.timeInMillis)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (hasWorkout || isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = when {
                                                isSelected -> Color.White
                                                hasWorkout -> ElectricCyan
                                                isToday -> Color.White
                                                else -> RoyalBlue300.copy(alpha = 0.7f)
                                            }
                                        )

                                        if (hasWorkout) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .background(
                                                        if (isSelected) Color.White else ElectricCyan,
                                                        CircleShape
                                                    )
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.size(38.dp))
                            }
                        }
                    }
                }
            }

            // Selected Day Workouts List
            val selectedWorkouts = workoutDayMap[selectedDayEpochDay] ?: emptyList()
            if (selectedWorkouts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Workouts on this day (${selectedWorkouts.size}):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = ElectricCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                selectedWorkouts.forEach { workout ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF070D18),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue800)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = workout.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${workout.durationMinutes} min • ${workout.totalSets} sets",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RoyalBlue300
                                    )
                                }
                            }
                            if (workout.totalVolumeLbs > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = RoyalBlue800
                                ) {
                                    Text(
                                        text = "${workout.totalVolumeLbs.toInt()} lbs vol",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ElectricCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
