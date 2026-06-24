package com.app.smartscan.frontendScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.smartscan.R
import androidx.compose.ui.tooling.preview.Preview
import com.app.smartscan.ui.auth.AuthUiState

@Composable
fun HomeScreen(
    uiState: AuthUiState,
    onClearMessage: () -> Unit,
    questionnaireCompleted: Boolean,
    cameFromAnalyzer: Boolean,
    onCreateAccount: () -> Unit,
    onScanProduct: () -> Unit,
    onGoToProfile: () -> Unit
) {
    if (cameFromAnalyzer && uiState.message.isNotEmpty() && uiState.message.startsWith("AI Analysis")) {
        val rawResult = uiState.message.substringAfter("AI Analysis Result: ").trim()
        val parts = rawResult.split("AI feedback:", limit = 2)
        val dataPart = parts.getOrNull(0)?.trim()
        val feedbackPart = parts.getOrNull(1)?.trim()

        AlertDialog(
            onDismissRequest = { onClearMessage() },
            title = { Text("Skin Analysis Result", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = {
                Column {
                    if (dataPart != null) {
                        Text(
                            "AI Analysis Result:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        dataPart.lines().forEach { line ->
                            if (line.contains(':')) {
                                val (key, value) = line.split(":", limit = 2)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$key:",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(120.dp) // Align values
                                    )
                                    Text(text = value.trim())
                                }
                            } else {
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (feedbackPart != null) {
                        Text(
                            "AI Feedback:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = feedbackPart, textAlign = TextAlign.Justify)
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { onClearMessage() }) {
                        Text("OK")
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.smartskin_logo),
            contentDescription = "SmartSkin Logo",
            modifier = Modifier
                .size(255.dp)
                .padding(top = 32.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isAnonymous) {
                Button(
                    onClick = onCreateAccount,
                    enabled = questionnaireCompleted || cameFromAnalyzer,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (questionnaireCompleted || cameFromAnalyzer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(64.dp)
                ) {
                    Text("Create Account", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                }

                if (!questionnaireCompleted && !cameFromAnalyzer) {
                    Text(
                        text = "Complete the questionnaire to create an account.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                Button(
                    onClick = onGoToProfile,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(64.dp)
                ) {
                    Text("My Profile", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                }
            }

            OutlinedButton(
                onClick = onScanProduct,
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(64.dp)
            ) {
                Text("Scan Product", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Your personal skincare assistant\n" +
                "Scan products, explore ingredients, and find what’s best for your skin",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenWithDialogPreview() {
    MaterialTheme {
        HomeScreen(
            uiState = AuthUiState(isAnonymous = true, message = "AI Analysis Result: Skin tone: Medium\nSkin type: Normal\nAvg brightness: 0.65\nStd deviation: 0.12\n(Sampled 5000 pixels)\n\nAI feedback:\nYour skin exhibits a medium tone and a normal skin type, indicating a balanced moisture level without excess oil or dryness."),
            onClearMessage = {},
            onCreateAccount = {},
            questionnaireCompleted = true,
            cameFromAnalyzer = true, // To show the dialog
            onScanProduct = {},
            onGoToProfile = {}
        )
    }
}
