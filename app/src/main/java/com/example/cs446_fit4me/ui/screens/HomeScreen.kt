package com.example.cs446_fit4me.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.cs446_fit4me.chat.GlobalChatSocketManager
import com.example.cs446_fit4me.datastore.SessionManager
import com.example.cs446_fit4me.model.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
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

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val userId = SessionManager(context).getUserId()

        if (!userId.isNullOrBlank()) {
            GlobalChatSocketManager.initWithSession(context)
            GlobalChatSocketManager.setOnGlobalMessageReceived(context) { msg ->
                // Optional: handle UI or ViewModel updates
            }
        }
    }

    val workoutSessionViewModel: WorkoutSessionViewModel = viewModel()
    workoutSessionViewModel.initApi(context)
    workoutSessionViewModel.fetchWorkoutHistory()
    workoutSessionViewModel.fetchExerciseHistory()

    val workoutHistoryState = workoutSessionViewModel.historyState.collectAsState().value
    val workoutDatesMap = (workoutHistoryState as? WorkoutSessionHistoryState.Success)?.sessions
        ?.mapNotNull { runCatching { LocalDate.parse(it.workoutDate, DateTimeFormatter.ISO_DATE) to it }.getOrNull() }
        ?.groupBy({ it.first }, { it.second })
        ?: emptyMap()

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
            Text("Hey ${username.split(" ").first()} 👋", style = MaterialTheme.typography.headlineMedium)

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

        Text("Quick Access", style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SquareButton("Workouts", onClick = { navController?.navigate("workout") }, modifier = Modifier.weight(1f))
                SquareButton("Exercises", onClick = { navController?.navigate("exercises") }, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SquareButton("Messages", onClick = { navController?.navigate("messages") }, modifier = Modifier.weight(1f))
                SquareButton("Match", onClick = { navController?.navigate("find_match") }, modifier = Modifier.weight(1f))
            }
        }


        // Calendar Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { visibleMonth = visibleMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
            }
            Text(text = monthYearLabel, style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { visibleMonth = visibleMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
            }
        }

        // Day of Week Header (Sun to Sat)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(
                java.time.DayOfWeek.SUNDAY,
                java.time.DayOfWeek.MONDAY,
                java.time.DayOfWeek.TUESDAY,
                java.time.DayOfWeek.WEDNESDAY,
                java.time.DayOfWeek.THURSDAY,
                java.time.DayOfWeek.FRIDAY,
                java.time.DayOfWeek.SATURDAY
            ).forEach { day ->
                val label = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    Text(label, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Calendar Grid
        val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
        val startDayOfWeekIndex = firstDayOfMonth.dayOfWeek.value % 7
        val totalCells = daysInMonth + startDayOfWeekIndex
        val weeks = (totalCells + 6) / 7
        var dayCounter = 1

        repeat(weeks) { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(7) { dow ->
                    val cellIndex = week * 7 + dow
                    if (cellIndex < startDayOfWeekIndex || dayCounter > daysInMonth) {
                        Spacer(Modifier.size(40.dp))
                    } else {
                        val date = LocalDate.of(currentYear, currentMonth, dayCounter)
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
                                    Text(dayCounter.toString(), color = textColor)
                                    if (isMarked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = textColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                        dayCounter++
                    }
                }
            }
        }

        // Motivation Quote
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
            title = { Text("Workouts on $selectedDate") },
            text = {
                if (sessions.isEmpty()) Text("No workouts found.")
                else Column { sessions.forEach { Text("• ${it.workoutName} (${it.duration} min)") } }
            }
        )
    }
}

@Composable
fun SquareButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .height(60.dp)
    ) {
        Text(label)
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CS446fit4meTheme {
        HomeScreen(username = "Yash")
    }
}
