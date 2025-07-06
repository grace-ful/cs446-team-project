package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.Exercise
import com.example.cs446_fit4me.model.BodyPart
import com.example.cs446_fit4me.model.Equipment
import com.example.cs446_fit4me.model.ExerciseTemplate
import com.example.cs446_fit4me.model.MuscleGroup
import com.example.cs446_fit4me.model.toExercise
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.network.ExerciseApiService
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.ui.components.ExerciseListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(navController: NavController? = null) {
    var searchText by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }

    var bodyPartDropdownExpanded by remember { mutableStateOf(false) }
    var selectedBodyParts by remember { mutableStateOf(setOf<BodyPart>()) }

    var equipmentDropdownExpanded by remember { mutableStateOf(false) }
    var selectedEquipments by remember { mutableStateOf(setOf<Equipment>()) }


		var allExercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
		var isLoading by remember { mutableStateOf(true) }
		var errorMessage by remember { mutableStateOf<String?>(null) }

    var filterButtonSize by remember { mutableStateOf(IntSize.Zero) }


		LaunchedEffect(Unit) {
				try {
						val response = ApiClient.exerciseApiService.getGeneralExercises()
                        println(response)
                        allExercises = response.map { exerciseTemplate ->
                            exerciseTemplate.toExercise()

                        }
                        println(allExercises)
						isLoading = false
				} catch (e: Exception) {
						errorMessage = "Failed to load exercises"
						isLoading = false
				}
		}


    val mockExercises = allExercises.sortedBy { it.name }

    // Pick base list depending on selected tab
    
    var showCreateModal by remember { mutableStateOf(false) }
    var myExercises by remember { mutableStateOf(listOf<Exercise>()) }

    val baseExercises = if (selectedTabIndex == 0) mockExercises else myExercises

    val filteredExercises = baseExercises.filter { exercise ->
        exercise.name.startsWith(searchText, ignoreCase = true) &&
                (selectedBodyParts.isEmpty() || exercise.bodyPart in selectedBodyParts) &&
                (selectedEquipments.isEmpty() || exercise.equipment in selectedEquipments)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Exercises") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { showCreateModal = true },
                        shape = RoundedCornerShape(50), // big rounding for pill shape
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 4.dp,
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = "New",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
                singleLine = true,
                shape = MaterialTheme.shapes.small
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Body Part Filter Button and Dropdown
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates: LayoutCoordinates ->
                            filterButtonSize = coordinates.size
                        }
                ) {
                    FilterButton(
                        text = if (selectedBodyParts.isEmpty()) "Body Part" else "${selectedBodyParts.size} selected",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { bodyPartDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = bodyPartDropdownExpanded,
                        onDismissRequest = { bodyPartDropdownExpanded = false },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { (filterButtonSize.width * 0.8f).toDp() })
                            .heightIn(max = 300.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        BodyPart.values().forEach { bodyPart ->
                            val isSelected = selectedBodyParts.contains(bodyPart)
                            DropdownMenuItem(
                                onClick = {
                                    selectedBodyParts = if (isSelected) {
                                        selectedBodyParts - bodyPart
                                    } else {
                                        selectedBodyParts + bodyPart
                                    }
                                },
                                text = {
                                    Text(
                                        bodyPart.name.replaceFirstChar { it.uppercase() },
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else LocalContentColor.current
                                    )
                                },
                                modifier = Modifier.background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }

                // Equipment Filter Button and Dropdown
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    FilterButton(
                        text = if (selectedEquipments.isEmpty()) "Equipment" else "${selectedEquipments.size} selected",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { equipmentDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = equipmentDropdownExpanded,
                        onDismissRequest = { equipmentDropdownExpanded = false },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { (filterButtonSize.width * 0.8f).toDp() })
                            .heightIn(max = 300.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Equipment.values().forEach { equipment ->
                            val isSelected = selectedEquipments.contains(equipment)
                            DropdownMenuItem(
                                onClick = {
                                    selectedEquipments = if (isSelected) {
                                        selectedEquipments - equipment
                                    } else {
                                        selectedEquipments + equipment
                                    }
                                },
                                text = {
                                    Text(
                                        equipment.name.replaceFirstChar { it.uppercase() },
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else LocalContentColor.current
                                    )
                                },
                                modifier = Modifier.background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }
            }

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

            ExercisesList(
                exercises = filteredExercises,
                modifier = Modifier.weight(1f)
            )
        }

        if (showCreateModal) {
            CreateExerciseModal(
                bodyParts = BodyPart.values().toList(),
                equipmentList = Equipment.values().toList(),
                onDismiss = { showCreateModal = false },
                onSave = { newExercise ->
                    myExercises = myExercises + newExercise
                    selectedTabIndex = 1
                    showCreateModal = false
                }
            )
        }
    }
}

@Composable
fun CreateExerciseModal(
    bodyParts: List<BodyPart>,
    equipmentList: List<Equipment>,
    onDismiss: () -> Unit,
    onSave: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedBodyPart by remember { mutableStateOf<BodyPart?>(null) }
    var equipmentDropdownExpanded by remember { mutableStateOf(false) }
    var selectedEquipment by remember { mutableStateOf<Equipment?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Create Custom Exercise",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Text("Body Part", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.heightIn(max = 150.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bodyParts) { bp ->
                        val isSelected = bp == selectedBodyPart
                        Surface(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { selectedBodyPart = bp }
                        ) {
                            Text(
                                bp.name.replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }


                Spacer(Modifier.height(16.dp))

                Text("Equipment", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                Box {
                    OutlinedTextField(
                        value = selectedEquipment?.name?.replaceFirstChar { it.uppercase() } ?: "",
                        onValueChange = { },
                        label = { Text("Equipment") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = if (equipmentDropdownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = "Toggle Dropdown",
                                Modifier.clickable { equipmentDropdownExpanded = !equipmentDropdownExpanded }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = equipmentDropdownExpanded,
                        onDismissRequest = { equipmentDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        equipmentList.forEach { eq ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedEquipment = eq
                                    equipmentDropdownExpanded = false
                                },
                                text = {
                                    Text(eq.name.replaceFirstChar { it.uppercase() })
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (selectedBodyPart != null && selectedEquipment != null && name.isNotBlank()) {
                            onSave(
                                Exercise(
                                    name = name,
                                    muscleGroup = MuscleGroup.CHEST,
                                    equipment = selectedEquipment!!,
                                    bodyPart = selectedBodyPart!!,
                                    description = "",
                                    isGeneric = false,
                                    imageUrl = null
                                )
                            )
                        }
                    },
                    enabled = selectedBodyPart != null && selectedEquipment != null && name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun ExercisesList(exercises: List<Exercise>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(exercises) { index, exercise ->
            ExerciseListItem(exercise = exercise)
            if (index < exercises.size - 1) {
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

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
