package com.example.cs446_fit4me.ui.screens

import MessagesViewModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.cs446_fit4me.LoginScreen
import com.example.cs446_fit4me.datastore.UserManager
import com.example.cs446_fit4me.navigation.AppRoutes
import com.example.cs446_fit4me.navigation.BottomNavItem
import com.example.cs446_fit4me.navigation.getTitleByRoute
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.ui.chat.ChatViewModel
import com.example.cs446_fit4me.ui.components.BottomNavigationBar
import com.example.cs446_fit4me.ui.components.TopBar
import com.example.cs446_fit4me.ui.viewmodel.MatchingViewModel
import com.example.cs446_fit4me.ui.viewmodel.WorkoutSessionViewModel
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel
import com.example.cs446_fit4me.ui.workout.WorkoutSessionScreen
import com.example.cs446_fit4me.ui.screens.settings_subscreens.*
import retrofit2.HttpException

private const val TAG = "MainScreen"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(onLogout: () -> Unit) {
    Log.d(TAG, ">>> Composing MainScreen")

    var userName by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val userPrefs = remember { com.example.cs446_fit4me.datastore.UserPreferencesManager(context) }
    val userId by userPrefs.userIdFlow.collectAsState(initial = null)

    // Tab state for bottom nav (KEY)
    var selectedTab by remember { mutableStateOf(BottomNavItem.Home.route) }

    // Fetch user profile on launch
    LaunchedEffect(Unit) {
        Log.d(TAG, "LaunchedEffect(Unit): fetching user profile (getUserById)")
        try {
            val user = ApiClient.getUserApi(context).getUserById()
            Log.d(TAG, "getUserById success: id=${user.id}, name=${user.name}")
            userName = user.name
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    Log.e(TAG, "getUserById HTTP error: code=${e.code()}, msg=${e.message()}", e)
                    if (e.code() == 401) {
                        Log.w(TAG, "HTTP 401 detected in MainScreen -> forcing logout()")
                        onLogout()   // <-- comment this out if you only want logs
                        return@LaunchedEffect
                    }
                }
                else -> Log.e(TAG, "getUserById failed: ${e.message}", e)
            }
            error = e.localizedMessage ?: "Failed to load user info"
        }
    }

    Log.d(TAG, "userName=$userName, error=$error, userIdFromPrefs=$userId")

    if (userName == null) {
        // Loading or error indicator
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (error != null) {
                Log.d(TAG, "Showing error UI: $error")
                Text(text = error ?: "Error loading user.")
            } else {
                Log.d(TAG, "Showing loading spinner while fetching profile...")
                CircularProgressIndicator()
            }
        }
        return
    }

    val navController = rememberNavController()
    val workoutViewModel: WorkoutViewModel = viewModel()
    val bottomNavItems = listOf(
        BottomNavItem.Messages,
        BottomNavItem.FindMatch,
        BottomNavItem.Home,
        BottomNavItem.Workout
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Home.route
    val currentScreenTitle = getTitleByRoute(currentRoute, bottomNavItems)
    val canNavigateBack = navController.previousBackStackEntry != null

    // Always update the selectedTab when navigation changes via gestures or navController
    LaunchedEffect(currentRoute) {
        Log.d(TAG, "Route changed to: $currentRoute")
        if (bottomNavItems.any { it.route == currentRoute }) {
            selectedTab = currentRoute
            Log.d(TAG, "selectedTab updated to $selectedTab")
        }
    }

    Scaffold(
        topBar = {
            if (bottomNavItems.any { it.route == currentRoute } || currentRoute.startsWith("chat/")) {
                TopBar(
                    title = if (currentRoute.startsWith("chat/")) "Chat" else currentScreenTitle,
                    canNavigateBack = canNavigateBack,
                    onNavigateUp = {
                        Log.d(TAG, "TopBar onNavigateUp()")
                        navController.navigateUp()
                    },
                    onSettingsClick = if (currentRoute == BottomNavItem.Home.route) {
                        {
                            Log.d(TAG, "Navigating to settings")
                            navController.navigate("settings")
                        }
                    } else {
                        null
                    }
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                items = bottomNavItems,
                selectedRoute = selectedTab,
                onTabSelected = { route ->
                    Log.d(TAG, "BottomNav tab selected: $route")
                    selectedTab = route
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->
        Log.d(TAG, "Rendering NavHost, startDestination=${BottomNavItem.Home.route}")
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                Log.d(TAG, "Composing HomeScreen")
                HomeScreen(navController, username = userName!!)
            }

            composable(BottomNavItem.Messages.route) {
                Log.d(TAG, "Composing MessagesScreen")
                val context = LocalContext.current
                val api = remember { ApiClient.getChatApi(context) }
                val viewModel = remember { MessagesViewModel(api) }
                MessagesScreen(navController = navController, viewModel = viewModel)
            }

            composable(BottomNavItem.FindMatch.route) {
                Log.d(TAG, "Composing MatchingScreen")
                val context = LocalContext.current
                val viewModel: MatchingViewModel = viewModel()
                LaunchedEffect(Unit) {
                    Log.d(TAG, "MatchingScreen -> fetchUserMatches()")
                    viewModel.fetchUserMatches(context)
                }

                MatchingScreen(
                    viewModel = viewModel,
                    navController = navController,
                    context = context
                )
            }

            composable(AppRoutes.PROFILE) {
                Log.d(TAG, "Composing ProfileScreen")
                ProfileScreen()
            }

            composable(AppRoutes.SETTINGS) {
                Log.d(TAG, "Composing SettingsMainScreen")
                SettingsMainScreen(navController, onLogout)
            }

            composable(AppRoutes.EXERCISES) {
                Log.d(TAG, "Composing ExercisesScreen")
                ExercisesScreen(navController)
            }

            composable(AppRoutes.CREATE_WORKOUT) {
                Log.d(TAG, "Composing CreateWorkoutScreen")
                CreateWorkoutScreen(
                    navController = navController,
                    workoutViewModel = workoutViewModel,
                    onAddExerciseClicked = {
                        Log.d(TAG, "CreateWorkoutScreen -> navigate to select_exercise")
                        navController.navigate("select_exercise")
                    },
                    userId = userId,
                    onCreateWorkout = { name -> Log.d(TAG, "onCreateWorkout called with name=$name") }
                )
            }

            composable(BottomNavItem.Workout.route) {
                Log.d(TAG, "Composing WorkoutScreen")
                WorkoutScreen(
                    navController = navController,
                    workoutViewModel = workoutViewModel,
                    onEditWorkout = { workout ->
                        Log.d(TAG, "WorkoutScreen -> Edit workout: ${workout.id}")
                        navController.navigate("edit_workout/${workout.id}")
                    }
                )
            }

            composable(AppRoutes.SELECT_EXERCISE) {
                Log.d(TAG, "Composing SelectExerciseScreen")
                val context = LocalContext.current
                val isLoading by workoutViewModel.isLoadingExercises
                LaunchedEffect(Unit) {
                    Log.d(TAG, "SelectExerciseScreen -> fetchAllExerciseTemplates()")
                    workoutViewModel.fetchAllExerciseTemplates(context)
                }
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Log.d(TAG, "SelectExerciseScreen loading spinner")
                        CircularProgressIndicator()
                    }
                } else {
                    SelectExerciseScreen(
                        navController = navController,
                        exercises = workoutViewModel.allExercises,
                        workoutViewModel = workoutViewModel
                    )
                }
            }

            composable(
                "edit_workout/{workoutId}",
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId")
                Log.d(TAG, "Composing EditWorkoutScreen for id=$workoutId")

                val workoutToEdit = workoutViewModel.myWorkouts.find { it.id == workoutId }
                    ?: workoutViewModel.standardWorkouts.find { it.id == workoutId }

                if (workoutToEdit != null) {
                    EditWorkoutScreen(
                        navController = navController,
                        workoutToEdit = workoutToEdit,
                        onSave = { updatedWorkout ->
                            Log.d(TAG, "EditWorkoutScreen -> onSave for id=${updatedWorkout.id}")
                            workoutViewModel.updateWorkout(updatedWorkout)
                            selectedTab = BottomNavItem.Workout.route
                            navController.navigate(BottomNavItem.Workout.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                } else {
                    Log.w(TAG, "Workout not found for id=$workoutId")
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Workout not found!")
                    }
                }
            }

            composable("workout_session/{sessionId}") { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
                Log.d(TAG, "Composing WorkoutSessionScreen for sessionId=$sessionId")
                WorkoutSessionScreen(
                    sessionId = sessionId,
                    navController = navController,
                    viewModel = remember { WorkoutSessionViewModel() }
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

            composable(
                "${AppRoutes.EXERCISE_DETAIL}/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                ExerciseDetailScreen(exerciseId = exerciseId, navController = navController)
            }


            // CHAT SCREEN - single peer chat
            composable(
                "chat/{peerUserId}",
                arguments = listOf(navArgument("peerUserId") { type = NavType.StringType })
            ) { backStackEntry ->
                val peerUserId = backStackEntry.arguments?.getString("peerUserId") ?: return@composable
                val context = LocalContext.current
                val currentUserId = UserManager.getUserId(context)!!
                Log.d(TAG, "Composing ChatScreen with peerUserId=$peerUserId, currentUserId=$currentUserId")

                val api = remember { ApiClient.getChatApi(context) }
                val socketManager = remember {
                    com.example.cs446_fit4me.chat.ChatSocketManager(
                        ApiClient.SOCKET_URL,
                        currentUserId
                    )
                }
                val viewModel = remember {
                    ChatViewModel(
                        api = api,
                        socketManager = socketManager,
                        currentUserId = currentUserId,
                        peerUserId = peerUserId
                    )
                }
                val messages by viewModel.messages.collectAsState()

                ChatScreen(
                    messages = messages,
                    onSend = {
                        Log.d(TAG, "ChatScreen -> sendMessage: $it")
                        viewModel.sendMessage(it)
                    },
                    currentUserId = currentUserId,
                    onBack = {
                        Log.d(TAG, "ChatScreen -> navigateUp()")
                        navController.navigateUp()
                    }
                )
            }

            composable(AppRoutes.CHANGE_PASSWORD) { ChangePasswordScreen(navController) }
            composable(AppRoutes.NOTIFICATION_SETTINGS) { NotificationSettingsScreen(navController) }
            composable(AppRoutes.UNITS) { UnitsScreen(navController) }
            composable(AppRoutes.PROFILE_VISIBILITY) { ProfileVisibilityScreen(navController) }
            composable(AppRoutes.MATCHING_PREFERENCES) { MatchingPreferencesScreen(navController) }

        }
    }
}
