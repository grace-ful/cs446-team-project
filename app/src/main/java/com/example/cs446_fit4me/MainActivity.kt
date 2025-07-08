package com.example.cs446_fit4me

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme
import com.example.cs446_fit4me.LoginScreen
import com.example.cs446_fit4me.SignUpScreen
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme
import com.example.cs446_fit4me.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            CS446fit4meTheme {
                var showMain by remember { mutableStateOf(false) }
                var currentScreen by remember { mutableStateOf("login") }

                if (showMain) {
                    MainScreen()
                } else {
                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onLoginSuccess = { showMain = true },
                            onNavigateToSignUp = { currentScreen = "signup" }
                        )
                        "signup" -> SignUpScreen(
                            onSignUpSuccess = { showMain = true },
                            onNavigateToLogin = { currentScreen = "login" }
                        )
                    }
                }
            }
        }
    }
}


