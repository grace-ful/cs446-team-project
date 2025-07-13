package com.example.cs446_fit4me.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cs446_fit4me.model.UserMatch
import androidx.compose.ui.tooling.preview.Preview
import com.example.cs446_fit4me.model.*

@Composable
fun MatchDetailSheet(
    match: UserMatch,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // Header with avatar and close button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = match.name.first().toString(),
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = match.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Card for details
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ProfileField(label = "Age", value = match.age.toString())
                ProfileField(label = "Location", value = match.location)
                ProfileField(label = "Time Preference", value = match.timePreference.name)
                ProfileField(label = "Experience Level", value = match.experienceLevel.name)
                ProfileField(label = "Gym Frequency", value = match.gymFrequency.name)
                ProfileField(
                    label = "Match Score",
                    value = formatScore(match.score),
                    valueColor = when {
                        match.score < 60 -> MaterialTheme.colorScheme.error
                        match.score < 85 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor
        )
    }
}






@Preview(showBackground = true)
@Composable
fun PreviewMatchDetailSheet() {
    MatchDetailSheet(
        match = UserMatch(
            name = "Aryaman",
            age = 22,
            location = "Toronto",
            timePreference = TimePreference.EVENING,
            experienceLevel = ExperienceLevel.INTERMEDIATE,
            gymFrequency = GymFrequency.REGULARLY,
            score = 91.0
        ),
        onClose = {}
    )
}
