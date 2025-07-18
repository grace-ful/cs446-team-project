package com.example.cs446_fit4me.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cs446_fit4me.model.ExerciseTemplate
import com.example.cs446_fit4me.model.WorkoutModel

@Composable
fun WorkoutPreviewDialog(
    workout: WorkoutModel,
    isCustom: Boolean,
    onClose: () -> Unit,
    onStart: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit // Now a simple callback to navigate to EditWorkoutScreen
) {
    var showDeleteWorkoutDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Top bar: Name, (X), Edit, Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        workout.name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                    if (isCustom) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Workout")
                        }
                        IconButton(onClick = { showDeleteWorkoutDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Workout")
                        }
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Exercises list
                Text(
                    "Exercises:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(4.dp))

                if (workout.exercises.isEmpty()) {
                    Text("No exercises. Add some!", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(min = 0.dp, max = 220.dp)
                    ) {
                        items(workout.exercises) { ex ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(ex.name, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                // Start Workout
                Button(
                    onClick = onStart,
                    enabled = workout.exercises.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Workout")
                }
            }
        }
    }

    // Confirm delete workout
    if (showDeleteWorkoutDialog) {
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
