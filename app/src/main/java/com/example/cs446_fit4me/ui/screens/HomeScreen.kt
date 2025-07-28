package com.example.cs446_fit4me.ui.screens

import MessagesViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs446_fit4me.ui.theme.CS446fit4meTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cs446_fit4me.model.*
import com.example.cs446_fit4me.navigation.AppRoutes
import com.example.cs446_fit4me.ui.viewmodel.WorkoutSessionHistoryState
import com.example.cs446_fit4me.ui.viewmodel.WorkoutSessionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward

@Composable
fun HomeScreen(navController: NavController? = null, username: String) {
    val quoteOfTheDay = remember {
        val index = LocalDate.now().dayOfYear % Quote.motivationalQuotes.size
        Quote.motivationalQuotes[index]
    }

    val workoutSessionViewModel: WorkoutSessionViewModel = viewModel()
    val context = LocalContext.current
    workoutSessionViewModel.initApi(context)
    workoutSessionViewModel.fetchWorkoutHistory()
    workoutSessionViewModel.fetchExerciseHistory()

    val workoutHistoryState = workoutSessionViewModel.historyState.collectAsState().value
    val workoutDatesMap: Map<LocalDate, List<WorkoutSessionUI>> = when (workoutHistoryState) {
        is WorkoutSessionHistoryState.Success -> {
            workoutHistoryState.sessions.groupBy {
                try {
                    LocalDate.parse(it.workoutDate, DateTimeFormatter.ISO_DATE)
                } catch (e: Exception) {
                    null
                }
            }.filterKeys { it != null }.mapKeys { it.key!! }
        }
        else -> emptyMap()
    }

    val today = LocalDate.now()
    var visibleMonth by remember { mutableStateOf(today.withDayOfMonth(1)) }
    val daysInMonth = visibleMonth.lengthOfMonth()
    val currentMonth = visibleMonth.monthValue
    val currentYear = visibleMonth.year
    val monthYearLabel = visibleMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val firstName = username.split(" ").first()
            Text("Hey $firstName 👋", style = MaterialTheme.typography.headlineMedium)

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { navController?.navigate(AppRoutes.PROFILE) }
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = username.first().uppercaseChar().toString(),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { visibleMonth = visibleMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
            }
            Text(
                text = monthYearLabel,
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { visibleMonth = visibleMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
            }
        }

        // Day of Week Header
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 0..6) {
                val dayName = LocalDate.of(2023, 1, 2 + i) // Monday start
                    .dayOfWeek
                    .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(dayName, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Calendar Grid
        for (week in 0 until daysInMonth step 7) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (dayOffset in 0 until 7) {
                    val day = week + dayOffset + 1
                    if (day <= daysInMonth) {
                        val date = LocalDate.of(currentYear, currentMonth, day)
                        val isMarked = workoutDatesMap.containsKey(date)
                        val isToday = date == today

                        val bgColor = when {
                            isMarked -> MaterialTheme.colorScheme.primary
                            isToday -> MaterialTheme.colorScheme.secondaryContainer
                            else -> Color.LightGray.copy(alpha = 0.3f)
                        }

                        val textColor = when {
                            isMarked -> MaterialTheme.colorScheme.onPrimary
                            isToday -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> Color.DarkGray
                        }

                        Surface(
                            shape = CircleShape,
                            color = bgColor,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    if (isMarked) {
                                        selectedDate = date
                                        showDialog = true
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = day.toString(), color = textColor)
                                    if (isMarked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Workout done",
                                            tint = textColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Motivation
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("\uD83D\uDCAA Motivation", style = MaterialTheme.typography.titleMedium)
                Text("\"${quoteOfTheDay.text}\"", style = MaterialTheme.typography.bodySmall)
                quoteOfTheDay.author?.let {
                    Text("- $it", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }

    // Workout Summary Dialog
    if (showDialog && selectedDate != null) {
        val sessions = workoutDatesMap[selectedDate] ?: emptyList()
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Workouts on ${selectedDate}") },
            text = {
                if (sessions.isEmpty()) {
                    Text("No workouts found.")
                } else {
                    Column {
                        sessions.forEach {
                            Text("• ${it.workoutName} (${it.duration} min)")
                        }
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CS446fit4meTheme {
        HomeScreen(username = "Yash")
    }
}
