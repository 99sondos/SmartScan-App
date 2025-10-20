package com.app.smartscan.frontendScreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.util.Log   // ✅ add this import

@Composable
fun FrontendNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "questionnaire") {

        // 1. Questionnaire screen
        composable("questionnaire") {
            QuestionnaireScreen(
                onFinish = { allAnswered ->
                    // When questionnaire finished — go to Home with completed=true/false
                    navController.navigate("home?completed=$allAnswered")
                }
            )
        }

        // 2. Home screen — can receive a “completed” argument
        composable(
            route = "home?completed={completed}",
            arguments = listOf(
                navArgument("completed") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val completed = backStackEntry.arguments
                ?.getBoolean("completed")
                ?: false

            // ✅ Add this debugging line here
            Log.d("FrontendNavGraph", "Questionnaire completed: $completed")

            HomeScreen(
                questionnaireCompleted = completed, // passes result to Home
                onCreateAccount = { navController.navigate("createAccount") },
                onScanProduct = { /* TODO: Add scan navigation later */ }
            )
        }

        // 3. Create Account screen
        composable(route = "createAccount") {
            CreateAccountScreen(
                onBack = { navController.popBackStack() },
                onCreateAccount = {
                    // TODO: Add logic for account creation or next screen
                    // For now, just go back to Home after creating account
                    navController.popBackStack()
                }
            )
        }
    }
}
