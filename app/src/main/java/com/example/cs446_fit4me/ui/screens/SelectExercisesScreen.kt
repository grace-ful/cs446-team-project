package com.example.cs446_fit4me.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.ExerciseTemplate
import com.example.cs446_fit4me.model.BodyPart
import com.example.cs446_fit4me.model.Equipment
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.platform.LocalDensity

@Composable
fun FilterButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Surface(
        modifier = modifier
            .height(36.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = text)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Filled.FilterList, contentDescription = "Filter Icon")
            }
        }
    }
}

@Composable
fun SelectExerciseScreen(
    navController: NavController,
    initiallySelected: List<ExerciseTemplate>,
    onExerciseSelected: (ExerciseTemplate) -> Unit,
    exercises: List<ExerciseTemplate>
) {
    var searchText by remember { mutableStateOf("") }

    val selectedExercises = remember {
        mutableStateListOf<ExerciseTemplate>().apply {
            addAll(initiallySelected)
        }
    }

    // 🧠 Filter state
    var selectedBodyParts by remember { mutableStateOf(setOf<BodyPart>()) }
    var selectedEquipments by remember { mutableStateOf(setOf<Equipment>()) }

    var bodyPartDropdownExpanded by remember { mutableStateOf(false) }
    var equipmentDropdownExpanded by remember { mutableStateOf(false) }
    var filterButtonSize by remember { mutableStateOf(IntSize.Zero) }

    val filteredExercises = exercises.filter { ex ->
        ex.name.contains(searchText, ignoreCase = true) &&
                (selectedBodyParts.isEmpty() || BodyPart.valueOf(ex.bodyPart) in selectedBodyParts) &&
                (selectedEquipments.isEmpty() || Equipment.valueOf(ex.equipment) in selectedEquipments)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search exercises") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔍 Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { filterButtonSize = it.size },
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Body Part
            Box(modifier = Modifier.weight(1f)) {
                FilterButton(
                    text = if (selectedBodyParts.isEmpty()) "Body Part" else "${selectedBodyParts.size} selected",
                    onClick = { bodyPartDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = bodyPartDropdownExpanded,
                    onDismissRequest = { bodyPartDropdownExpanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { filterButtonSize.width.toDp() })
                        .heightIn(max = 300.dp)
                ) {
                    BodyPart.values().forEach { bp ->
                        val isSelected = selectedBodyParts.contains(bp)
                        DropdownMenuItem(
                            onClick = {
                                selectedBodyParts = if (isSelected) selectedBodyParts - bp else selectedBodyParts + bp
                            },
                            text = {
                                Text(bp.name.replaceFirstChar { it.uppercase() })
                            }
                        )
                    }
                }
            }

            // Equipment
            Box(modifier = Modifier.weight(1f)) {
                FilterButton(
                    text = if (selectedEquipments.isEmpty()) "Equipment" else "${selectedEquipments.size} selected",
                    onClick = { equipmentDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = equipmentDropdownExpanded,
                    onDismissRequest = { equipmentDropdownExpanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { filterButtonSize.width.toDp() })
                        .heightIn(max = 300.dp)
                ) {
                    Equipment.values().forEach { eq ->
                        val isSelected = selectedEquipments.contains(eq)
                        DropdownMenuItem(
                            onClick = {
                                selectedEquipments = if (isSelected) selectedEquipments - eq else selectedEquipments + eq
                            },
                            text = {
                                Text(eq.name.replaceFirstChar { it.uppercase() })
                            }
                        )
                    }
                }
            }
        }

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
