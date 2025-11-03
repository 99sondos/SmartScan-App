package com.app.smartscan.frontendScreens

import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun FrontendNavGraph() {
    val navController = rememberNavController()

    // 🔹 Här sparar vi om kontot är skapat
    var accountCreated by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "questionnaire") {

        composable("questionnaire") {
            QuestionnaireScreen(
                onFinish = { allAnswered ->
                    navController.navigate("home?completed=$allAnswered")
                }
            )
        }

        composable(
            "home?completed={completed}",
            arguments = listOf(
                navArgument("completed") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { entry ->
            val completed = entry.arguments?.getBoolean("completed") ?: false
            Log.d("FrontendNavGraph", "Questionnaire completed: $completed")

            HomeScreen(
                questionnaireCompleted = completed,
                accountCreated = accountCreated,
                onCreateAccount = { navController.navigate("createAccount") },
                onProfile = { navController.navigate("profile/$userName") },
                onScanProduct = { /* TODO: scan later */ }
            )
        }

        composable("createAccount") {
            CreateAccountScreen(
                onBack = { navController.popBackStack() },
                onCreateAccount = { name ->
                    // 🔹 Spara att användaren skapat konto
                    accountCreated = true
                    userName = name
                    navController.navigate("profile/$name") {
                        popUpTo("home?completed={completed}") { inclusive = false }
                    }
                }
            )
        }

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

        composable("favorites") {
            FavoritesScreen(onBack = { navController.popBackStack() })
        }

        composable("blacklist") {
            BlacklistScreen(onBack = { navController.popBackStack() })
        }
    }
}
