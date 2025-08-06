package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.ExerciseTemplate
import com.example.cs446_fit4me.model.WorkoutModel
import com.example.cs446_fit4me.ui.components.ExerciseListItem
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.model.ExerciseIdListRequest
import com.example.cs446_fit4me.model.RemoveExerciseRequest
import com.example.cs446_fit4me.model.UpdateWorkoutNameRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkoutScreen(
    navController: NavController,
    workoutToEdit: WorkoutModel,
    onSave: (WorkoutModel) -> Unit = {}
) {
    var workoutName by remember { mutableStateOf(workoutToEdit.name) }
    val exercises = remember { mutableStateListOf<ExerciseTemplate>().apply { addAll(workoutToEdit.exercises) } }

    val context = LocalContext.current

    // Handle exercises returned from SelectExercisesScreen
    val navBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(navBackStackEntry?.savedStateHandle?.get<ArrayList<ExerciseTemplate>>("selectedExercises")) {
        val returnedExercises = navBackStackEntry
            ?.savedStateHandle
            ?.get<ArrayList<ExerciseTemplate>>("selectedExercises")
        if (returnedExercises != null) {
            exercises.clear()
            exercises.addAll(returnedExercises)
            navBackStackEntry.savedStateHandle.remove<ArrayList<ExerciseTemplate>>("selectedExercises")
        }
    }

    val canSave = workoutName.isNotBlank() && exercises.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (canSave) {
                                val oldIds = workoutToEdit.exercises.map { it.id }.toSet()
                                val newIds = exercises.map { it.id }.toSet()
                                val added = newIds - oldIds
                                val removed = oldIds - newIds

                                val api = ApiClient.getWorkoutApi(context)
                                // Launch backend updates in coroutine
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        // Add new exercises (if any)
                                        if (added.isNotEmpty()) {
                                            api.addExercisesToTemplate(
                                                workoutToEdit.id,
                                                ExerciseIdListRequest(added.toList())
                                            )
                                        }
                                        // Remove exercises (if any)
                                        for (removedId in removed) {
                                            api.removeExerciseFromTemplate(
                                                workoutToEdit.id,
                                                RemoveExerciseRequest(removedId)
                                            )
                                        }
                                        // handle workout name update
                                        if (workoutName != workoutToEdit.name) {
                                            api.updateWorkoutTemplateName(
                                                workoutToEdit.id,
                                                UpdateWorkoutNameRequest(workoutName)
                                            )
                                        }

                                        // Update UI on main thread
                                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                                            onSave(
                                                workoutToEdit.copy(
                                                    name = workoutName,
                                                    exercises = exercises.toList()
                                                )
                                            )
                                            navController.popBackStack()
                                        }
                                    } catch (e: Exception) {
                                        // Optionally handle error
                                    }
                                }
                            }
                        },
                        enabled = canSave,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canSave) MaterialTheme.colorScheme.primary else Color.Gray,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(10.dp))
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                OutlinedTextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    label = { Text("Workout Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                )
            }

            Button(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle
                        ?.set("selectedExercises", ArrayList(exercises))
                    navController.navigate("select_exercise")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(Modifier.width(8.dp))
                Text("Add Exercises")
            }

            Spacer(Modifier.height(18.dp))
            Text(
                "Exercises:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))

            if (exercises.isEmpty()) {
                Text(
                    "No exercises in this workout.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 14.dp)
                )
            } else {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(exercises) { ex ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp, max = 78.dp),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ExerciseListItem(
                                    exercise = ex.toExercise(),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { exercises.remove(ex) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Remove Exercise"
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

// Helper to convert ExerciseTemplate to Exercise (if needed)
fun ExerciseTemplate.toExercise() = com.example.cs446_fit4me.model.Exercise(
    id = this.id,
    name = this.name,
    muscleGroup = com.example.cs446_fit4me.model.MuscleGroup.valueOf(this.muscleGroup),
    bodyPart = com.example.cs446_fit4me.model.BodyPart.valueOf(this.bodyPart),
    equipment = com.example.cs446_fit4me.model.Equipment.valueOf(this.equipment),
    description = "",
    isGeneric = this.isGeneral,
    imageUrl = this.imageURL
)
