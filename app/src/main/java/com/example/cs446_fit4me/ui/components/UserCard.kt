package com.example.cs446_fit4me.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cs446_fit4me.model.UserMatch
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun formatScore(score: Double): String {
    return if (score % 1.0 == 0.0) {
        // Whole number, show as integer
        score.toInt().toString()
    } else {
        // Has decimals, show with one decimal point
        String.format("%.1f", score)
    }
}

@Composable
fun UserCard(
    match: UserMatch,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            // Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = match.name.first().toString(),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name (expands)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = match.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Score (right-aligned, colored)
            val scoreColor = when {
                match.score < 50 -> MaterialTheme.colorScheme.error
                match.score < 75 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            }
            Text(
                text = "Score: ${formatScore(match.score)}",
                color = scoreColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewUserCard() {
    UserCard(
        match = UserMatch(
            name = "Aryaman",
            age = 22,
            location = "Toronto",
            timePreference = com.example.cs446_fit4me.model.TimePreference.EVENING,
            experienceLevel = com.example.cs446_fit4me.model.ExperienceLevel.INTERMEDIATE,
            gymFrequency = com.example.cs446_fit4me.model.GymFrequency.REGULARLY,
            score = 70.3
        ),
        onClick = {}
    )
}
