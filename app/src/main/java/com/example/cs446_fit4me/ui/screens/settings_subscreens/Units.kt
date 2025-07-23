package com.example.cs446_fit4me.ui.screens.settings_subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme

@Composable
fun UnitsScreen(navController: NavController) {
    var selectedWeightUnit by remember { mutableStateOf("Pounds") }
    var selectedLengthUnit by remember { mutableStateOf("Feet") }

    SettingsSubScreenTemplate("Units", navController) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Weight Section
            Text("Weight", style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UnitToggleButton(
                    label = "Pounds",
                    isSelected = selectedWeightUnit == "Pounds",
                    onClick = { selectedWeightUnit = "Pounds" }
                )
                UnitToggleButton(
                    label = "Kilos",
                    isSelected = selectedWeightUnit == "Kilos",
                    onClick = { selectedWeightUnit = "Kilos" }
                )
            }

            // Length Section
            Text("Length", style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UnitToggleButton(
                    label = "Feet",
                    isSelected = selectedLengthUnit == "Feet",
                    onClick = { selectedLengthUnit = "Feet" }
                )
                UnitToggleButton(
                    label = "Centimeters",
                    isSelected = selectedLengthUnit == "Centimeters",
                    onClick = { selectedLengthUnit = "Centimeters" }
                )
            }
        }
    }
}

@Composable
fun UnitToggleButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    if (isSelected) {
        Button(onClick = onClick) {
            Text(label)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(label)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnitsScreenPreview() {
    CS446fit4meTheme {
        val navController = rememberNavController()
        UnitsScreen(navController = navController)
    }
}
