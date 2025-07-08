package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cs446_fit4me.navigation.BottomNavItem
import com.example.cs446_fit4me.navigation.getTitleByRoute
import com.example.cs446_fit4me.ui.components.BottomNavigationBar
import com.example.cs446_fit4me.ui.components.TopBar
import com.example.cs446_fit4me.ui.screens.settings_subscreens.SettingsNavGraph

// Main screen that contains the bottom navigation bar and the navigation host
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem.Messages,
        BottomNavItem.FindMatch,
        BottomNavItem.Home,
        BottomNavItem.Workout,
        BottomNavItem.Profile
    )
    //SettingsNavGraph(navController);

    // Get current route to determine title and back navigation state
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreenTitle = getTitleByRoute(currentRoute, bottomNavItems)

    // Determine if back navigation is possible
    val canNavigateBack = navController.previousBackStackEntry != null

    Scaffold(
        topBar = {
            // Only show TopAppBar if the current route is one of the bottom nav items
            // May need to adjust this logic when we have more fragments
            if (bottomNavItems.any { it.route == currentRoute }) {
                TopBar(
                    title = currentScreenTitle,
                    canNavigateBack = canNavigateBack,
                    onNavigateUp = { navController.navigateUp() },
                    onSettingsClick = if (currentRoute == BottomNavItem.Home.route) {
                        { navController.navigate("settings") }
                    } else {
                        null
                    }
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, items = bottomNavItems)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen(navController) }
            composable(BottomNavItem.Messages.route) { MessagesScreen(navController) }
            composable(BottomNavItem.FindMatch.route) { FindMatchScreen(navController) }
            composable(BottomNavItem.Workout.route) { WorkoutScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }

            composable("settings") {
                SettingsMainScreen(navController)
            }
            composable("exercises") { ExercisesScreen(navController) }
        }
    }
}
