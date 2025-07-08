package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme

@Composable
fun HomeScreen(navController: NavController? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to the Home Screen!",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Button(
            onClick = { navController?.navigate("workout") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Workout")
        }

        Button(
            onClick = { navController?.navigate("exercises") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Exercises")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CS446fit4meTheme {
        HomeScreen()
    }
}
