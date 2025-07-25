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
fun ProfileVisibilityScreen(navController: NavController) {
    var consentToMatching by remember { mutableStateOf(true) } // Default is Yes
    var genderPreference by remember { mutableStateOf("Same Gender") }

    SettingsSubScreenTemplate("Profile Visibility", navController) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Consent to Matching Section
            Text(
                "Consent to being matched with other Gym Buddies",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ConsentToggleButton(
                    label = "Yes, I Consent.",
                    isSelected = consentToMatching,
                    onClick = { consentToMatching = true }
                )
                ConsentToggleButton(
                    label = "No, I am good.",
                    isSelected = !consentToMatching,
                    onClick = { consentToMatching = false }
                )
            }

            // Gender Preference Section (only if consent is Yes)
            if (consentToMatching) {
                Text(
                    "Gender preference when matched",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConsentToggleButton(
                        label = "Same Gender",
                        isSelected = genderPreference == "Same Gender",
                        onClick = { genderPreference = "Same Gender" }
                    )
                    ConsentToggleButton(
                        label = "Open to All",
                        isSelected = genderPreference == "Open to All",
                        onClick = { genderPreference = "Open to All" }
                    )
                }
            }
        }
    }
}

@Composable
fun ConsentToggleButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
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
fun ProfileVisibilityScreenPreview() {
    CS446fit4meTheme {
        val navController = rememberNavController()
        ProfileVisibilityScreen(navController = navController)
    }
}
