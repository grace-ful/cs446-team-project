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
    onCurrentScreenChanged: (String) -> Unit
) {
    if (showMain) {
        MainScreen(onLogout = {
            onShowMainChanged(false)
            onCurrentScreenChanged("login")
        })
    } else {
        when (currentScreen) {
            "login" -> LoginScreen(
                onLoginSuccess = { onShowMainChanged(true) },
                onNavigateToSignUp = { onCurrentScreenChanged("signup") }
            )
            "signup" -> SignUpScreen(
                onSignUpSuccess = { onShowMainChanged(true) },
                onNavigateToLogin = { onCurrentScreenChanged("login") }
            )
        }
    }
}
