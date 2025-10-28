package com.app.smartscan.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ResultScreen.kt – UI for showing skin analysis results
 */
@Composable
fun ResultScreen(
    type: String,
    result: String,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Analysis Result",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Type: $type", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Text(result, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = { onBack() }) {
                Text("Go Back")
            }
        }
    }
}
