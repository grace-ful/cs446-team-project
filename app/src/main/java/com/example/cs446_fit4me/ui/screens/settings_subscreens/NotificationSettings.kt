package com.example.cs446_fit4me.ui.screens.settings_subscreens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme

@Composable
fun NotificationSettingsScreen(navController: NavController) {
    SettingsSubScreenTemplate("Notification Settings", navController) {
        Text("Notification Settings Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationSettingsScreenPreview() {
    CS446fit4meTheme {
        val navController = rememberNavController()
        NotificationSettingsScreen(navController = navController)
    }
}