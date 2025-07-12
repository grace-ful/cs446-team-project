package com.example.cs446_fit4me

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.datastore.TokenManager
import com.example.cs446_fit4me.datastore.UserPreferencesManager
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.network.ApiClient
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesManager(context) }

    var isProfileSetupScreen by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var age by remember { mutableStateOf("") }
    var heightFeet by remember { mutableStateOf("") }
    var heightInches by remember { mutableStateOf("") }
    var weightLbs by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var timePreference by remember { mutableStateOf(TimePreference.NONE) }
    var experienceLevel by remember { mutableStateOf(ExperienceLevel.BEGINNER) }
    var gymFrequency by remember { mutableStateOf(GymFrequency.NEVER) }

    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    val isProfileFormValid = age.toIntOrNull()?.let { it in 5..120 } == true &&
            heightFeet.toIntOrNull()?.let { it in 3..8 } == true &&
            heightInches.toIntOrNull()?.let { it in 0..11 } == true &&
            weightLbs.toFloatOrNull()?.let { it in 30f..1000f } == true &&
            location.isNotBlank()

    fun submitSignup() {
        val totalInches = (heightFeet.toIntOrNull() ?: 0) * 12 + (heightInches.toIntOrNull() ?: 0)
        val weightKg = (weightLbs.toFloatOrNull() ?: 0f) * 0.453592f

        scope.launch {
            isLoading = true
            error = null
            try {
                val request = SignupRequest(
                    email = email.trim(),
                    name = name.trim(),
                    password = password,
                    heightCm = totalInches,
                    weightKg = weightKg,
                    age = age.trim().toInt(),
                    location = location.trim(),
                    timePreference = timePreference,
                    experienceLevel = experienceLevel,
                    gymFrequency = gymFrequency
                )

                val response = ApiClient.getUserApi(context).signup(request)
                TokenManager.saveToken(context, response.token)
                userPrefs.saveUserId(response.id)

                isLoading = false
                onSignUpSuccess()
            } catch (e: Exception) {
                isLoading = false
                error = e.localizedMessage ?: "Signup failed"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isProfileSetupScreen) {
                ProfileSetupScreenContent(
                    age = age,
                    heightFeet = heightFeet,
                    heightInches = heightInches,
                    weightLbs = weightLbs,
                    location = location,
                    timePreference = timePreference,
                    experienceLevel = experienceLevel,
                    gymFrequency = gymFrequency,
                    isLoading = isLoading,
                    isValid = isProfileFormValid,
                    onAgeChange = { age = it },
                    onHeightFeetChange = { heightFeet = it },
                    onHeightInchesChange = { heightInches = it },
                    onWeightChange = { weightLbs = it },
                    onLocationChange = { location = it },
                    onTimePrefChange = { timePreference = it },
                    onExperienceLevelChange = { experienceLevel = it },
                    onGymFrequencyChange = { gymFrequency = it },
                    onBack = { isProfileSetupScreen = false },
                    onSubmit = { submitSignup() }
                )
            } else {
                BasicSignupScreenContent(
                    name = name,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    submitted = submitted,
                    onNameChange = { name = it },
                    onEmailChange = { email = it },
                    onPasswordChange = { password = it },
                    onConfirmPasswordChange = { confirmPassword = it },
                    onNext = { isProfileSetupScreen = true },
                    onNavigateToLogin = onNavigateToLogin
                )
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}



@Composable
fun BasicSignupScreenContent(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    submitted: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onNext: () -> Unit,
    onNavigateToLogin: () -> Unit   // 👈 ADD THIS PARAMETER
) {
    val isPasswordMatch = password == confirmPassword

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(name, onNameChange, label = { Text("Full Name") },
            isError = submitted && name.isBlank(), modifier = Modifier.fillMaxWidth())
        if (submitted && name.isBlank()) {
            Text("Name is required", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(email, onEmailChange, label = { Text("Email") },
            isError = submitted && email.isBlank(), modifier = Modifier.fillMaxWidth())
        if (submitted && email.isBlank()) {
            Text("Email is required", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(password, onPasswordChange, label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = submitted && password.isBlank(),
            modifier = Modifier.fillMaxWidth())
        if (submitted && password.isBlank()) {
            Text("Password is required", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(confirmPassword, onConfirmPasswordChange, label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = submitted && (!isPasswordMatch || confirmPassword.isBlank()),
            modifier = Modifier.fillMaxWidth())
        if (submitted && confirmPassword.isBlank()) {
            Text("Please confirm password", color = MaterialTheme.colorScheme.error)
        } else if (submitted && !isPasswordMatch) {
            Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Proceed to Profile Setup")
        }

        // 👇 ADD THIS AT THE END
        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Log in")
        }
    }
}


@Composable
fun ProfileSetupScreenContent(
    age: String,
    heightFeet: String,
    heightInches: String,
    weightLbs: String,
    location: String,
    timePreference: TimePreference,
    experienceLevel: ExperienceLevel,
    gymFrequency: GymFrequency,
    isLoading: Boolean,
    isValid: Boolean,
    onAgeChange: (String) -> Unit,
    onHeightFeetChange: (String) -> Unit,
    onHeightInchesChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onTimePrefChange: (TimePreference) -> Unit,
    onExperienceLevelChange: (ExperienceLevel) -> Unit,
    onGymFrequencyChange: (GymFrequency) -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile Setup", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(age, onAgeChange, label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = !isValid && age.toIntOrNull()?.let { it !in 5..120 } != false,
            modifier = Modifier.fillMaxWidth())

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(heightFeet, onHeightFeetChange, label = { Text("Height (ft)") },
                modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            OutlinedTextField(heightInches, onHeightInchesChange, label = { Text("Height (in)") },
                modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        OutlinedTextField(weightLbs, onWeightChange, label = { Text("Weight (lbs)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(location, onLocationChange, label = { Text("Location") },
            modifier = Modifier.fillMaxWidth())

        EnumDropdown("Time Preference", TimePreference.values().map { it.name }, timePreference.name) {
            onTimePrefChange(TimePreference.valueOf(    it))
        }
        EnumDropdown("Experience Level", ExperienceLevel.values().map { it.name }, experienceLevel.name) {
            onExperienceLevelChange(ExperienceLevel.valueOf(it))
        }
        EnumDropdown("Gym Frequency", GymFrequency.values().map { it.name }, gymFrequency.name) {
            onGymFrequencyChange(GymFrequency.valueOf(it))
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onBack, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Back")
            }
            Button(onClick = onSubmit, enabled = !isLoading, modifier = Modifier.weight(1f)) {
                Text(if (isLoading) "Signing up..." else "Complete Sign Up")
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnumDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
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


