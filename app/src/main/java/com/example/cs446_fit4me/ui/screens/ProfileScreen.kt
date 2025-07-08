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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme

fun filterDigits(input: String): String = input.filter { it.isDigit() }
fun filterFloatInput(input: String): String =
    input.filterIndexed { i, c -> c.isDigit() || (c == '.' && !input.take(i).contains('.')) }

@Composable
fun ProfileScreen() {
    var name by remember { mutableStateOf("John Doe") }
    var age by remember { mutableStateOf("25") }
    var heightFeet by remember { mutableStateOf("5") }
    var heightInches by remember { mutableStateOf("10") }
    var weightLbs by remember { mutableStateOf("170") }
    var location by remember { mutableStateOf("Toronto") }
    var email by remember { mutableStateOf("john@example.com") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var timePreference by remember { mutableStateOf("MORNING") }
    var experienceLevel by remember { mutableStateOf("BEGINNER") }
    var gymFrequency by remember { mutableStateOf("REGULARLY") }

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
//            confirmPassword.isNotBlank() &&
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
                onClick = { /* TODO: Save changes */ },
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
