package com.example.cs446_fit4me.ui.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.cs446_fit4me.datastore.TokenManager
import com.example.cs446_fit4me.navigation.AppRoutes

// Sealed class for all setting items
sealed class SettingsItemEntry(
    val label: String,
    val route: String? = null,
    val isDangerous: Boolean = false
) {
    object EditAccountInfo : SettingsItemEntry("Edit Account Info", AppRoutes.EDIT_ACCOUNT_INFO)
    object ChangePassword : SettingsItemEntry("Change Password", AppRoutes.CHANGE_PASSWORD)
    object NotificationSettings : SettingsItemEntry("Notification Settings", AppRoutes.NOTIFICATION_SETTINGS)
    object RemindMe : SettingsItemEntry("Remind Me", AppRoutes.REMIND_ME)
    object Units : SettingsItemEntry("Units", AppRoutes.UNITS)
    object Accessibility : SettingsItemEntry("Accessibility", AppRoutes.ACCESSIBILITY)
    object ProfileVisibility : SettingsItemEntry("Profile Visibility", AppRoutes.PROFILE_VISIBILITY)
    object MatchingPreferences : SettingsItemEntry("Matching Preferences", AppRoutes.MATCHING_PREFERENCES)
    object WorkoutHistory : SettingsItemEntry("Workout History", AppRoutes.WORKOUT_HISTORY)
    object Rate : SettingsItemEntry("Rate", AppRoutes.RATE)
    object HelpSupport : SettingsItemEntry("Help Center", AppRoutes.HELP_SUPPORT)
    object Logout : SettingsItemEntry("Logout", isDangerous = true)
    object DeleteAccount : SettingsItemEntry("Delete Account", isDangerous = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen(navController: NavController, onLogout: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val settingsSections = listOf(
        "ACCOUNT SETTINGS" to listOf(
            SettingsItemEntry.EditAccountInfo,
            SettingsItemEntry.ChangePassword
        ),
        "NOTIFICATIONS" to listOf(
            SettingsItemEntry.NotificationSettings,
            SettingsItemEntry.RemindMe
        ),
        "APPEARANCE" to listOf(
            SettingsItemEntry.Units,
            SettingsItemEntry.Accessibility
        ),
        "PRIVACY" to listOf(
            SettingsItemEntry.ProfileVisibility,
            SettingsItemEntry.MatchingPreferences,
            SettingsItemEntry.WorkoutHistory
        ),
        "SUPPORT" to listOf(
            SettingsItemEntry.Rate,
            SettingsItemEntry.HelpSupport
        ),
        "DANGER ZONE" to listOf(
            SettingsItemEntry.Logout,
            SettingsItemEntry.DeleteAccount
        )
    )

    val filteredSections = settingsSections.mapNotNull { (header, items) ->
        val filteredItems = items.filter {
            it.label.contains(searchQuery, ignoreCase = true)
        }
        if (filteredItems.isNotEmpty()) header to filteredItems else null
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Settings",
                canNavigateBack = true,
                onNavigateUp = { navController.popBackStack() }
            )
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
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon"
                    )
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

            // Render Sections Dynamically
            filteredSections.forEach { (sectionTitle, items) ->
                SettingsSectionHeader(sectionTitle)
                items.forEach { item ->
                    SettingsItem(
                        text = item.label,
                        isDangerous = item.isDangerous,
                        onClick = {
                            when (item) {
                                SettingsItemEntry.Logout -> showLogoutDialog = true
                                SettingsItemEntry.DeleteAccount -> {
                                    // TODO: Implement delete confirmation dialog
                                }
                                else -> item.route?.let { navController.navigate(it) }
                            }
                        }
                    )
                }
            }
        }

        // Logout Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log out") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    Text(
                        "Yes",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                showLogoutDialog = false
                                try {
                                    TokenManager.clearToken(context = navController.context)
                                    onLogout()

                                } catch (e: Exception) {
                                    println("Logout failed: ${e.message}")
                                }
                            },
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                dismissButton = {
                    Text(
                        "No",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { showLogoutDialog = false },
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(
    text: String,
    isDangerous: Boolean = false,
    onClick: () -> Unit
) {
    val borderColor = if (isDangerous) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(
                if (isDangerous)
                    Modifier
                        .border(1.dp, borderColor, shape = MaterialTheme.shapes.small)
                        .padding(12.dp)
                else Modifier
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = if (isDangerous) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (!isDangerous) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Navigate to $text",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsMainScreenPreview() {
    CS446fit4meTheme {
        val navController = rememberNavController()
        SettingsMainScreen(navController = navController)
    }
}
