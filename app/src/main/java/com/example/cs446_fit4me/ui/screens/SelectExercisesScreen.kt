package com.example.cs446_fit4me.ui.workout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
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

@Composable
fun SelectExerciseScreen(
    navController: NavController,
    exercises: List<ExerciseTemplate>,
//    onExerciseSelected: (ExerciseTemplate) -> Unit, // Probably not using it
) {
    // CHANGED: Get selected exercises from previous back stack entry
    val initiallySelected = remember {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<ArrayList<ExerciseTemplate>>("selectedExercises")
            ?.toList() ?: emptyList()
    }

    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // KEY: Maintain and update the selected exercises
    val selectedExercises = remember { mutableStateListOf<ExerciseTemplate>() }
    // CHANGED: Use LaunchedEffect to update whenever initiallySelected changes
    LaunchedEffect(initiallySelected) {
        selectedExercises.clear()
        selectedExercises.addAll(initiallySelected)
    }

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
            } catch (e: Exception) {
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
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                if (isSelected) selectedExercises.remove(ex) else selectedExercises.add(ex)
                            }
                            .then(
                                if (isSelected)
                                    Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                                else Modifier
                            ),
                        color = Color.Transparent,
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        ExerciseListItem(
                            exercise = ex.toExercise(),
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        // ADD EXERCISES BUTTON
        Button(
            onClick = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selectedExercises", ArrayList(selectedExercises))
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


@Composable
fun FilterButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
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

// Helper to convert ExerciseTemplate to Exercise for ExerciseListItem
fun ExerciseTemplate.toExercise(): Exercise {
    return Exercise(
        id = this.id,
        name = this.name,
        muscleGroup = MuscleGroup.valueOf(this.muscleGroup),
        bodyPart = BodyPart.valueOf(this.bodyPart),
        equipment = Equipment.valueOf(this.equipment),
        description = "",
        isGeneric = this.isGeneral,
        imageUrl = this.imageURL
    )
}
