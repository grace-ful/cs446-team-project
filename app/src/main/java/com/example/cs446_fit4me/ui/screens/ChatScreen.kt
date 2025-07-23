package com.example.cs446_fit4me.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cs446_fit4me.model.ChatMessage
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val estZone = ZoneId.of("America/New_York")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    onSend: (String) -> Unit,
    currentUserId: String,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Group messages by date
    val groupedMessages = messages
        .sortedBy { it.createdAt }
        .groupBy { formatDateHeader(it.createdAt) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            state = listState
        ) {
            groupedMessages.forEach { (dateLabel, messagesForDate) ->
                item {
                    Text(
                        text = dateLabel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                items(messagesForDate, key = { it.id ?: it.hashCode() }) { msg ->
                    val isCurrentUser = msg.senderId == currentUserId
                    Row(
                        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Surface(
                            color = if (isCurrentUser) Color.Blue else Color.Gray,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = msg.content,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatTime(msg.createdAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                singleLine = true
            )

            if (text.isNotBlank()) {
                IconButton(
                    onClick = {
                        onSend(text)
                        text = ""
                    },
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(48.dp)
                        .background(
                            color = Color(0xff5c4896),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// Parses ISO string to ZonedDateTime
private fun parseZonedDateTime(dateStr: String): ZonedDateTime? {
    if (dateStr.isBlank()) return null
    return try {
        ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        null
    }
}

// Format like WhatsApp: "Today", "Yesterday", "Monday", or "Apr 12"
private fun formatDateHeader(dateStr: String): String {
    val date = parseZonedDateTime(dateStr)?.withZoneSameInstant(estZone)
    val now = ZonedDateTime.now(estZone)
    val today = now.toLocalDate()
    val msgDate = date?.toLocalDate()

    return when {
        msgDate == null -> "Unknown Date"
        msgDate.isEqual(today) -> "Today"
        msgDate.isEqual(today.minusDays(1)) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("MMM d"))
    }
}


// Format time like "4:30 PM" in EST
private fun formatTime(dateStr: String): String {
    val date = parseZonedDateTime(dateStr)?.withZoneSameInstant(estZone)
    return date?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: ""
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val sampleMessages = listOf(
        ChatMessage(
            id = "1",
            senderId = "user1",
            receiverId = "user2",
            content = "Hey! How are you doing?",
            createdAt = "2025-07-22T10:00:00Z"
        ),
        ChatMessage(
            id = "2",
            senderId = "user2",
            receiverId = "user1",
            content = "I'm doing great, thanks! What about you?",
            createdAt = "2025-07-22T10:01:00Z"
        ),
        ChatMessage(
            id = "3",
            senderId = "user1",
            receiverId = "user2",
            content = "Awesome! Just finished a workout 💪",
            createdAt = "2025-07-21T09:02:00Z"
        ),
        ChatMessage(
            id = "4",
            senderId = "user2",
            receiverId = "user1",
            content = "Nice! 💥",
            createdAt = "2025-07-20T12:10:00Z"
        )
    )

    MaterialTheme {
        ChatScreen(
            messages = sampleMessages,
            onSend = {},
            currentUserId = "user1",
            onBack = {}
        )
    }
}
