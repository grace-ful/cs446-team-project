package com.example.cs446_fit4me.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.cs446_fit4me.model.WorkoutModel

@Composable
fun WorkoutPreviewDialog(
    workout: WorkoutModel,
    isCustom: Boolean,
    onClose: () -> Unit,
    onStart: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showDeleteWorkoutDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height(500.dp)
        ) {
            Box(Modifier.fillMaxSize()) {
                // Main content in a Column
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    // Top bar: emoji and close
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "🏋️",
                            fontSize = 40.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    // Name
                    Text(
                        text = workout.name,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = if (isCustom) 1 else Int.MAX_VALUE,
                        overflow = if (isCustom) TextOverflow.Ellipsis else TextOverflow.Clip,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )

                    // Custom: buttons under name
                    if (isCustom) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                        ) {
                            Button(
                                onClick = onEdit,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                                Spacer(Modifier.width(4.dp))
                                Text("Edit")
                            }
                            Button(
                                onClick = { showDeleteWorkoutDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                Spacer(Modifier.width(4.dp))
                                Text("Delete", color = Color.Red)
                            }
                        }
                    } else {
                        Spacer(Modifier.height(8.dp))
                    }

                    // Divider
                    Divider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.09f)
                    )
                    Spacer(Modifier.height(8.dp))

                    // Exercises Section: fixed height, scrolls if long!
                    Text(
                        "Exercises:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(4.dp))

                    // --- Begin Fading Scrollable List ---
                    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f) // Take up all remaining vertical space
                    ) {
                        if (workout.exercises.isEmpty()) {
                            Text(
                                "No exercises. Add some!",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                contentPadding = PaddingValues(bottom = 32.dp)
                            ) {
                                itemsIndexed(workout.exercises) { idx, ex ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 7.dp)
                                    ) {
                                        Text(
                                            text = "${idx + 1}.",
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.width(26.dp)
                                        )
                                        Text(
                                            text = ex.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                            // Fade gradient overlay
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .align(Alignment.BottomCenter)
                                    .drawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    surfaceColor
                                                )
                                            )
                                        )
                                    }
                            )
                        }
                    }
                    Spacer(Modifier.height(23.dp))
                    // End Fading Scrollable List
                }

                // Fixed button at bottom
                Button(
                    onClick = onStart,
                    enabled = workout.exercises.isNotEmpty(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Start Workout", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    // Confirm Delete for custom only
    if (showDeleteWorkoutDialog && isCustom) {
        AlertDialog(
            onDismissRequest = { showDeleteWorkoutDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteWorkoutDialog = false
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWorkoutDialog = false }) { Text("Cancel") }
            },
            title = { Text("Delete Workout?") },
            text = { Text("Are you sure you want to delete this workout? This cannot be undone.") }
        )
    }
}
