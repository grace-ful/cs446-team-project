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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme
import com.example.cs446_fit4me.datastore.UserPreferencesManager
import com.example.cs446_fit4me.network.ApiClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.example.cs446_fit4me.model.UpdateUserRequest



fun filterDigits(input: String): String = input.filter { it.isDigit() }
fun filterFloatInput(input: String): String =
    input.filterIndexed { i, c -> c.isDigit() || (c == '.' && !input.take(i).contains('.')) }

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesManager(context) }

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var heightFeet by remember { mutableStateOf("") }
    var heightInches by remember { mutableStateOf("") }
    var weightLbs by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var timePreference by remember { mutableStateOf("NONE") }
    var experienceLevel by remember { mutableStateOf("BEGINNER") }
    var gymFrequency by remember { mutableStateOf("NEVER") }

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Fetch user data on first composition
    LaunchedEffect(Unit) {
        userPrefs.userIdFlow.collectLatest { userId ->
            if (userId != null) {
                try {
                    val user = ApiClient.userApiService.getUserById(userId)
                    name = user.name ?: ""
                    age = user.age.toString()
                    val totalInches = user.heightCm // <- remember, heightCm holds inches as per your backend setup
                    heightFeet = (totalInches / 12).toString()
                    heightInches = (totalInches % 12).toString()
                    weightLbs = (user.weightKg * 2.20462f).toInt().toString()
                    location = user.location ?: ""
                    email = user.email ?: ""
                    timePreference = user.timePreference ?: "NONE"
                    experienceLevel = user.experienceLevel ?: "BEGINNER"
                    gymFrequency = user.gymFrequency ?: "NEVER"
                } catch (e: Exception) {
                    error = e.localizedMessage ?: "Failed to load profile."
                } finally {
                    loading = false
                }
            }
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

    val originalState = remember {
        listOf(
            name, age, heightFeet, heightInches, weightLbs, location, email,
            timePreference, experienceLevel, gymFrequency, password, confirmPassword
        )
    }

    val isAgeValid = age.toIntOrNull()?.let { it in 5..120 } == true
    val isHeightValid = heightFeet.toIntOrNull()?.let { it in 3..8 } == true &&
            heightInches.toIntOrNull()?.let { it in 0..11 } == true
    val isWeightValid = weightLbs.toFloatOrNull()?.let { it in 30f..1000f } == true
    val isPasswordMatch = password == confirmPassword
    val isFormValid = name.isNotBlank() &&
            isAgeValid &&
            isHeightValid &&
            isWeightValid &&
            location.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            isPasswordMatch

    val currentState = listOf(
        name, age, heightFeet, heightInches, weightLbs, location, email,
        timePreference, experienceLevel, gymFrequency, password, confirmPassword
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
            OutlinedTextField(location, { location = it }, label = { Text("Location") },
                modifier = Modifier.fillMaxWidth())
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
            EnumDropdown("Gym Frequency", listOf("NEVER", "RARELY", "OCCASIONALLY", "REGULARLY", "FREQUENTLY", "DAILY"), gymFrequency) {
                gymFrequency = it
            }
        }

        item {
            OutlinedTextField(email, { email = it }, label = { Text("Email") },
                modifier = Modifier.fillMaxWidth())
        }

        item {
            OutlinedTextField(password, { password = it }, label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth())
        }

        item {
            OutlinedTextField(confirmPassword, { confirmPassword = it }, label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth())
        }

        item {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val userId = userPrefs.userIdFlow.firstOrNull()
                            if (userId != null) {
                                val updatedFields = mutableMapOf<String, Any>()

                                if (name != originalState[0]) updatedFields["name"] = name.trim()
                                if (age != originalState[1]) updatedFields["age"] = age.toIntOrNull() ?: 0

                                val totalInches = (heightFeet.toIntOrNull() ?: 0) * 12 + (heightInches.toIntOrNull() ?: 0)
                                val originalTotalInches = (originalState[2].toIntOrNull() ?: 0) * 12 + (originalState[3].toIntOrNull() ?: 0)
                                if (totalInches != originalTotalInches) updatedFields["heightCm"] = totalInches

                                val weightKg = (weightLbs.toFloatOrNull() ?: 0f) * 0.453592f
                                val originalWeightKg = (originalState[4].toFloatOrNull() ?: 0f) * 0.453592f
                                if (weightKg != originalWeightKg) updatedFields["weightKg"] = weightKg

                                if (location != originalState[5]) updatedFields["location"] = location.trim()
                                if (timePreference != originalState[7]) updatedFields["timePreference"] = timePreference
                                if (experienceLevel != originalState[8]) updatedFields["experienceLevel"] = experienceLevel
                                if (gymFrequency != originalState[9]) updatedFields["gymFrequency"] = gymFrequency

                                if (password.isNotBlank() && password == confirmPassword) {
                                    updatedFields["password"] = password
                                }

                                val updateRequest = UpdateUserRequest(
                                    name = if (updatedFields.containsKey("name")) name.trim() else null,
                                    age = if (updatedFields.containsKey("age")) age.toIntOrNull() else null,
                                    heightCm = if (updatedFields.containsKey("heightCm")) totalInches else null,
                                    weightKg = if (updatedFields.containsKey("weightKg")) weightKg else null,
                                    location = if (updatedFields.containsKey("location")) location.trim() else null,
                                    timePreference = if (updatedFields.containsKey("timePreference")) timePreference else null,
                                    experienceLevel = if (updatedFields.containsKey("experienceLevel")) experienceLevel else null,
                                    gymFrequency = if (updatedFields.containsKey("gymFrequency")) gymFrequency else null,
                                    password = if (updatedFields.containsKey("password")) password else null
                                )


                                if (updatedFields.isNotEmpty()) {
                                    ApiClient.userApiService.updateUser(
                                        userId = userId,
                                        updateData = updateRequest
                                    )
                                    println("✅ Profile update success")
                                } else {
                                    println("⚠️ No changes to update")
                                }
                            }
                        } catch (e: Exception) {
                            println("❌ Profile update error: ${e.localizedMessage}")
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
