package com.example.cs446_fit4me.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.tooling.preview.Preview

fun formatWorkoutDate(isoString: String): String {
    return try {
        val instant = Instant.parse(isoString)
        val zoneId = ZoneId.systemDefault()
        val date = ZonedDateTime.ofInstant(instant, zoneId)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
        date.format(formatter)
    } catch (e: Exception) {
        isoString
    }
}

fun formatDuration(seconds: Int?): String {
    if (seconds == null) return "--"
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    return buildString {
        if (h > 0) append("${h}h ")
        append("${m} mins")
    }
}

@Composable
fun WorkoutHistoryCard(
    workoutName: String,
    workoutDate: String,
    duration: Int?,
    exerciseNames: List<String>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Workout Name
            Text(
                text = workoutName,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(12.dp))

            // Day/Date and Duration in one Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formatWorkoutDate(workoutDate),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "\uD83D\uDD50", fontSize = 14.sp)
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = formatDuration(duration),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(thickness = 1.2.dp, color = Color.LightGray)
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Exercises:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(2.dp))

            // Plain text, each exercise on its own line
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                exerciseNames.forEach { name ->
                    Text(
                        text = name,
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutHistoryCardPreview() {
    WorkoutHistoryCard(
        workoutName = "Push Day (Custom)",
        workoutDate = "2025-07-26T10:05:00Z",
        duration = 3980,
        exerciseNames = listOf("Bench Press", "Overhead Press", "Triceps Dips", "Chest Fly"),
        onClick = {}
    )
}
