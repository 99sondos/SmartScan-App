package com.app.smartscan.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.smartscan.aiCamera.AiCameraActivity
import com.app.smartscan.ui.auth.AuthUiState
import com.app.smartscan.ui.auth.AuthViewModel

@Composable
fun MainContentScreen(authViewModel: AuthViewModel, uiState: AuthUiState, navController: NavController) {
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val analysisType = intent?.getStringExtra("analysis_type")
            val resultText = intent?.getStringExtra("result_text")
            if (resultText != null) {
                when (analysisType) {
                    "ocr" -> authViewModel.onOcrScanClicked(resultText)
                    "barcode" -> authViewModel.onFetchProductClicked(resultText)
                    "skin" -> authViewModel.updateUserSkinTypeFromAnalysis(resultText)
                }
            }
        }
    }

    // Navigate to ResultScreen when a new scanId is available
    LaunchedEffect(uiState.scanId) {
        if (uiState.scanId != null) {
            navController.navigate("result/${uiState.scanId}")
        }
    }

    if (uiState.isSignedIn) {
        AnalysisSelectionScreen(uiState = uiState, onOptionSelected = { type ->
            val intent = Intent(context, AiCameraActivity::class.java)
            intent.putExtra("analysis_type", type)
            cameraLauncher.launch(intent)
        }, onSignOutClicked = {
            authViewModel.onSignOutClicked()
        })
    } else {
        AuthScreen(uiState = uiState,
            onFullNameChange = { authViewModel.onFullNameChange(it) },
            onUsernameChange = { authViewModel.onUsernameChange(it) },
            onEmailChange = { authViewModel.onEmailChange(it) },
            onPasswordChange = { authViewModel.onPasswordChange(it) },
            onSignInClicked = { authViewModel.onSignInClicked() },
            onSignUpClicked = { authViewModel.onSignUpClicked() })
    }
}

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onFullNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign In or Sign Up", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = uiState.fullName, onValueChange = onFullNameChange, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.username, onValueChange = onUsernameChange, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.email, onValueChange = onEmailChange, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.password, onValueChange = onPasswordChange, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onSignInClicked) { Text("Sign In") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onSignUpClicked) { Text("Sign Up") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = uiState.message, color = if (uiState.message.startsWith("Error")) Color.Red else Color.Black)
    }
}

@Composable
fun StatusDisplay(uiState: AuthUiState) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Status: ${uiState.message}",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.scanId != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Current Scan ID: ${uiState.scanId}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AnalysisSelectionScreen(uiState: AuthUiState, onOptionSelected: (String) -> Unit, onSignOutClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatusDisplay(uiState = uiState)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Choose Analysis Mode", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onOptionSelected("skin") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("Skin Analyzer")
        }
        Button(onClick = { onOptionSelected("ocr") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("OCR Text Reader")
        }
        Button(onClick = { onOptionSelected("barcode") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("Barcode Scanner")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onSignOutClicked, modifier = Modifier.fillMaxWidth()) {
            Text("Sign Out")
        }
    }
}