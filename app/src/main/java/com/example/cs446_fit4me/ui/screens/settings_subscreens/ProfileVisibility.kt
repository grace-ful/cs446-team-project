package com.example.cs446_fit4me.ui.screens.settings_subscreens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cs446_fit4me.model.UpdateGenderMatchingPreferenceRequest
import com.example.cs446_fit4me.model.UpdatePrivacyRequest
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp

@Composable
fun ProfileVisibilityScreen(navController: NavController) {
    val tag = "ProfileVisibilityScreen"
    val context = LocalContext.current
    val api = remember { ApiClient.getUserApi(context) }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // UI state
    var consentToMatching by remember { mutableStateOf(true) }    // true == consent
    var genderPreference by remember { mutableStateOf("Same Gender") }

    val scope = rememberCoroutineScope()

    // Load current values
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val user = api.getUserById()
            // profilePrivacy=true => NOT consenting to matching
            consentToMatching = !user.profilePrivacy
            genderPreference = if (user.matchWithSameGender) "Same Gender" else "Open to All"
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Failed to load profile visibility settings"
            Log.e(tag, "Load error", e)
        } finally {
            isLoading = false
        }
    }

    fun updatePrivacy(consent: Boolean) {
        scope.launch {
            try {
                val newPrivacy = !consent // consentToMatching=true => profilePrivacy=false
                Log.d(tag, "Updating profilePrivacy to: $newPrivacy")
                val updated = api.updatePrivacy(UpdatePrivacyRequest(profilePrivacy = newPrivacy))
                consentToMatching = !updated.profilePrivacy
            } catch (e: Exception) {
                error = e.localizedMessage ?: "Failed to update privacy"
                Log.e(tag, "Privacy update error", e)
            }
        }
    }

    fun updateGenderPreference(sameGender: Boolean) {
        scope.launch {
            try {
                Log.d(tag, "Updating matchWithSameGender to: $sameGender")
                val updated = api.updateGenderMatchingPreference(
                    UpdateGenderMatchingPreferenceRequest(matchWithSameGender = sameGender)
                )
                genderPreference = if (updated.matchWithSameGender) "Same Gender" else "Open to All"
            } catch (e: Exception) {
                error = e.localizedMessage ?: "Failed to update gender match preference"
                Log.e(tag, "Gender pref update error", e)
            }
        }
    }

    SettingsSubScreenTemplate("Profile Visibility", navController) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Consent to Matching Section
                Text(
                    "Consent to being matched with other Gym Buddies",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConsentToggleButton(
                        label = "Yes, I Consent.",
                        isSelected = consentToMatching,
                        onClick = {
                            if (!consentToMatching) {
                                consentToMatching = true
                                updatePrivacy(consent = true)
                            }
                        }
                    )
                    ConsentToggleButton(
                        label = "No, I am good.",
                        isSelected = !consentToMatching,
                        onClick = {
                            if (consentToMatching) {
                                consentToMatching = false
                                updatePrivacy(consent = false)
                            }
                        }
                    )
                }

                // Gender Preference Section (only if consent is Yes)
                if (consentToMatching) {
                    Text(
                        "Gender preference when matched",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ConsentToggleButton(
                            label = "Same Gender",
                            isSelected = genderPreference == "Same Gender",
                            onClick = {
                                if (genderPreference != "Same Gender") {
                                    genderPreference = "Same Gender"
                                    updateGenderPreference(true)
                                }
                            }
                        )
                        ConsentToggleButton(
                            label = "Open to All",
                            isSelected = genderPreference == "Open to All",
                            onClick = {
                                if (genderPreference != "Open to All") {
                                    genderPreference = "Open to All"
                                    updateGenderPreference(false)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConsentToggleButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    if (isSelected) {
        Button(onClick = onClick) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileVisibilityScreenPreview() {
    CS446fit4meTheme {
        val navController = rememberNavController()
        ProfileVisibilityScreen(navController = navController)
    }
}
