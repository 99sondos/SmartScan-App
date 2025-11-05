package com.app.smartscan.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Background and logo animation container
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFDF8F2) // same beige background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SmartSkinLogo() // your animated logo (unchanged)
        }
    }

    // ✅ Wait and call callback
    LaunchedEffect(Unit) {
        delay(4000) // 4 seconds splash duration
        onTimeout()
    }
}
