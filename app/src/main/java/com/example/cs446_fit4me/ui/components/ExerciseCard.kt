package com.example.cs446_fit4me.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.cs446_fit4me.model.Exercise
import com.example.cs446_fit4me.model.MuscleGroup
import com.example.cs446_fit4me.model.Equipment
import com.example.cs446_fit4me.model.BodyPart
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun ExerciseListItem(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onInfoClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image or circle letter
        if (exercise.imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(exercise.imageUrl),
                contentDescription = exercise.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = exercise.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Name and body part: flexible width
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // truncate if too long
            )
            Text(
                text = exercise.bodyPart.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Fixed width action buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (onInfoClick != null) {
                IconButton(onClick = onInfoClick) {
                    Icon(Icons.Default.Info, contentDescription = "Exercise Info")
                }
            }
            if (onEditClick != null) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Exercise")
                }
            }
            if (onDeleteClick != null) {
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Exercise")
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ExerciseListItemPreview() {
    val sampleExercise = Exercise(
        name = "Push Up",
        muscleGroup = MuscleGroup.CHEST,
        equipment = Equipment.NONE,
        bodyPart = BodyPart.CHEST,
        description = "Sample exercise",
        isGeneric = true,
        imageUrl = null,
        id = "1"
    )
    ExerciseListItem(exercise = sampleExercise)
}
