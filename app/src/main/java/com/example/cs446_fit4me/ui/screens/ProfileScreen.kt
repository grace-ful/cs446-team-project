package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme

@Composable
fun ProfileScreen(navController: NavController? = null) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Welcome to the Profile Screen!")
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    CS446fit4meTheme {
        ProfileScreen()
    }
}