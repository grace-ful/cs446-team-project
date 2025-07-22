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
fun NotificationSettingsScreen(navController: NavController) {
    var isNotificationsEnabled by remember { mutableStateOf(true) }  // default ON

    SettingsSubScreenTemplate("Notification Settings", navController) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Receive Notifications",
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = isNotificationsEnabled,
                onCheckedChange = { isNotificationsEnabled = it }
            )
        }
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
