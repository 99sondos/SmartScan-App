package com.app.smartscan.frontendScreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.util.Log
import com.app.smartscan.ui.SplashScreen // import splash screen new

@Composable
fun FrontendNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") { // questionnaire

        // Splash Screen (SmartSkin logo animation)
        composable("splash") {
            SplashScreen(navController)
        }

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

            // Added debugging line here
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

        // 4. Favorites screen
        composable("favorites") {
            FavoriteListScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 5. Blacklist screen
        composable("blacklist") {
            BlacklistScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 6. Result screen
        composable(
            route = "result?imageUri={imageUri}",
            arguments = listOf(
                navArgument("imageUri") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            ResultScreen(
                navController = navController,
                imageUri = imageUri,
                onBack = { navController.popBackStack() }
            )
        }

    }
}
