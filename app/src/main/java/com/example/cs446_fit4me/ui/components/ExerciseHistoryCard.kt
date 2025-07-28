package com.example.cs446_fit4me.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ExerciseHistoryCard(
    exerciseName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = exerciseName,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseHistoryCardPreview() {
    ExerciseHistoryCard(
        exerciseName = "Barbell Bench Press",
        onClick = {}
    )
}
