package com.app.smartscan.frontendScreens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.smartscan.aiCamera.AiCameraActivity
import com.app.smartscan.ui.SplashScreen
import com.app.smartscan.ui.auth.AuthViewModel

@Composable
fun FrontendNavGraph(authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)) {
    val navController = rememberNavController()
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val barcode = result.data?.getStringExtra("barcode")
            val ocrText = result.data?.getStringExtra("ocrText")
            val skinAnalysisResult = result.data?.getStringExtra("skinAnalysisResult")

            if (barcode != null) {
                authViewModel.onFetchProductClicked(barcode)
                navController.navigate("result")
            } else if (ocrText != null) {
                authViewModel.onOcrScanClicked(ocrText)
                navController.navigate("result")
            } else if (skinAnalysisResult != null) {
                authViewModel.updateUserSkinTypeFromAnalysis(skinAnalysisResult)
                navController.navigate("home?cameFromAnalyzer=true") { 
                     popUpTo(navController.graph.findStartDestination().id) 
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") { SplashScreen(navController) }

        composable("start") {
            StartPage(
                onAnswerQuestions = { navController.navigate("questionnaire?skipFirstTwo=false&cameFromAnalyzer=false") },
                onSkinAnalyzer = {
                    val intent = Intent(context, AiCameraActivity::class.java)
                    intent.putExtra("analysisType", "skin")
                    cameraLauncher.launch(intent)
                },
                onLoginClicked = { navController.navigate("login") }
            )
        }

        composable("login") {
            LaunchedEffect(uiState.isSignedIn, uiState.isAnonymous) {
                if (uiState.isSignedIn && !uiState.isAnonymous) {
                    navController.navigate("home") { 
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                uiState = uiState,
                onEmailChanged = authViewModel::onEmailChange,
                onPasswordChanged = authViewModel::onPasswordChange,
                onBack = { navController.popBackStack() },
                onLogin = { authViewModel.onSignInClicked() }
            )
        }

        composable(
            route = "questionnaire?skipFirstTwo={skipFirstTwo}&cameFromAnalyzer={cameFromAnalyzer}",
            arguments = listOf(
                navArgument("skipFirstTwo") { type = NavType.BoolType; defaultValue = false },
                navArgument("cameFromAnalyzer") { type = NavType.BoolType; defaultValue = false }
            )
        ) { entry ->
            QuestionnaireScreen(
                skipFirstTwo = entry.arguments?.getBoolean("skipFirstTwo") ?: false,
                cameFromAnalyzer = entry.arguments?.getBoolean("cameFromAnalyzer") ?: false,
                onFinish = { allAnswered, skinType, isSensitive, ageRange, allergies ->
                    authViewModel.onQuestionnaireSubmitted(skinType, isSensitive, ageRange, allergies)
                    navController.navigate("home?completed=$allAnswered&cameFromAnalyzer=${entry.arguments?.getBoolean("cameFromAnalyzer")}")
                }
            )
        }

        composable(
            route = "home?completed={completed}&cameFromAnalyzer={cameFromAnalyzer}",
            arguments = listOf(
                navArgument("completed") { type = NavType.BoolType; defaultValue = false },
                navArgument("cameFromAnalyzer") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            HomeScreen(
                uiState = uiState, // Pass the whole state
                onClearMessage = authViewModel::clearMessage, // Pass the clear function
                questionnaireCompleted = backStackEntry.arguments?.getBoolean("completed") ?: false,
                cameFromAnalyzer = backStackEntry.arguments?.getBoolean("cameFromAnalyzer") ?: false,
                onCreateAccount = { navController.navigate("createAccount") },
                onScanProduct = {
                    val intent = Intent(context, AiCameraActivity::class.java)
                    intent.putExtra("analysisType", "product")
                    cameraLauncher.launch(intent)
                },
                onGoToProfile = { navController.navigate("profile") }
            )
        }

        composable("profile") {
            MyProfileScreen(
                onScanProduct = {
                    val intent = Intent(context, AiCameraActivity::class.java)
                    intent.putExtra("analysisType", "product")
                    cameraLauncher.launch(intent)
                },
                onViewMyProducts = { navController.navigate("viewMyProducts") },
                onBack = { navController.popBackStack() },
                onSignOut = {
                    authViewModel.onSignOutClicked()
                    navController.navigate("start") { 
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            )
        }

        composable("viewMyProducts") {
            ViewMyProductsScreen(
                favourites = emptyList(),
                blacklist = emptyList(),
                onBack = { navController.popBackStack() }
            )
        }

        composable("createAccount") {
            LaunchedEffect(uiState.isSignedIn, uiState.isAnonymous) {
                if (uiState.isSignedIn && !uiState.isAnonymous) {
                    navController.navigate("home") { 
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            }

            CreateAccountScreen(
                uiState = uiState,
                onFullNameChanged = authViewModel::onFullNameChange,
                onUsernameChanged = authViewModel::onUsernameChange,
                onEmailChanged = authViewModel::onEmailChange,
                onPasswordChanged = authViewModel::onPasswordChange,
                onBack = { navController.popBackStack() },
                onCreateAccount = { authViewModel.onSignUpClicked() }
            )
        }

        composable(
            route = "result",
        ) { 
            ResultScreen(
                navController = navController,
                scanId = uiState.scanId,
                observeScan = authViewModel::observeScan,
                getProduct = authViewModel::getProduct,
                isGuest = uiState.isAnonymous,
                onAddToFavorites = authViewModel::onAddToFavoritesClicked,
                onAddToBlacklist = authViewModel::onAddToBlacklistClicked,
                onBack = { navController.popBackStack() }
            )
        }
    }
}