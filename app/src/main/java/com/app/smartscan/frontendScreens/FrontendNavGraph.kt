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


        //  New StartPage route
        composable("start") {
            StartPage(
                onAnswerQuestions = {
                    navController.navigate("questionnaire?skipFirstTwo=false&cameFromAnalyzer=false")
                },
                onSkinAnalyzer = {
                    navController.navigate("questionnaire?skipFirstTwo=true&cameFromAnalyzer=true")
                }
            )

        }




        // 1. Questionnaire screen
        composable(
            route = "questionnaire?skipFirstTwo={skipFirstTwo}&cameFromAnalyzer={cameFromAnalyzer}",
            arguments = listOf(
                navArgument("skipFirstTwo") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("cameFromAnalyzer") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { entry ->
            val skip = entry.arguments?.getBoolean("skipFirstTwo") ?: false
            val cameFromAnalyzer = entry.arguments?.getBoolean("cameFromAnalyzer") ?: false

            QuestionnaireScreen(
                skipFirstTwo = skip,
                cameFromAnalyzer = cameFromAnalyzer,
                onFinish = { allAnswered ->
                    navController.navigate("home?completed=$allAnswered&cameFromAnalyzer=$cameFromAnalyzer")
                }
            )
        }




        // 2. Home screen — can receive a “completed” argument
        composable(
            route = "home?completed={completed}&cameFromAnalyzer={cameFromAnalyzer}",
            arguments = listOf(
                navArgument("completed") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("cameFromAnalyzer") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val completed = backStackEntry.arguments?.getBoolean("completed") ?: false
            val cameFromAnalyzer = backStackEntry.arguments?.getBoolean("cameFromAnalyzer") ?: false

            HomeScreen(
                questionnaireCompleted = completed,
                cameFromAnalyzer = cameFromAnalyzer,   // ⬅️ viktigt!
                onCreateAccount = { navController.navigate("createAccount") },
                onScanProduct = { /* TODO */ },
                onGoToProfile = { navController.navigate("profile") } // eller ta bort parametern helt
            )
        }



        composable("profile") {
            MyProfileScreen(
                onScanProduct = {
                    // TODO: scanner
                },
                onViewMyProducts = {
                    navController.navigate("viewMyProducts")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("viewMyProducts") {
            ViewMyProductsScreen(
                favourites = emptyList(),
                blacklist = emptyList(),
                onBack = { navController.popBackStack() }
            )
        }



        // 3. Create Account screen
        composable("createAccount") {
            CreateAccountScreen(
                onBack = { navController.popBackStack() },
                onCreateAccount = {
                    navController.navigate("profile") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }

    }
}
