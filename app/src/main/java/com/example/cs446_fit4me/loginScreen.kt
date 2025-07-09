package com.example.cs446_fit4me

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.cs446_fit4me.model.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.cs446_fit4me.network.ApiClient
import com.google.gson.Gson
import java.io.IOException


@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}, onNavigateToSignUp: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun login() {
        isLoading = true
        error = null

        scope.launch {
            try {
                val request = LoginRequest(
                    email = email.trim(),
                    password = password
                )

                val response = ApiClient.userApiService.login(request)
                println("Login Success: $response")

                isLoading = false
                onLoginSuccess()
            } catch (e: Exception) {
                isLoading = false

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

                println("Login Error: $error")
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
