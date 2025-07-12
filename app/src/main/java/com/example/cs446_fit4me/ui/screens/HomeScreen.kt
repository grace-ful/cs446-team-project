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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.unit.sp
import com.example.cs446_fit4me.navigation.BottomNavItem


@Composable
fun HomeScreen(navController: NavController? = null, username: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Profile button in top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
                .clickable {
                    navController?.navigate(BottomNavItem.Profile.route)
                },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = username.first().uppercaseChar().toString(),
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(48.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize(),
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
}



@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CS446fit4meTheme {
        HomeScreen(username = "Yash")
    }
}
