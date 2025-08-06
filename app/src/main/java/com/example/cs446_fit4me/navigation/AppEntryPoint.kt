package com.example.cs446_fit4me.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.cs446_fit4me.LoginScreen
import com.example.cs446_fit4me.SignUpScreen
import com.example.cs446_fit4me.datastore.SessionManager
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.ui.screens.MainScreen
import kotlinx.coroutines.launch
import com.example.cs446_fit4me.chat.GlobalChatSocketManager


@Composable
fun AppEntryPoint(
    showMain: Boolean,
    currentScreen: String,
    onShowMainChanged: (Boolean) -> Unit,
    onCurrentScreenChanged: (String) -> Unit,
    onResetApp: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    val sessionState by sessionManager.sessionFlow.collectAsState(initial = null)
    val currentSession = sessionState

    var initialized by remember { mutableStateOf(false) }

    // Initial splash until we determine route
    if (!initialized && currentSession == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Only run once on first composition to decide the initial route
    LaunchedEffect(currentSession) {
        if (!initialized && currentSession != null) {
            ApiClient.setToken(currentSession.token)
            val isPersistentlyLoggedIn =
                currentSession.keepLoggedIn && !currentSession.token.isNullOrBlank()

            Log.d("AppEntryPoint", "First session=$currentSession, loggedIn=$isPersistentlyLoggedIn")

            // Set initial route only
            if (isPersistentlyLoggedIn) {
                onShowMainChanged(true)
            } else if (!showMain) {
                // Only force login if we are still on initial boot
                onShowMainChanged(false)
                onCurrentScreenChanged("login")
            }
            initialized = true
        }
    }

    // After initialized, trust showMain (LoginScreen toggles this correctly)
    if (!initialized) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (showMain) {
        MainScreen(
            onLogout = {
                scope.launch {
                    GlobalChatSocketManager.disconnect()
                    sessionManager.clearSession()
                    onShowMainChanged(false)
                    onCurrentScreenChanged("login")
                    onResetApp()
                }
            }
        )
    } else {
        when (currentScreen) {
            "login" -> LoginScreen(
                onLoginSuccess = {
                    Log.d("AppEntryPoint", "Login success triggered - showMain set to true")
                    onShowMainChanged(true)
                    onResetApp()
                },
                onNavigateToSignUp = { onCurrentScreenChanged("signup") }
            )
            "signup" -> SignUpScreen(
                onSignUpSuccess = {
                    onShowMainChanged(true)
                    onResetApp()
                },
                onNavigateToLogin = { onCurrentScreenChanged("login") }
            )
            else -> {
                LoginScreen(
                    onLoginSuccess = {
                        onShowMainChanged(true)
                        onResetApp()
                    },
                    onNavigateToSignUp = { onCurrentScreenChanged("signup") }
                )
            }
        }
    }
}



