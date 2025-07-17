package com.example.cs446_fit4me.ui.screens

import android.util.Log
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
import androidx.compose.material3.AlertDialog
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
import com.example.cs446_fit4me.datastore.TokenManager
import com.example.cs446_fit4me.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen(navController: NavController, onLogout: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                navController.navigate(AppRoutes.EDIT_ACCOUNT_INFO)
            }
            SettingsItem("Change Password") {
                navController.navigate(AppRoutes.CHANGE_PASSWORD)
            }

            // NOTIFICATIONS
            SettingsSectionHeader("NOTIFICATIONS")
            SettingsItem("Notification Settings") {
                navController.navigate(AppRoutes.NOTIFICATION_SETTINGS)
            }
            SettingsItem("Remind Me") {
                navController.navigate(AppRoutes.REMIND_ME)
            }

            // APPEARANCE
            SettingsSectionHeader("APPEARANCE")
            SettingsItem("Units") {
                navController.navigate(AppRoutes.UNITS)
            }
            SettingsItem("Accessibility") {
                navController.navigate(AppRoutes.ACCESSIBILITY)
            }

            // PRIVACY
            SettingsSectionHeader("PRIVACY")
            SettingsItem("Profile Visibility") {
                navController.navigate(AppRoutes.PROFILE_VISIBILITY)
            }
            SettingsItem("Matching Preferences") {
                navController.navigate(AppRoutes.MATCHING_PREFERENCES)
            }
            SettingsItem("Workout History") {
                navController.navigate(AppRoutes.WORKOUT_HISTORY)
            }

            // SUPPORT
            SettingsSectionHeader("SUPPORT")
            SettingsItem("Rate") {
                navController.navigate(AppRoutes.RATE)
            }
            SettingsItem("Help Center") {
                navController.navigate(AppRoutes.HELP_SUPPORT)
            }

            // DANGER ZONE
            SettingsSectionHeader("DANGER ZONE")
            SettingsItem("Logout", textStyle = TextStyle(color = MaterialTheme.colorScheme.error)) {
                showLogoutDialog = true
            }
            SettingsItem("Delete Account", textStyle = TextStyle(color = MaterialTheme.colorScheme.error)) {

            }
        }
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
                                    println("Settings Screen - Logout navigation failed: ${e.message}")
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
                            .clickable {
                                showLogoutDialog = false
                            },
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

