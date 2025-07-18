package com.example.cs446_fit4me.navigation

import androidx.compose.runtime.Composable
import com.example.cs446_fit4me.LoginScreen
import com.example.cs446_fit4me.SignUpScreen
import com.example.cs446_fit4me.ui.screens.MainScreen

@Composable
fun AppEntryPoint(
    showMain: Boolean,
    currentScreen: String,
    onShowMainChanged: (Boolean) -> Unit,
    onCurrentScreenChanged: (String) -> Unit,
    onResetApp: () -> Unit
) {
    if (showMain) {
        MainScreen(onLogout = {
            onShowMainChanged(false)
            onCurrentScreenChanged("login")
            onResetApp()
        })
    } else {
        when (currentScreen) {
            "login" -> LoginScreen(
                onLoginSuccess = {
                    onShowMainChanged(true)
                    onResetApp() // ← RESET on login to wipe any stale state
                },
                onNavigateToSignUp = { onCurrentScreenChanged("signup") }
            )

            "signup" -> SignUpScreen(
                onSignUpSuccess = {
                    onShowMainChanged(true)
                    onResetApp() // ← RESET on sign up success too
                },
                onNavigateToLogin = { onCurrentScreenChanged("login") }
            )
        }
    }
}
