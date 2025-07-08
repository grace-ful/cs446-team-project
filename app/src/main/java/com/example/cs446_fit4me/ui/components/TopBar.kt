package com.example.cs446_fit4me.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit = {}, // Callback for back button
    onSettingsClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title) },
        // change to use our theme colours
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.LightGray,
            titleContentColor = Color.Black,
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (onSettingsClick != null) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        }
        // You can add actions here if needed
        // actions = {
        //     IconButton(onClick = { /* do something */ }) {
        //         Icon(Icons.Filled.Favorite, contentDescription = "Favorite")
        //     }
        // }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAppTopBarWithBack() {
    TopBar(title = "Screen Title", canNavigateBack = true)
}

@Preview(showBackground = true)
@Composable
fun PreviewAppTopBarWithoutBack() {
    TopBar(title = "Screen Title", canNavigateBack = false)
}