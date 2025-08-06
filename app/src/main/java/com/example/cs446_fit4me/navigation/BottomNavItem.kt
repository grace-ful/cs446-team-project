package com.example.cs446_fit4me.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Face // replace with actual icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.cs446_fit4me.ui.screens.HomeScreen
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme

// Sealed class to represent bottom navigation screens
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home, "Home")
    object Messages : BottomNavItem("messages", "Messages", Icons.Filled.Email, "Messages")
    object FindMatch : BottomNavItem("find_match", "Find A Gym Buddy", Icons.Filled.Face, "Find Match")
    object History : BottomNavItem("history", "See your History", Icons.Filled.History, "History")
    object Workout : BottomNavItem("workout", "Get those Gains!", Icons.Filled.FitnessCenter, "Workout")
}

fun getTitleByRoute(route: String?, items: List<BottomNavItem>): String {
    return items.find { it.route == route }?.title ?: "Fit4Me" // Default title
}

@Preview(showBackground = true)
@Composable
fun BottomNavItemPreview() {
    CS446fit4meTheme {
        BottomNavItem.Home
    }
}