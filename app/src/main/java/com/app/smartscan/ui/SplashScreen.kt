package com.app.smartscan.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Background and logo animation container
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFDF8F2) // same beige background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SmartSkinLogo() // my animated logo
        }
    }

    // After a few seconds go to Questionnaire
    LaunchedEffect(Unit) {
        delay(4000) // 4 seconds splash duration
        navController.navigate("start") {
            popUpTo("splash") { inclusive = true } // remove splash from back stack
        }
    }
}
