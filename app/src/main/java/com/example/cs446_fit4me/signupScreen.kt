package com.example.cs446_fit4me

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

fun filterDigits(input: String): String = input.filter { it.isDigit() }
fun filterFloatInput(input: String): String =
    input.filterIndexed { i, c -> c.isDigit() || (c == '.' && !input.take(i).contains('.')) }

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var heightFeet by remember { mutableStateOf("") }
    var heightInches by remember { mutableStateOf("") }
    var weightLbs by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var timePreference by remember { mutableStateOf("NONE") }
    var experienceLevel by remember { mutableStateOf("BEGINNER") }
    var gymFrequency by remember { mutableStateOf("NEVER") }

    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

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

    val auth = FirebaseAuth.getInstance()

    fun signUp() {
        submitted = true
        if (!isFormValid) return

        val formattedHeight = "${heightFeet.trim()}'${heightInches.trim()}\""
        val parsedWeight = weightLbs.trim().toFloatOrNull() ?: 0f

        isLoading = true
        error = null

        if (isFormValid) {
            onSignUpSuccess()
        }
//        auth.createUserWithEmailAndPassword(email.trim(), password)
//            .addOnCompleteListener { task ->
//                isLoading = false
//                if (task.isSuccessful) {
//                    // Save name, formattedHeight, parsedWeight, etc.
//                    onSignUpSuccess()
//                } else {
//                    error = task.exception?.localizedMessage ?: "Signup failed"
//                }
//            }
    }

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
            Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        }

        item {
            OutlinedTextField(name, { name = it }, label = { Text("Full Name") },
                isError = submitted && name.isBlank(), modifier = Modifier.fillMaxWidth())
            if (submitted && name.isBlank()) {
                Text("Name is required", color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            OutlinedTextField(
                value = age,
                onValueChange = { age = filterDigits(it) },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = submitted && !isAgeValid,
                modifier = Modifier.fillMaxWidth()
            )
            if (submitted && !isAgeValid) {
                Text("Enter a valid age", color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = heightFeet,
                    onValueChange = { heightFeet = filterDigits(it) },
                    label = { Text("Height (ft)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = submitted && heightFeet.toIntOrNull() == null
                )
                OutlinedTextField(
                    value = heightInches,
                    onValueChange = { heightInches = filterDigits(it) },
                    label = { Text("Height (in)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = submitted && heightInches.toIntOrNull() == null
                )
            }
            if (submitted && !isHeightValid) {
                Text("Enter valid height (e.g., 5'10\")", color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            OutlinedTextField(
                value = weightLbs,
                onValueChange = { weightLbs = filterFloatInput(it) },
                label = { Text("Weight (lbs)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = submitted && !isWeightValid
            )
            if (submitted && !isWeightValid) {
                Text("Enter valid weight (e.g., 145)", color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            OutlinedTextField(location, { location = it }, label = { Text("Location") },
                isError = submitted && location.isBlank(), modifier = Modifier.fillMaxWidth())
            if (submitted && location.isBlank()) {
                Text("Location is required", color = MaterialTheme.colorScheme.error)
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
            EnumDropdown("Gym Frequency", listOf("NEVER", "RARELY", "OCCASIONALLY", "REGULARLY", "FREQUENTLY", "DAILY"), gymFrequency) {
                gymFrequency = it
            }
        }

        item {
            OutlinedTextField(email, { email = it }, label = { Text("Email") },
                isError = submitted && email.isBlank(), modifier = Modifier.fillMaxWidth())
            if (submitted && email.isBlank()) {
                Text("Email is required", color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            OutlinedTextField(password, { password = it }, label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = submitted && password.isBlank(),
                modifier = Modifier.fillMaxWidth())
            if (submitted && password.isBlank()) {
                Text("Password is required", color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            OutlinedTextField(confirmPassword, { confirmPassword = it }, label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = submitted && (!isPasswordMatch || confirmPassword.isBlank()),
                modifier = Modifier.fillMaxWidth())
            if (submitted && confirmPassword.isBlank()) {
                Text("Please confirm password", color = MaterialTheme.colorScheme.error)
            } else if (submitted && !isPasswordMatch) {
                Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            Button(
                onClick = { signUp() },
                enabled = !isLoading && isFormValid,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(if (isLoading) "Signing up..." else "Sign Up")
            }
        }

        item {
            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Log in")
            }
        }

        if (error != null) {
            item {
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

