package com.example.cs446_fit4me

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.datastore.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepMeLoggedIn by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // **Reset fields when screen is recomposed (after logout)**
    LaunchedEffect(Unit) {
        email = ""
        password = ""
        error = null
        isLoading = false
        keepMeLoggedIn = false
        Log.d(TAG, "Login screen reset after logout")
    }

    fun login() {
        Log.d(TAG, "Login button clicked with email=$email, keepMeLoggedIn=$keepMeLoggedIn")
        isLoading = true
        error = null

        scope.launch {
            try {
                val request = LoginRequest(
                    email = email.trim(),
                    password = password
                )
                Log.d(TAG, "Sending login request: $request")

                val response = ApiClient.getUserApi(context).login(request)
                Log.d(TAG, "Login Success! Response: $response")

                sessionManager.saveSession(
                    userId = response.id,
                    token = response.token,
                    keepLoggedIn = keepMeLoggedIn
                )
                Log.d(TAG, "Session saved: userId=${response.id}, token=${response.token.take(10)}..., keep=$keepMeLoggedIn")

                try {
                    ApiClient.getMatchingApi(context).updateMatches()
                    Log.d(TAG, "updateMatches() called successfully")
                } catch (e: Exception) {
                    Log.w(TAG, "updateMatches() failed: ${e.message}")
                }

                isLoading = false
                Log.d(TAG, "Calling onLoginSuccess() now...")
                onLoginSuccess()

            } catch (e: Exception) {
                isLoading = false
                Log.e(TAG, "Login failed with exception: ${e.message}", e)

                error = when (e) {
                    is retrofit2.HttpException -> {
                        val errorJson = e.response()?.errorBody()?.string()
                        val parsed = try {
                            Gson().fromJson(errorJson, ErrorResponse::class.java)
                        } catch (_: Exception) {
                            null
                        }
                        parsed?.error ?: "Login failed. Please try again."
                    }
                    is IOException -> "Network error. Please check your connection."
                    else -> e.localizedMessage ?: "Login failed."
                }

                Log.e(TAG, "Login Error message displayed: $error")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Welcome Back!", style = MaterialTheme.typography.headlineLarge)
            Text("Log in to continue", style = MaterialTheme.typography.bodyMedium)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = keepMeLoggedIn,
                    onCheckedChange = { keepMeLoggedIn = it }
                )
                Text("Keep me logged in")
            }

            Button(
                onClick = { login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Logging in..." else "Login")
            }

            TextButton(onClick = onNavigateToSignUp) {
                Text("Don't have an account? Sign up")
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
