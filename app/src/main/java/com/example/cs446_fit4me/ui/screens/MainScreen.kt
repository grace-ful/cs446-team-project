package com.example.cs446_fit4me.ui.screens

import MessagesViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.cs446_fit4me.ui.chat.ChatScreen
import com.example.cs446_fit4me.ui.chat.ChatViewModel
import com.example.cs446_fit4me.ui.components.BottomNavigationBar
import com.example.cs446_fit4me.ui.components.TopBar
import com.example.cs446_fit4me.ui.viewmodel.MatchingViewModel
import com.example.cs446_fit4me.ui.viewmodel.WorkoutSessionViewModel
import com.example.cs446_fit4me.ui.viewmodel.WorkoutViewModel
import com.example.cs446_fit4me.ui.workout.CreateWorkoutScreen
import com.example.cs446_fit4me.ui.workout.SelectExerciseScreen
import com.example.cs446_fit4me.ui.workout.WorkoutSessionScreen
import com.example.cs446_fit4me.ui.screens.settings_subscreens.*


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(onLogout: () -> Unit) {
    var userName by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Tab state for bottom nav (KEY)
    var selectedTab by remember { mutableStateOf(BottomNavItem.Home.route) }

    // Fetch user profile on launch
    LaunchedEffect(Unit) {
        try {
            val user = ApiClient.getUserApi(context).getUserById()
            userName = user.name
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Failed to load user info"
        }
    }

    if (userName == null) {
        // Loading or error indicator
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (error != null) {
                Text(text = error ?: "Error loading user.")
            } else {
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
        BottomNavItem.Workout,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Home.route
    val currentScreenTitle = getTitleByRoute(currentRoute, bottomNavItems)
    val canNavigateBack = navController.previousBackStackEntry != null

    // Always update the selectedTab when navigation changes via gestures or navController
    LaunchedEffect(currentRoute) {
        if (bottomNavItems.any { it.route == currentRoute }) {
            selectedTab = currentRoute
        }
    }

    Scaffold(
        topBar = {
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
            BottomNavigationBar(
                navController = navController,
                items = bottomNavItems,
                selectedRoute = selectedTab,
                onTabSelected = { route ->
                    selectedTab = route
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController, username = userName!!)
            }

            // MESSAGES TAB: uses MessagesViewModel with SimpleUser
            composable(BottomNavItem.Messages.route) {
                val context = LocalContext.current
                // Pass ApiClient dependency if required by your MessagesViewModel
                val api = remember { ApiClient.getChatApi(context) }
                val viewModel = remember { MessagesViewModel(api) }
                MessagesScreen(navController = navController, viewModel = viewModel)
            }

            composable(BottomNavItem.FindMatch.route) {
                val context = LocalContext.current
                val viewModel = remember { MatchingViewModel() }
                val matches = viewModel.matches
                LaunchedEffect(Unit) { viewModel.fetchUserMatches(context) }
                MatchingScreen(matches = matches, navController = navController)
            }

            composable(BottomNavItem.Profile.route) { ProfileScreen() }

            composable(AppRoutes.SETTINGS) {
                SettingsMainScreen(navController, onLogout)
            }
            composable(AppRoutes.EXERCISES) { ExercisesScreen(navController) }

            composable(AppRoutes.CREATE_WORKOUT) {
                CreateWorkoutScreen(
                    navController = navController,
                    workoutViewModel = workoutViewModel,
                    onAddExerciseClicked = { navController.navigate("select_exercise") },
                    onCreateWorkout = { name -> /* TODO */ }
                )
            }
            composable(BottomNavItem.Workout.route) {
                WorkoutScreen(
                    navController = navController,
                    workoutViewModel = workoutViewModel,
                    onEditWorkout = { workout ->
                        navController.navigate("edit_workout/${workout.id}")
                    }
                )
            }

            composable(AppRoutes.SELECT_EXERCISE) {
                val context = LocalContext.current
                val isLoading by workoutViewModel.isLoadingExercises
                LaunchedEffect(Unit) { workoutViewModel.fetchAllExerciseTemplates(context) }
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
                        exercises = workoutViewModel.allExercises
                    )
                }
            }
            composable(
                "edit_workout/{workoutId}",
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId")
                val workoutToEdit = workoutViewModel.myWorkouts.find { it.id == workoutId }
                    ?: workoutViewModel.standardWorkouts.find { it.id == workoutId }
                if (workoutToEdit != null) {
                    EditWorkoutScreen(
                        navController = navController,
                        workoutToEdit = workoutToEdit,
                        onSave = { updatedWorkout ->
                            workoutViewModel.updateWorkout(updatedWorkout)
                            // KEY: update tab and navigate
                            selectedTab = BottomNavItem.Workout.route
                            navController.navigate(BottomNavItem.Workout.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                } else {
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

                // Provide ApiService and SocketManager for ViewModel
                val api = remember { ApiClient.getChatApi(context) }
                val socketManager = remember {
                    com.example.cs446_fit4me.chat.ChatSocketManager(
                        "http://10.0.2.2:3000",
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
                    onSend = { viewModel.sendMessage(it) },
                    currentUserId = currentUserId
                )
            }
            composable(AppRoutes.EDIT_ACCOUNT_INFO) { EditAccountInfoScreen(navController) }
            composable(AppRoutes.CHANGE_PASSWORD) { ChangePasswordScreen(navController) }
            composable(AppRoutes.NOTIFICATION_SETTINGS) { NotificationSettingsScreen(navController) }
            composable(AppRoutes.REMIND_ME) { RemindMeScreen(navController) }
            composable(AppRoutes.UNITS) { UnitsScreen(navController) }
            composable(AppRoutes.ACCESSIBILITY) { AccessibilityScreen(navController) }
            composable(AppRoutes.PROFILE_VISIBILITY) { ProfileVisibilityScreen(navController) }
            composable(AppRoutes.MATCHING_PREFERENCES) { MatchingPreferencesScreen(navController) }
            composable(AppRoutes.WORKOUT_HISTORY) { WorkoutHistoryScreen(navController) }
            composable(AppRoutes.RATE) { RateScreen(navController) }
            composable(AppRoutes.HELP_SUPPORT) { HelpSupportScreen(navController) }

        }
    }
}
