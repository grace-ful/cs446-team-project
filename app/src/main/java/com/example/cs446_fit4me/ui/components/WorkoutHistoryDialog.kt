package com.example.cs446_fit4me.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cs446_fit4me.model.ExerciseSessionUI
import com.example.cs446_fit4me.model.ExerciseSetUI
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutHistoryDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    workoutName: String,
    workoutDate: String,        // ISO string
    duration: Int?,             // in seconds
    exerciseSessions: List<ExerciseSessionUI>
) {
    if (!showDialog) return

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(0.98f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Row for close button only
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                // Workout name centered with extra top padding
                Text(
                    text = workoutName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp)
                )

                // Time, day, date centered
                Text(
                    text = formatFullDateTime(workoutDate),
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                // Pills for duration and total weight
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 7.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("\uD83D\uDD50", fontSize = 16.sp) // clock emoji
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = formatDuration(duration),
                                fontSize = 15.sp,
                                color = Color(0xFF1565C0),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    val totalWeight = calculateTotalWeight(exerciseSessions)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 7.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("\uD83C\uDFCB\uFE0F", fontSize = 16.sp) // weight lifter emoji
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${totalWeight.toInt()} lb",
                                fontSize = 15.sp,
                                color = Color(0xFFEF6C00),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Divider(thickness = 1.dp, color = Color.LightGray)
                Spacer(Modifier.height(10.dp))

                // Only THIS PART scrolls!
                val exercisesMaxHeight = screenHeight * 0.55f // ~55% of screen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = exercisesMaxHeight)
                        .verticalScroll(rememberScrollState())
                ) {
                    exerciseSessions.forEach { ex ->
                        // Each exercise in a box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 7.dp)
                                .background(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = ex.exerciseName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(5.dp))
                                ex.sets.forEachIndexed { i, set ->
                                    Text(
                                        text = "${i + 1}. ${formatSet(set)}",
                                        fontSize = 15.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

// Helpers

fun formatFullDateTime(isoString: String): String {
    // e.g., "2025-07-27T20:02:00Z" -> "8:02 PM, Saturday, July 27"
    return try {
        val instant = Instant.parse(isoString)
        val zoneId = ZoneId.systemDefault()
        val dateTime = ZonedDateTime.ofInstant(instant, zoneId)
        val time = dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
        val dayDate = dateTime.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
        "$time, $dayDate"
    } catch (e: Exception) {
        isoString
    }
}

fun calculateTotalWeight(exerciseSessions: List<ExerciseSessionUI>): Float {
    var total = 0f
    exerciseSessions.forEach { session ->
        session.sets.forEach { set ->
            if (set.weight != null) total += set.weight * set.reps
        }
    }
    return total
}

fun formatSet(set: ExerciseSetUI): String {
    val weightPart = set.weight?.let { "${it.toInt()} lb x " } ?: ""
    val repsPart = "${set.reps}"
    return "$weightPart$repsPart"
}
