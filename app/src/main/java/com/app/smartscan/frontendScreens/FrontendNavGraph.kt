package com.app.smartscan.frontendScreens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// File that handles all frontend navigation (the heart of the frontend navigation).
// Fatima and I can work here without touching other teams logic in MainActivity.kt
@Composable
fun FrontendNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "questionnaire") {
        composable("questionnaire") {
            QuestionnaireScreen(
                onFinish = { navController.navigate("main") },
                // onBack = { navController.popBackStack() }
            )
        }
        composable("main") {
            MainScreen()
        }
    }
}
