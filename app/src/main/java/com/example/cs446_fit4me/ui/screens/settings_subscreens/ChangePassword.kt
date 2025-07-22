package com.example.cs446_fit4me.ui.screens.settings_subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.datastore.UserManager
import com.example.cs446_fit4me.model.UpdateUserRequest
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(navController: NavController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val isPasswordMatch = newPassword == confirmPassword

    SettingsSubScreenTemplate("Change Password", navController) {

        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = screenHeight * 0.15f, start = 16.dp, end = 16.dp)
        ) {
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = submitted && newPassword.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            if (submitted && newPassword.isBlank()) {
                Text("New password is required", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = submitted && (!isPasswordMatch || confirmPassword.isBlank()),
                modifier = Modifier.fillMaxWidth()
            )
            if (submitted && confirmPassword.isBlank()) {
                Text("Please confirm new password", color = MaterialTheme.colorScheme.error)
            } else if (submitted && !isPasswordMatch) {
                Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    submitted = true
                    if (newPassword.isNotBlank() && isPasswordMatch) {
                        val userId = UserManager.getUserId(context)
                        if (userId != null) {
                            val updateRequest = UpdateUserRequest(password = newPassword)
                            println("👉 Preparing to send update request for userId: $userId")

                            coroutineScope.launch {
                                try {
                                    val response = ApiClient.getUserApi(context).updateUser(userId, updateRequest)
                                    println("✅ Password updated response! Response: $response")
                                } catch (e: Exception) {
                                    println("❌ Exception during password update: ${e.message}")
                                }
                            }
                        } else {
                            println("❌ Cannot update password: userId is null")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Update Password")
            }
        }
    }
}
