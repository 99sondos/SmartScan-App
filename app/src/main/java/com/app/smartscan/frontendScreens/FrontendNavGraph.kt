package com.app.smartscan.frontendScreens

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.smartscan.aiCamera.AiCameraActivity
import com.app.smartscan.ui.SplashScreen

@Composable
fun FrontendNavGraph() {
    val navController = rememberNavController()

    // ✅ Lokalt state (som du hade innan)
    var accountCreated by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    // ✅ START: Splash
    NavHost(navController = navController, startDestination = "splash") {

        // 1) Splash ➜ Welcome
        composable("splash") {
            SplashScreen(

                onTimeout = {
                    navController.navigate("welcome") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // 2) Welcome med två val: frågor eller skin-scan
        composable("welcome") {
            WelcomeScreen(
                onStartQuestions = {
                    // ➜ Frågesidan
                    navController.navigate("questionnaire")
                },
                onStartSkinScan = { context ->
                    // ➜ Starta hud-kameran
                    val intent = Intent(context, AiCameraActivity::class.java)
                        .putExtra("analysis_type", "skin")
                    context.startActivity(intent)

                    // ➜ Efter scan: till profil
                    navController.navigate("home?completed=true") {
                        popUpTo("welcome") { inclusive = false }
                    }
                }
            )
        }

        // 3) Frågor ➜ när klar: Profile
        composable("questionnaire") {
            QuestionnaireScreen(
                onFinish = { allAnswered ->
                    Log.d("FrontendNavGraph", "Questionnaire completed: $allAnswered")
                    navController.navigate("home?completed=$allAnswered") {
                        popUpTo("welcome") { inclusive = false }
                    }
                }
            )
        }

        // 4) Profile (Home) – oförändrad logik
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
                onScanProduct = {
                    // (din produktkamera/placeholder triggas inne i Home/profil – låt vara)
                }
            )
        }

        // Create account ➜ gå till profil (samma som du hade)
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

        // Profil + favoriter/blacklist – oförändrat
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

        composable("favorites") { FavoritesScreen(onBack = { navController.popBackStack() }) }
        composable("blacklist") { BlacklistScreen(onBack = { navController.popBackStack() }) }
    }
}
