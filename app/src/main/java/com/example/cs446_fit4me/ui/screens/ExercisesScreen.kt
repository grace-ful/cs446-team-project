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
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch


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
    var userExercisesLoaded by remember { mutableStateOf(false) }


    var filterButtonSize by remember { mutableStateOf(IntSize.Zero) }

    var myExercises by remember { mutableStateOf(listOf<Exercise>()) }

    var editingExercise by remember { mutableStateOf<Exercise?>(null) }


    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.exerciseApiService.getGeneralExercises()
            println(response)
            allExercises = response.map { exerciseTemplate ->
                exerciseTemplate.toExercise()

            }
            println(allExercises)
            isLoading = false
            userExercisesLoaded = false
        } catch (_: Exception) {
            errorMessage = "Failed to load exercises"
            isLoading = false
        }
    }

    // Fetch user exercises when "My Exercises" tab is selected
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 1 && !userExercisesLoaded) {
            try {
                val userId = "621b6f5d-aa5d-422b-bd15-87f23724396c"
                val response = ApiClient.exerciseApiService.getUserExercises(userId)
                println(response)
                myExercises = response.map { it.toExercise() }
                println(myExercises)
                userExercisesLoaded = true
            } catch (e: Exception) {
                println("Failed to load user exercises: ${e.message}")
            }
        }
    }


    val mockExercises = allExercises.sortedBy { it.name }

    // Pick base list depending on selected tab

    var showCreateModal by remember { mutableStateOf(false) }

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
                modifier = Modifier.weight(1f),
                onEditClick = { editingExercise = it },
                isEditable = selectedTabIndex == 1
            )
        }

        if (showCreateModal || editingExercise != null) {
            CreateExerciseModal(
                bodyParts = BodyPart.values().toList(),
                equipmentList = Equipment.values().toList(),
                initialExercise = editingExercise,
                onDismiss = {
                    showCreateModal = false
                    editingExercise = null
                },
                onExerciseCreated = { created ->
                    myExercises = myExercises + created
                    selectedTabIndex = 1
                },
                onExerciseUpdated = { updated ->
                    myExercises = myExercises.map {
                        if (it.name == updated.name) updated else it
                    }
                    selectedTabIndex = 1
                }
            )
        }

    }
}


@Composable
fun CreateExerciseModal(
    bodyParts: List<BodyPart>,
    equipmentList: List<Equipment>,
    initialExercise: Exercise? = null,
    onDismiss: () -> Unit,
    onExerciseCreated: (Exercise) -> Unit,
    onExerciseUpdated: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf(initialExercise?.name ?: "") }
    //var name by remember { mutableStateOf("") }
    var selectedBodyPart by remember { mutableStateOf(initialExercise?.bodyPart) }
    //var selectedBodyPart by remember { mutableStateOf<BodyPart?>(null) }
    var equipmentDropdownExpanded by remember { mutableStateOf(false) }
    var selectedEquipment by remember { mutableStateOf(initialExercise?.equipment) }
    //var selectedEquipment by remember { mutableStateOf<Equipment?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Create Custom Exercise",
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
                            modifier = Modifier.clickable { selectedBodyPart = bp }
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { equipmentDropdownExpanded = !equipmentDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedEquipment?.name?.replaceFirstChar { it.uppercase() } ?: "",
                        onValueChange = {},
                        label = { Text("Equipment") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = if (equipmentDropdownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = "Toggle Dropdown"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(color = Color.Transparent)
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
                        scope.launch {
                            isSaving = true
                            try {
                                if (initialExercise != null) {
                                    val updatedTemplate = ExerciseTemplate(
                                        id = initialExercise.id,
                                        name = name,
                                        muscleGroup = MuscleGroup.OTHER.name,
                                        bodyPart = selectedBodyPart!!.name,
                                        equipment = selectedEquipment!!.name,
                                        isGeneral = false,
                                        imageURL = initialExercise.imageUrl,
                                        createdAt = initialExercise.createdAt,
                                        userId = initialExercise.userId
                                    )
                                    val updated = ApiClient.exerciseApiService.updateExercise(initialExercise.id, updatedTemplate)
                                    onExerciseUpdated(updated.toExercise());
                                } else {
                                    val req = CreateExerciseRequest(
                                        name = name,
                                        muscleGroup = MuscleGroup.OTHER,
                                        bodyPart = selectedBodyPart!!,
                                        equipment = selectedEquipment!!,
                                        isGeneral = false,
                                        userId = "621b6f5d-aa5d-422b-bd15-87f23724396c"
                                    )
                                    val created = ApiClient.exerciseApiService.createExercise(req)
                                    onExerciseCreated(created.toExercise())
                                }
                            } catch (e: Exception) {
                                println("Error saving exercise: ${e.message}")
                            } finally {
                                isSaving = false
                                onDismiss()
                            }
                        }
                    },
                    enabled = !isSaving && selectedBodyPart != null && selectedEquipment != null && name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        when {
                            isSaving -> "Saving..."
                            initialExercise != null -> "Update"
                            else -> "Create"
                        }
                    )
                }

            }
        }
    }
}


@Composable
fun ExercisesList(exercises: List<Exercise>,
                  modifier: Modifier = Modifier,
                  onEditClick: (Exercise) -> Unit = {},
                  isEditable: Boolean = false) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(exercises) { index, exercise ->
            ExerciseListItem(
                exercise = exercise,
                onEditClick = if (isEditable && !exercise.isGeneric) { { onEditClick(exercise) } } else null
            )
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
