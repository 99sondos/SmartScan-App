package com.app.smartscan.frontendScreens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StartPage(
    onAnswerQuestions: () -> Unit,
    onSkinAnalyzer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to SmartScan")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAnswerQuestions,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Answer Questions")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSkinAnalyzer()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Skin Analyzer")
        }

    }
}
