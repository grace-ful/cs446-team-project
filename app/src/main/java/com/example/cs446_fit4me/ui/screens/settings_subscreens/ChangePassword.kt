package com.example.cs446_fit4me.ui.screens.settings_subscreens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme

@Composable
fun ChangePasswordScreen(navController: NavController) {
    SettingsSubScreenTemplate("Change Password", navController) {
        Text("Change Password Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    CS446fit4meTheme {
        val navController = rememberNavController()
        ChangePasswordScreen(navController = navController)
    }
}