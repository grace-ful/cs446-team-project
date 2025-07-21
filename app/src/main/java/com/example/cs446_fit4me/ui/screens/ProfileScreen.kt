package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme
import com.example.cs446_fit4me.network.ApiClient
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.cs446_fit4me.datastore.UserManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.cs446_fit4me.model.UpdateUserRequest
import com.example.cs446_fit4me.model.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager


fun filterDigits(input: String): String = input.filter { it.isDigit() }
fun filterFloatInput(input: String): String =
    input.filterIndexed { i, c -> c.isDigit() || (c == '.' && !input.take(i).contains('.')) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var heightFeet by remember { mutableStateOf("") }
    var heightInches by remember { mutableStateOf("") }
    var weightLbs by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var timePreference by remember { mutableStateOf("NONE") }
    var experienceLevel by remember { mutableStateOf("BEGINNER") }
    var gymFrequency by remember { mutableStateOf("NEVER") }

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    var hasLoadedInitialLocation by remember { mutableStateOf(false) }



    var originalState by remember {
        mutableStateOf(
            listOf(
                name, age, heightFeet, heightInches, weightLbs, location, email,
                timePreference, experienceLevel, gymFrequency
            )
        )
    }

    // Fetch user data on first composition
    LaunchedEffect(Unit) {
        try {
            loading = true
            val user = ApiClient.getUserApi(context).getUserById()

            name = user.name
            age = user.age.toString()

            // Backend stores height in inches
            val totalInches = user.heightCm
            heightFeet = (totalInches / 12).toString()
            heightInches = (totalInches % 12).toString()

            weightLbs = (user.weightKg * 2.20462f).toInt().toString()

            location = user.location ?: ""
            email = user.email ?: ""

            timePreference = user.timePreference.name
            experienceLevel = user.experienceLevel.name
            gymFrequency = user.gymFrequency.name

            originalState = listOf(
                name, age, heightFeet, heightInches, weightLbs, location, email,
                timePreference, experienceLevel, gymFrequency
            )

        } catch (e: Exception) {
            error = e.localizedMessage ?: "Failed to load profile."
        } finally {
            loading = false
        }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    LaunchedEffect(Unit) {
        delay(100)
        focusManager.clearFocus(force = true)
    }

    val isAgeValid = age.toIntOrNull()?.let { it in 5..120 } == true
    val isHeightValid = heightFeet.toIntOrNull()?.let { it in 3..8 } == true &&
            heightInches.toIntOrNull()?.let { it in 0..11 } == true
    val isWeightValid = weightLbs.toFloatOrNull()?.let { it in 30f..1000f } == true
    val isFormValid = name.isNotBlank() &&
            isAgeValid &&
            isHeightValid &&
            isWeightValid &&
            location.isNotBlank()

    val currentState = listOf(
        name, age, heightFeet, heightInches, weightLbs, location, email,
        timePreference, experienceLevel, gymFrequency
    )

    val isChanged = currentState != originalState

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EnumDropdown(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Welcome to Profile", style = MaterialTheme.typography.headlineMedium)
        }

        item {
            OutlinedTextField(name, { name = it }, label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth())
        }

        item {
            OutlinedTextField(
                value = age,
                onValueChange = { age = filterDigits(it) },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = heightFeet,
                    onValueChange = { heightFeet = filterDigits(it) },
                    label = { Text("Height (ft)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = heightInches,
                    onValueChange = { heightInches = filterDigits(it) },
                    label = { Text("Height (in)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        item {
            OutlinedTextField(
                value = weightLbs,
                onValueChange = { weightLbs = filterFloatInput(it) },
                label = { Text("Weight (lbs)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            val keyboardController = LocalSoftwareKeyboardController.current
            val placesClient = remember { com.google.android.libraries.places.api.Places.createClient(context) }
            val cityFocusRequester = remember { FocusRequester() }

            var selectedCountry by remember { mutableStateOf("CA") }
            var cityQuery by remember { mutableStateOf("") }
            var cityPredictions by remember { mutableStateOf(listOf<String>()) }
            var isDropdownExpanded by remember { mutableStateOf(false) }
            var hasUserSelected by remember { mutableStateOf(false) }

            // Initial load: split location into country + city if possible
            LaunchedEffect(location) {
                if (location.contains(",")) {
                    val parts = location.split(",").map { it.trim() }
                    cityQuery = parts.getOrNull(0) ?: ""
                    selectedCountry = parts.getOrNull(1) ?: "CA"
                } else {
                    cityQuery = location
                }
            }

            // Debounced API query
            LaunchedEffect(cityQuery) {
                if (hasLoadedInitialLocation && cityQuery.isNotBlank() && !hasUserSelected) {
                    delay(300)
                    val request = com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest.builder()
                        .setCountries(listOf(selectedCountry))
                        .setTypesFilter(listOf("locality"))
                        .setQuery(cityQuery)
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            cityPredictions = response.autocompletePredictions.map { it.getPrimaryText(null).toString() }
                            isDropdownExpanded = cityPredictions.isNotEmpty()
                        }
                        .addOnFailureListener {
                            cityPredictions = listOf()
                            isDropdownExpanded = false
                        }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EnumDropdown("Country", listOf("CA", "US"), selectedCountry) {
                    selectedCountry = it
                }

                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = cityQuery,
                        onValueChange = {
                            cityQuery = it
                            hasUserSelected = false
                            hasLoadedInitialLocation = true
                        },
                        label = { Text("City") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .focusRequester(cityFocusRequester),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        },
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        cityPredictions.forEach { prediction ->
                            DropdownMenuItem(
                                text = { Text(prediction) },
                                onClick = {
                                    cityQuery = prediction
                                    hasUserSelected = true
                                    isDropdownExpanded = false
                                    cityPredictions = listOf()
                                    location = "$prediction, $selectedCountry"
                                    keyboardController?.hide()
                                }
                            )
                        }
                    }
                }
            }
        }


        item {
            EnumDropdown("Time Preference", listOf("NONE", "MORNING", "AFTERNOON", "EVENING", "NIGHT"), timePreference) {
                timePreference = it
            }
        }

        item {
            EnumDropdown("Experience Level", listOf("BEGINNER", "INTERMEDIATE", "ADVANCED", "ATHLETE", "COACH"), experienceLevel) {
                experienceLevel = it
            }
        }

        item {
            EnumDropdown(
                "Gym Frequency",
                listOf("NEVER", "RARELY", "OCCASIONALLY", "REGULARLY", "FREQUENTLY", "DAILY"),
                gymFrequency
            ) {
                gymFrequency = it
            }
        }

        item {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val currentUserId = UserManager.getUserId(context)
                            if (currentUserId != null) {

                                val totalInches = (heightFeet.toIntOrNull() ?: 0) * 12 + (heightInches.toIntOrNull() ?: 0)
                                val weightKg = (weightLbs.toFloatOrNull() ?: 0f) * 0.453592f

                                val updateRequest = UpdateUserRequest(
                                    name = name.trim(),
                                    age = age.toIntOrNull(),
                                    heightCm = totalInches,
                                    weightKg = weightKg,
                                    location = location.trim(),
                                    timePreference = TimePreference.valueOf(timePreference),
                                    experienceLevel = ExperienceLevel.valueOf(experienceLevel),
                                    gymFrequency = GymFrequency.valueOf(gymFrequency)
                                )

                                if (isChanged) {
                                    val response = ApiClient.getUserApi(context).updateUser(
                                        userId = currentUserId,
                                        updateData = updateRequest
                                    )
                                    println("✅ API response: $response")
                                    println("✅ Profile update success")

                                    originalState = listOf(
                                        name, age, heightFeet, heightInches, weightLbs, location, email,
                                        timePreference, experienceLevel, gymFrequency
                                    )

                                    focusManager.clearFocus(force = true)
                                } else {
                                    println("⚠️ No changes to update (skipping API call)")
                                }
                            } else {
                                println("❌ No userId found — cannot send update")
                            }
                        } catch (e: Exception) {
                            println("❌ Profile update error: ${e.localizedMessage}")
                            e.printStackTrace()
                        }
                    }
                },

                enabled = isFormValid && isChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Update Profile")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    CS446fit4meTheme {
        ProfileScreen()
    }
}
