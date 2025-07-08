package com.example.cs446_fit4me.ui.screens.settings_subscreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cs446_fit4me.ui.screens.*

@Composable
fun SettingsNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SettingsScreen.EditAccountInfo.route // or a main hub like "settings_home"
    ) {
        composable(SettingsScreen.EditAccountInfo.route) {
            EditAccountInfoScreen(navController)
        }
        composable(SettingsScreen.ChangePassword.route) {
            ChangePasswordScreen(navController)
        }
        composable(SettingsScreen.NotificationSettings.route) {
            NotificationSettingsScreen(navController)
        }
        composable(SettingsScreen.RemindMe.route) {
            RemindMeScreen(navController)
        }
        composable(SettingsScreen.Units.route) {
            UnitsScreen(navController)
        }
        composable(SettingsScreen.Accessibility.route) {
            AccessibilityScreen(navController)
        }
        composable(SettingsScreen.ProfileVisibility.route) {
            ProfileVisibilityScreen(navController)
        }
        composable(SettingsScreen.MatchingPreferences.route) {
            MatchingPreferencesScreen(navController)
        }
        composable(SettingsScreen.WorkoutHistory.route) {
            WorkoutHistoryScreen(navController)
        }
        composable(SettingsScreen.Rate.route) {
            RateScreen(navController)
        }
        composable(SettingsScreen.HelpSupport.route) {
            HelpSupportScreen(navController)
        }
        composable(SettingsScreen.Logout.route) {
            LogoutScreen(navController)
        }
        composable(SettingsScreen.DeleteAccount.route) {
            DeleteAccountScreen(navController)
        }
    }
}
