package com.app.smartscan.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.smartscan.ui.auth.AuthViewModel
import com.app.smartscan.ui.screens.MainContentScreen
import com.app.smartscan.ui.screens.QuestionnaireScreen
import com.app.smartscan.ui.screens.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val uiState by authViewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            // This effect runs once when the destination is first composed
            // or if the user's sign-in status changes.
            LaunchedEffect(uiState.isSignedIn, uiState.isAnonymous) {
                if (uiState.isSignedIn && !uiState.isAnonymous) {
                    // If a registered user is signed in, navigate to main and clear the stack.
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            }

            // Only show the WelcomeScreen if we are a guest. This prevents flickering.
            if (!uiState.isSignedIn || uiState.isAnonymous) {
                WelcomeScreen(
                    onNavigateToQuestionnaire = { navController.navigate("questionnaire") },
                    onNavigateToSkinAnalysis = { navController.navigate("main") }
                )
            }
        }
        composable("questionnaire") {
            QuestionnaireScreen {
                skinType, isSensitive, ageRange, allergies ->
                authViewModel.onQuestionnaireSubmitted(skinType, isSensitive, ageRange, allergies)
                navController.navigate("main") { popUpTo("welcome") { inclusive = true } }
            }
        }
        composable("main") {
            MainContentScreen(authViewModel, uiState, navController)
        }
        composable(
            "result/{scanId}",
            arguments = listOf(navArgument("scanId") { type = NavType.StringType })
        ) {
            backStackEntry ->
            val scanId = backStackEntry.arguments?.getString("scanId") ?: ""
            ResultScreen(scanId, navController)
        }
    }
}