package com.example.cs446_fit4me.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.ExerciseTemplate

@Composable
fun SelectExerciseScreen(
    navController: NavController,
    initiallySelected: List<ExerciseTemplate>,
    onExerciseSelected: (ExerciseTemplate) -> Unit,
    exercises: List<ExerciseTemplate> // <-- passed from viewModel
) {
    var searchText by remember { mutableStateOf("") }

    val selectedExercises = remember {
        mutableStateListOf<ExerciseTemplate>().apply {
            addAll(initiallySelected)
        }
    }

    val filteredExercises = exercises.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search exercises") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredExercises) { exercise ->
                val isSelected = selectedExercises.contains(exercise)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) {
                                selectedExercises.remove(exercise)
                            } else {
                                selectedExercises.add(exercise)
                            }
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isSelected, onCheckedChange = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(exercise.name)
                }
            }
        }

        Button(
            onClick = {
                selectedExercises.forEach { onExerciseSelected(it) }
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add ${selectedExercises.size} Exercises")
        }
    }
}