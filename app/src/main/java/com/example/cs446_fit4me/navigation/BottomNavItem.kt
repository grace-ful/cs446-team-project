package com.example.cs446_fit4me.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Face // replace with actual icons
import androidx.compose.ui.graphics.vector.ImageVector

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
    object Workout : BottomNavItem("workout", "Get those Gainz!!!!", Icons.Filled.Add, "Workout")
    object Profile : BottomNavItem("profile", "Profile", Icons.Filled.AccountCircle, "Profile")
}

fun getTitleByRoute(route: String?, items: List<BottomNavItem>): String {
    return items.find { it.route == route }?.title ?: "Fit4Me" // Default title
}
