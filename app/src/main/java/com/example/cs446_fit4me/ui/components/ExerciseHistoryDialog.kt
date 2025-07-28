package com.example.cs446_fit4me.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.cs446_fit4me.model.ExerciseSessionHistoryResponse
import com.example.cs446_fit4me.model.PRResponse
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Helper: PR date without year
fun formatDateNoYear(dateString: String): String {
    val date = OffsetDateTime.parse(dateString)
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())
    return date.format(formatter)
}

// Set Info String Formatter (e.g. "50 lb x 12")
fun formatSetInfo(weight: Float?, reps: Int): String {
    return if (weight != null) "${weight.toInt()} lb x $reps"
    else "$reps reps"
}

// PR Row
@Composable
fun PRRow(pr: PRResponse?) {
    if (pr == null || pr.weight == null) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("🏆", fontSize = 24.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            "${pr.weight.toInt()} lb x ${pr.duration ?: ""}", // duration is rep count in this model
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.width(8.dp))
        Text(
            formatDateNoYear(pr.date),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// Expandable History Card
@Composable
fun ExpandableHistoryCard(session: ExerciseSessionHistoryResponse) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = formatFullDateTime(session.date),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (expanded) {
                session.sets.forEachIndexed { idx, set ->
                    Text(
                        text = "${idx + 1}. ${formatSetInfo(set.weight, set.reps)}",
                        modifier = Modifier.padding(start = 32.dp, bottom = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// Main Dialog
@Composable
fun ExerciseDetailsDialog(
    exerciseName: String,
    onDismiss: () -> Unit,
    pr: PRResponse?,
    history: List<ExerciseSessionHistoryResponse>
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp
        ) {
            Column(Modifier.padding(20.dp)) {
                // Title and Close Icon
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = exerciseName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp, start = 32.dp, end = 32.dp)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // PR Row
                PRRow(pr = pr)

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                // Expandable History Cards
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                ) {
                    items(history) { session ->
                        ExpandableHistoryCard(session)
                    }
                }
            }
        }
    }
}
