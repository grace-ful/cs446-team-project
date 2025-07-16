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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.ui.unit.sp
import com.example.cs446_fit4me.navigation.BottomNavItem


@Composable
fun HomeScreen(navController: NavController? = null, username: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with avatar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hey $username 👋", style = MaterialTheme.typography.headlineMedium)

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        navController?.navigate(BottomNavItem.Profile.route)
                    }
            ) {
                Box(Modifier.wrapContentSize(Alignment.Center)) {
                    Text(
                        text = username.first().uppercaseChar().toString(),
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Today's Workout card
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController?.navigate("workout") }
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Today's Workout", style = MaterialTheme.typography.titleMedium)
                Text("Leg Day • 5 exercises • 45 mins", style = MaterialTheme.typography.bodyMedium)
                Text("Tap to continue ➡️", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatBox("🔥", "Streak", "4 days")
            StatBox("📈", "Level", "Beginner")
            StatBox("⚖️", "Calories", "1150 kcal")
        }

        // Quick links
        Text("Quick Access", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickButton("Workouts") { navController?.navigate("workout") }
            QuickButton("Exercises") { navController?.navigate("exercises") }
            QuickButton("Messages") { navController?.navigate("messages") }
            QuickButton("Match") { navController?.navigate("find_match") }
        }

        // Matches preview (placeholder for now)
        Text("Suggested Matches", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MatchChip("Tom")
            MatchChip("Sara")
            MatchChip("+ Find more")
        }

        // Motivational quote
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("💪 Motivation")
                Text(
                    "“Success isn’t always about greatness. It’s about consistency.”",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun StatBox(emoji: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, style = MaterialTheme.typography.headlineMedium)
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun QuickButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier
            .height(40.dp)
    ) {
        Text(label)
    }
}

@Composable
fun MatchChip(name: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable { /* TODO: Match profile */ }
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}




@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CS446fit4meTheme {
        HomeScreen(username = "Yash")
    }
}
