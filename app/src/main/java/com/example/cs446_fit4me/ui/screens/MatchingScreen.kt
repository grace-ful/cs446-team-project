package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.model.UserMatch
import com.example.cs446_fit4me.ui.components.MatchDetailSheet
import com.example.cs446_fit4me.ui.components.UserCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingScreen(
    matches: List<UserMatch>
) {
    var selectedMatch by remember { mutableStateOf<UserMatch?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Only show the bottom sheet if a match is selected
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

        // Safely show the sheet only when a match is selected
        LaunchedEffect(selectedMatch) {
            if (!sheetState.isVisible) {
                sheetState.show()
            }
        }
    }

    // List of user cards
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(matches) { match ->
            UserCard(match = match) {
                selectedMatch = match
                // sheetState.show() is managed by LaunchedEffect above
            }
        }
    }
}
