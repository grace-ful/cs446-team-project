package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cs446_fit4me.navigation.BottomNavItem
import com.example.cs446_fit4me.navigation.getTitleByRoute
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.ui.components.BottomNavigationBar
import com.example.cs446_fit4me.ui.components.TopBar
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.ui.viewmodel.MatchingViewModel
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel
import com.example.cs446_fit4me.ui.workout.CreateWorkoutScreen
import com.example.cs446_fit4me.ui.workout.SelectExerciseScreen


// Main screen that contains the bottom navigation bar and the navigation host
@Composable
fun MainScreen() {
    var userName by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        try {
            val user = ApiClient.getUserApi(context).getUserById()
            userName = user.name
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Failed to load user info"
        }
    }

    if (userName == null) return // or show loading spinner
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
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Home.route // ✅ fallback
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
        val workoutViewModel: WorkoutViewModel = viewModel()
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen(navController, username = userName!!) }
            composable(BottomNavItem.Messages.route) { MessagesScreen(navController) }
            composable(BottomNavItem.FindMatch.route) {
                val context = LocalContext.current
                val viewModel = remember { MatchingViewModel() } // or use hiltViewModel() if using Hilt
                val matches = viewModel.matches

                // Trigger fetch when the screen is first composed
                LaunchedEffect(Unit) {
                    viewModel.fetchUserMatches(context)
                }

                MatchingScreen(matches = matches)
            }


            composable(BottomNavItem.Workout.route) { WorkoutScreen(navController) }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }

            composable("settings") {
                SettingsMainScreen(navController)
            }
            composable("exercises") { ExercisesScreen(navController) }


            composable("create_workout") {
                CreateWorkoutScreen(
                    navController = navController,
                    workoutViewModel = workoutViewModel,
                    onAddExerciseClicked = { navController.navigate("select_exercise") },
                    onCreateWorkout = { name ->
                        // TODO: handle workout creation
                    }
                )
            }

            composable("select_exercise") {
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    workoutViewModel.fetchAllExerciseTemplates(context)
                }

                SelectExerciseScreen(
                    navController = navController,
                    initiallySelected = workoutViewModel.selectedExercises,
                    onExerciseSelected = { workoutViewModel.addExercise(it) },
                    exercises = workoutViewModel.allExercises
                )
            }
        }
    }
}

fun getMatchesForPreview(): List<UserMatch> = listOf(
    UserMatch("Aryaman", 22, "Toronto", TimePreference.EVENING, ExperienceLevel.INTERMEDIATE, GymFrequency.REGULARLY, 95.5),
    UserMatch("Priya", 21, "Delhi", TimePreference.MORNING, ExperienceLevel.BEGINNER, GymFrequency.OCCASIONALLY, 73.0),
    UserMatch("Alex", 24, "Vancouver", TimePreference.NIGHT, ExperienceLevel.ADVANCED, GymFrequency.DAILY, 58.0)
)