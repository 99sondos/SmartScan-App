package com.app.smartscan.frontendScreens

import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.smartscan.ui.SplashScreen // från Shaheras version

@Composable
fun FrontendNavGraph() {
    val navController = rememberNavController()

    // 🔹 Spara om användaren har skapat konto
    var accountCreated by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    // 🔹 Startar med Splash (från Shahera)
    NavHost(navController = navController, startDestination = "splash") {

        // Splash Screen shahera
        composable("splash") {
            SplashScreen(navController)
        }

        // Questionnaire
        composable("questionnaire") {
            QuestionnaireScreen(
                onFinish = { allAnswered ->
                    navController.navigate("home?completed=$allAnswered")
                }
            )
        }

        // Home
        composable(
            "home?completed={completed}",
            arguments = listOf(
                navArgument("completed") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val completed = backStackEntry.arguments?.getBoolean("completed") ?: false
            Log.d("FrontendNavGraph", "Questionnaire completed: $completed")

            HomeScreen(
                questionnaireCompleted = completed,
                accountCreated = accountCreated,
                onCreateAccount = { navController.navigate("createAccount") },
                onProfile = { navController.navigate("profile/$userName") },
                onScanProduct = { /* TODO: scan later */ }
            )
        }

        // Create Account
        composable("createAccount") {
            CreateAccountScreen(
                onBack = { navController.popBackStack() },
                onCreateAccount = { name ->
                    accountCreated = true
                    userName = name
                    navController.navigate("profile/$name") {
                        popUpTo("home?completed={completed}") { inclusive = false }
                    }
                }
            )
        }

        // Profile
        composable(
            route = "profile/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { entry ->
            val name = entry.arguments?.getString("name") ?: "User"
            ProfileScreen(
                userName = name,
                onBack = { navController.navigate("home?completed=true") },
                onFavorites = { navController.navigate("favorites") },
                onBlacklist = { navController.navigate("blacklist") }
            )
        }

        // Favorites
        composable("favorites") {
            FavoritesScreen(onBack = { navController.popBackStack() })
        }

        // Blacklist
        composable("blacklist") {
            BlacklistScreen(onBack = { navController.popBackStack() })
        }
    }
}
