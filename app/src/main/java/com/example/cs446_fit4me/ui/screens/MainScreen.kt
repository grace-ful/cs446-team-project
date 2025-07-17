package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cs446_fit4me.LoginScreen
import com.example.cs446_fit4me.navigation.BottomNavItem
import com.example.cs446_fit4me.navigation.getTitleByRoute
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.ui.components.BottomNavigationBar
import com.example.cs446_fit4me.ui.components.TopBar
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.ui.viewmodel.MatchingViewModel
import com.example.cs446_fit4me.ui.viewmodel.WorkoutSessionViewModel
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel
import com.example.cs446_fit4me.ui.workout.CreateWorkoutScreen
import com.example.cs446_fit4me.ui.workout.SelectExerciseScreen
import com.example.cs446_fit4me.ui.workout.WorkoutSessionScreen
import com.example.cs446_fit4me.navigation.AppRoutes


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

            composable(AppRoutes.SETTINGS) {
                SettingsMainScreen(navController)
            }
            composable(AppRoutes.EXERCISES) { ExercisesScreen(navController) }


            composable(AppRoutes.CREATE_WORKOUT) {
                CreateWorkoutScreen(
                    navController = navController,
                    workoutViewModel = workoutViewModel,
                    onAddExerciseClicked = { navController.navigate("select_exercise") },
                    onCreateWorkout = { name ->
                        // TODO: handle workout creation
                    }
                )
            }

            composable(AppRoutes.SELECT_EXERCISE) {
                val context = LocalContext.current
                val isLoading by workoutViewModel.isLoadingExercises

                LaunchedEffect(Unit) {
                    workoutViewModel.fetchAllExerciseTemplates(context)
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    SelectExerciseScreen(
                        navController = navController,
                        initiallySelected = workoutViewModel.selectedExercises,
                        onExerciseSelected = { workoutViewModel.addExercise(it) },
                        exercises = workoutViewModel.allExercises
                    )
                }
            }

            composable("workout_session/{sessionId}") { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
                val viewModel = remember { WorkoutSessionViewModel() }

                WorkoutSessionScreen(
                    sessionId = sessionId,
                    navController = navController,
                    viewModel = viewModel
                )
            }
            composable("login") {
                var showMain by remember { mutableStateOf(false) }
                var currentScreen by remember { mutableStateOf("mainscreen") }
                LoginScreen(
                    onLoginSuccess = { showMain = true },
                    onNavigateToSignUp = { currentScreen = "signup" }
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