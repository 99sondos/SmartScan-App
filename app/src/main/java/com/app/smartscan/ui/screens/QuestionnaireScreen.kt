package com.app.smartscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuestionnaireScreen(
    onQuestionnaireComplete: (String, Boolean, String, List<String>) -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var skinType by remember { mutableStateOf("") }
    var isSensitive by remember { mutableStateOf(false) }
    var ageRange by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (step) {
            0 -> {
                Text("Select your skin type")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { skinType = "Dry"; step++ }) { Text("Dry") }
                Button(onClick = { skinType = "Oily"; step++ }) { Text("Oily") }
                Button(onClick = { skinType = "Combination"; step++ }) { Text("Combination") }
                Button(onClick = { skinType = "Normal"; step++ }) { Text("Normal") }
            }
            1 -> {
                Text("Is your skin sensitive?")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { isSensitive = true; step++ }) { Text("Yes") }
                Button(onClick = { isSensitive = false; step++ }) { Text("No") }
            }
            2 -> {
                Text("Select your age range")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { ageRange = "<18"; step++ }) { Text("<18") }
                Button(onClick = { ageRange = "18-25"; step++ }) { Text("18-25") }
                Button(onClick = { ageRange = "26-40"; step++ }) { Text("26-40") }
                Button(onClick = { ageRange = "40+"; step++ }) { Text("40+") }
            }
            3 -> {
                onQuestionnaireComplete(skinType, isSensitive, ageRange, emptyList())
            }
        }
    }
}
