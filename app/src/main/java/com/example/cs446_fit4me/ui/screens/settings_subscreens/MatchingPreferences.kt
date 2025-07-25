package com.example.cs446_fit4me.ui.screens.settings_subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cs446_fit4me.model.MatchStrategy
import com.example.cs446_fit4me.model.UpdateMatchStrategyRequest
import com.example.cs446_fit4me.network.ApiClient
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme
import kotlinx.coroutines.launch
import android.util.Log


@Composable
fun MatchingPreferencesScreen(navController: NavController) {
    val matchTypes = listOf(
        "Balanced Match",
        "Schedule Match",
        "Experience Match",
        "Local Match",
        "Body Match",
        "Beginner Match"
    )

    val matchDescriptions = mapOf(
        "Balanced Match" to "A balanced strategy that considers factors like your workout time, fitness experience, body stats, and location to match you with the most compatible gym buddies.",
        "Schedule Match" to "Focuses mainly on matching you with others who share similar workout times and weekly workout frequency.",
        "Experience Match" to "Pairs you with people who have similar fitness levels and training experience for smoother compatibility.",
        "Local Match" to "Prioritizes proximity and preferred workout times, matching you with gym buddies near your location.",
        "Body Match" to "Matches based on body metrics like height and weight for better workout pacing and exercise pairing.",
        "Beginner Match" to "Specifically designed for beginners or intermediates, factoring in workout frequency and location."
    )

    val uiToEnum = mapOf(
        "Balanced Match" to MatchStrategy.BALANCED,
        "Schedule Match" to MatchStrategy.SCHEDULE,
        "Experience Match" to MatchStrategy.EXPERIENCE,
        "Local Match" to MatchStrategy.LOCAL,
        "Body Match" to MatchStrategy.BODY,
        "Beginner Match" to MatchStrategy.BEGINNER
    )
    val enumToUi = uiToEnum.entries.associate { (k, v) -> v to k }

    val context = LocalContext.current
    val api = remember { ApiClient.getUserApi(context) }

    var selectedMatchType by remember { mutableStateOf(matchTypes[0]) }
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val tag = "MatchingPreferencesScreen"


    // Fetch the user (like MainScreen)
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val user = api.getUserById()  // No arguments
            selectedMatchType = enumToUi[user.matchStrategy] ?: matchTypes[0]
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Failed to load user"
        } finally {
            isLoading = false
        }
    }

    SettingsSubScreenTemplate("Matching Preferences", navController) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                matchTypes.forEach { type ->
                    MatchTypeButton(
                        text = type,
                        isSelected = selectedMatchType == type,
                        onClick = {
                            if (selectedMatchType == type) return@MatchTypeButton
                            selectedMatchType = type

                            val newStrategy = uiToEnum[type] ?: return@MatchTypeButton
                            scope.launch {
                                try {
                                    isUpdating = true
                                    Log.d(tag, "Updating match strategy to: $newStrategy")
                                    val response = api.updateMatchStrategy(UpdateMatchStrategyRequest(newStrategy))
                                    Log.d(tag, "Update response: $response")
                                } catch (e: Exception) {
                                    Log.e(tag, "Error updating strategy: ${e.localizedMessage}", e)
                                    error = e.localizedMessage ?: "Failed to update"
                                } finally {
                                    isUpdating = false
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = matchDescriptions[selectedMatchType] ?: "",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (isUpdating) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun MatchTypeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun MatchingPreferencesScreenPreview() {
    CS446fit4meTheme {
        val navController = rememberNavController()
        MatchingPreferencesScreen(navController = navController)
    }
}
