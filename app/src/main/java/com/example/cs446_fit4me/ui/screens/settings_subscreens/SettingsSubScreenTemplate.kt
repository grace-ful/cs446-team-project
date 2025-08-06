package com.example.cs446_fit4me.ui.screens.settings_subscreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.cs446_fit4me.ui.components.TopBar

@Composable
fun SettingsSubScreenTemplate(
    title: String,
    navController: NavController,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                title = title,
                canNavigateBack = true,
                onNavigateUp = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
//            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
