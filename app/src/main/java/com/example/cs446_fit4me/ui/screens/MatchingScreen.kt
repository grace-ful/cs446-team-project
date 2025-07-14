package com.example.cs446_fit4me.ui.screens

import MatchEntry
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.ui.components.MatchDetailSheet
import com.example.cs446_fit4me.ui.components.UserCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingScreen(
    matches: List<MatchEntry>
) {
    var selectedMatch: MatchEntry? by remember { mutableStateOf(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(matches) { match ->
            UserCard(match = match) {
                selectedMatch = match
            }
        }
    }
}
