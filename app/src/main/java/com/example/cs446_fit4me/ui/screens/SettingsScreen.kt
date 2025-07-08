package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cs446_fit4me.ui.components.TopBar
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme
import com.example.cs446_fit4me.ui.screens.settings_subscreens.*


// Sealed class for navigation routes (as defined in step 1)
sealed class SettingsScreen(val route: String) {
    object EditAccountInfo : SettingsScreen("settings_main")
    object ChangePassword : SettingsScreen("account_settings")
    object NotificationSettings: SettingsScreen("notification_settings")
    object RemindMe: SettingsScreen("remind_me")
    object Units: SettingsScreen("units")
    object Accessibility: SettingsScreen("accessibility")
    object ProfileVisibility: SettingsScreen("profile_visibility")
    object MatchingPreferences: SettingsScreen("matching_preferences")
    object WorkoutHistory: SettingsScreen("workout_history")
    object Rate: SettingsScreen("rate")
    object HelpSupport: SettingsScreen("help_support")
    object Logout: SettingsScreen("logout")
    object DeleteAccount: SettingsScreen("delete_account")
    // Add others as needed, e.g., Logout
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(title = "Settings", canNavigateBack = true, onNavigateUp = { navController.popBackStack() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Icon")
                },
                placeholder = { Text("Search settings") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // ACCOUNT SETTINGS
            SettingsSectionHeader("ACCOUNT SETTINGS")
            SettingsItem("Edit Account Info") {
                //navController.navigate(SettingsScreen.EditAccountInfo.route)
            }
            SettingsItem("Change Password") {
                //navController.navigate(SettingsScreen.ChangePassword.route) // Add this route if needed
            }

            // NOTIFICATIONS
            SettingsSectionHeader("NOTIFICATIONS")
            SettingsItem("Notification Settings") {
                //navController.navigate(SettingsScreen.NotificationSettings.route)
            }
            SettingsItem("Remind Me") {
                //navController.navigate(SettingsScreen.RemindMe.route) // Add this route if needed
            }

            // APPEARANCE
            SettingsSectionHeader("APPEARANCE")
            SettingsItem("Units") {
                //navController.navigate(SettingsScreen.Units.route)
            }
            SettingsItem("Accessibility") {
                //navController.navigate(SettingsScreen.Accessibility.route)
            }

            // PRIVACY
            SettingsSectionHeader("PRIVACY")
            SettingsItem("Profile Visibility") {
                //navController.navigate(SettingsScreen.ProfileVisibility.route)
            }
            SettingsItem("Matching Preferences") {
                //navController.navigate(SettingsScreen.MatchingPreferences.route)
            }
            SettingsItem("Workout History") {
                //navController.navigate(SettingsScreen.WorkoutHistory.route)
            }

            // SUPPORT
            SettingsSectionHeader("SUPPORT")
            SettingsItem("Rate") {
                //navController.navigate(SettingsScreen.Rate.route)
            }
            SettingsItem("Help Center") {
                //navController.navigate(SettingsScreen.HelpSupport.route)
            }

            // DANGER ZONE
            SettingsSectionHeader("DANGER ZONE")
            SettingsItem("Logout", textStyle = TextStyle(color = MaterialTheme.colorScheme.error)) {
                //navController.navigate(SettingsScreen.Logout.route)
            }
            SettingsItem("Delete Account", textStyle = TextStyle(color = MaterialTheme.colorScheme.error)) {
                //navController.navigate(SettingsScreen.DeleteAccount.route)
            }
        }
    }
}


@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontSize = 14.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .padding(top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(text: String, textStyle: TextStyle? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = textStyle ?: MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Navigate to $text",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsMainScreenPreview() {
    CS446fit4meTheme {
        // You need a NavController for the preview, use rememberNavController
        val navController = rememberNavController()
        SettingsMainScreen(navController = navController)
    }
}

