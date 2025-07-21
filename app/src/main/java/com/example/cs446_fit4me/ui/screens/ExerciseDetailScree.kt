package com.example.cs446_fit4me.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.cs446_fit4me.model.Exercise
import com.example.cs446_fit4me.model.toExercise
import com.example.cs446_fit4me.network.ApiClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    navController: NavController
) {
    val context = LocalContext.current

    var exercise by remember { mutableStateOf<Exercise?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(exerciseId) {
        try {
            val exerciseTemplate = ApiClient.getExerciseApi(context).getExerciseById(exerciseId)
            exercise = exerciseTemplate.toExercise()
            Log.d("ExerciseDetailScreen", "Fetched exercise: $exercise")
        } catch (e: Exception) {
            errorMessage = "Failed to load exercise: ${e.localizedMessage}"
            Log.e("ExerciseDetailScreen", "Error: ${e.localizedMessage}", e)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage != null -> {
                    Text(errorMessage ?: "Unknown error")
                }
                exercise != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Image
                        if (exercise!!.imageUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(exercise!!.imageUrl),
                                contentDescription = exercise!!.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Inside
                            )

                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = exercise!!.name.firstOrNull()?.uppercase() ?: "?",
                                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Name
                        Text(
                            text = exercise!!.name,
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Description
                        val plainDescription = android.text.Html.fromHtml(
                            exercise!!.description,
                            android.text.Html.FROM_HTML_MODE_LEGACY
                        ).toString()

                        Text(
                            text = plainDescription,
                            style = MaterialTheme.typography.bodyLarge
                        )

                    }
                }
            }
        }
    }
}
