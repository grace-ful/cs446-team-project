package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.ui.components.ExerciseListItem
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel


@Composable
fun SelectExerciseScreen(
    navController: NavController,
    exercises: List<ExerciseTemplate>,
    workoutViewModel: WorkoutViewModel
) {

    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val selectedExercises = workoutViewModel.selectedExercises

    // Filter states
    var selectedBodyParts by remember { mutableStateOf(setOf<BodyPart>()) }
    var selectedEquipments by remember { mutableStateOf(setOf<Equipment>()) }
    var bodyPartDropdownExpanded by remember { mutableStateOf(false) }
    var equipmentDropdownExpanded by remember { mutableStateOf(false) }
    var filterButtonSize by remember { mutableStateOf(IntSize.Zero) }

    // Backend for My Exercises (mirrors ExercisesScreen)
    var myExercises by remember { mutableStateOf<List<ExerciseTemplate>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var myExercisesLoaded by remember { mutableStateOf(false) }

    // Fetch my exercises on tab change to "My Exercises"
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 1 && !myExercisesLoaded) {
            isLoading = true
            try {
                val response = ApiClient.getExerciseApi(context).getUserExercises()
                myExercises = response
                myExercisesLoaded = true
            } catch (_: Exception) {
                errorMessage = "Failed to load your exercises"
            } finally {
                isLoading = false
            }
        }
    }

    // Pick base list depending on selected tab
    val baseExercises = if (selectedTabIndex == 0) exercises else myExercises

    // Filter and search
    val filteredExercises = baseExercises.filter { ex ->
        ex.name.contains(searchText, ignoreCase = true) &&
                (selectedBodyParts.isEmpty() || BodyPart.valueOf(ex.bodyPart) in selectedBodyParts) &&
                (selectedEquipments.isEmpty() || Equipment.valueOf(ex.equipment) in selectedEquipments)
    }

    // UI LAYOUT
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        // SEARCH BAR
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search exercises") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            shape = MaterialTheme.shapes.small
        )

        // FILTERS under search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .onGloballyPositioned { filterButtonSize = it.size },
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Body Part filter
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
                        .width(with(LocalDensity.current) { (filterButtonSize.width * 0.8f).toDp() })
                        .heightIn(max = 300.dp)
                ) {
                    BodyPart.values().forEach { bp ->
                        val isSelected = selectedBodyParts.contains(bp)
                        DropdownMenuItem(
                            onClick = {
                                selectedBodyParts = if (isSelected) selectedBodyParts - bp else selectedBodyParts + bp
                            },
                            text = {
                                Text(
                                    bp.name.replaceFirstChar { it.uppercase() },
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            modifier = if (isSelected)
                                Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            else Modifier
                        )
                    }
                }
            }

            // Equipment filter
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
                        .width(with(LocalDensity.current) { (filterButtonSize.width * 0.8f).toDp() })
                        .heightIn(max = 300.dp)
                ) {
                    Equipment.values().forEach { eq ->
                        val isSelected = selectedEquipments.contains(eq)
                        DropdownMenuItem(
                            onClick = {
                                selectedEquipments = if (isSelected) selectedEquipments - eq else selectedEquipments + eq
                            },
                            text = {
                                Text(
                                    eq.name.replaceFirstChar { it.uppercase() },
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            modifier = if (isSelected)
                                Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            else Modifier
                        )
                    }
                }
            }
        }

        // TABS under filters
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Preset Exercises") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("My Exercises") }
            )
        }

        // EXERCISE LIST
        if (isLoading && selectedTabIndex == 1) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMessage ?: "Error")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filteredExercises) { ex ->
                    val isSelected = selectedExercises.contains(ex)
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .clip(RoundedCornerShape(16.dp)),
                        color = Color.Transparent,
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    if (isSelected) selectedExercises.remove(ex) else selectedExercises.add(ex)
                                }
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                                    else Color.Transparent
                                )
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExerciseListItem(
                                exercise = ex.toExercise(),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    navController.navigate("exercise_detail/${ex.id}")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info, // You can use Info icon if you prefer
                                    contentDescription = "Exercise Info"
                                )
                            }
                        }
                    }
                }

            }
        }

        // ADD EXERCISES BUTTON
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = selectedExercises.isNotEmpty()
        ) {
            Text("Add ${selectedExercises.size} Exercises")
        }
    }
}

