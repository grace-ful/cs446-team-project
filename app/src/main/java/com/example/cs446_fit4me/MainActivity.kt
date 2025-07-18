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
import android.util.Log
import com.example.cs446_fit4me.navigation.AppEntryPoint
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.network.TestResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.libraries.places.api.Places


class MainActivity : ComponentActivity() {
    private var showMain by mutableStateOf(false)
    private var currentScreen by mutableStateOf("login")
    private var resetKey by mutableStateOf(0) // ← NEW

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyDp7yaybG_NXQ0nPFixhdGe0SMFnd7iP5M")
        }

        // TEMP TEST: Call /api/test endpoint
        ApiClient.getTestApi(this).checkStatus().enqueue(object : Callback<TestResponse> {
            override fun onResponse(call: Call<TestResponse>, response: Response<TestResponse>) {
                if (response.isSuccessful) {
                    Log.i("TestAPI", "✅ Success: ${response.body()?.status}")
                } else {
                    Log.e("TestAPI", "❌ Failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TestResponse>, t: Throwable) {
                Log.e("TestAPI", "❌ Error: ${t.message}")
            }
        })


        setContent {
            CS446fit4meTheme {
                key(resetKey) {
                    AppEntryPoint(
                        showMain = showMain,
                        currentScreen = currentScreen,
                        onShowMainChanged = { showMain = it },
                        onCurrentScreenChanged = { currentScreen = it },
                        onResetApp = { resetKey++ }
                    )
                }
            }
        }
    }
}


