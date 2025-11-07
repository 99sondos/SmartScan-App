package com.app.smartscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onNavigateToQuestionnaire: () -> Unit,
    onNavigateToSkinAnalysis: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Choose how you want to start.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateToQuestionnaire) {
            Text("Answer questions to personalize")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToSkinAnalysis) {
            Text("Scan skin to analyze")
        }
    }
}
