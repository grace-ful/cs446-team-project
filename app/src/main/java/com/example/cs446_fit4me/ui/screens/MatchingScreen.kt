package com.example.cs446_fit4me.ui.screens

import MatchEntry
import MatcheeUser
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs446_fit4me.model.GymFrequency
import com.example.cs446_fit4me.ui.components.MatchDetailSheet
import com.example.cs446_fit4me.ui.components.UserCard
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme
import com.example.cs446_fit4me.ui.viewmodel.MatchingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingScreen(
    viewModel: MatchingViewModel,
    navController: NavController,
    context: Context
) {
    var selectedMatch by remember { mutableStateOf<MatchEntry?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    // Bottom sheet for match details
    if (selectedMatch != null) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }
                selectedMatch = null
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            MatchDetailSheet(
                match = selectedMatch!!,
                onClose = {
                    coroutineScope.launch { sheetState.hide() }
                    selectedMatch = null
                }
            )
        }

        LaunchedEffect(selectedMatch) {
            if (!sheetState.isVisible) {
                sheetState.show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when {
                isRefreshing -> {
                    // Loader while refreshing
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Refreshing matches...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                viewModel.matches.isEmpty() -> {
                    // Empty state (e.g., no matches found)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No matches found.")
                    }
                }

                else -> {
                    // Match list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.matches) { match ->
                            UserCard(
                                match = match,
                                onClick = { selectedMatch = match },
                                onChatClick = {
                                    navController.navigate("chat/${match.matchee!!.id}")
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Refresh Button
        Button(
            onClick = {
                coroutineScope.launch {
                    isRefreshing = true

                    try {
                        val delayJob = launch { delay(1500) }

                        viewModel.clearMatches()
                        viewModel.refreshMatchesManually(context)

                        delayJob.join()
                        Log.d("MatchingScreen", "Match refresh completed")
                    } catch (e: Exception) {
                        Log.e("MatchingScreen", "Error refreshing matches", e)
                    } finally {
                        isRefreshing = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            enabled = !isRefreshing
        ) {
            Text("Refresh Matches")
        }
    }
}
