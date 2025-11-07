package com.app.smartscan.frontendScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.smartscan.R

@Composable
fun QuestionnaireScreen(
    skipFirstTwo: Boolean = false,
    cameFromAnalyzer: Boolean = false,
    onFinish: (Boolean, String, Boolean, String, List<String>) -> Unit
) {
    val questions = listOf("skinType", "sensitive", "ageRange", "allergies")

    var currentIndex by remember { mutableStateOf(if (skipFirstTwo) 2 else 0) }

    // State for all answers
    var skinType by remember { mutableStateOf("") }
    var isSensitive by remember { mutableStateOf(false) }
    var ageRange by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf<List<String>>(emptyList()) }
    
    var answeredQuestions by remember { mutableStateOf(mutableSetOf<String>()) }

    fun handleFinish() {
        val allAnswered = answeredQuestions.size == questions.size
        onFinish(allAnswered, skinType, isSensitive, ageRange, allergies)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = (currentIndex + 1) / questions.size.toFloat(),
            modifier = Modifier.fillMaxWidth().height(8.dp).padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        when (questions[currentIndex]) {
            "skinType" -> SkinTypeQuestion(
                selectedType = skinType,
                onAnswer = { selected ->
                    skinType = selected
                    answeredQuestions.add("skinType")
                    if (currentIndex < questions.lastIndex) currentIndex++ else handleFinish()
                },
                onSkip = { if (currentIndex < questions.lastIndex) currentIndex++ else handleFinish() }
            )

            "sensitive" -> SimpleQuestion(
                question = "Is your skin sensitive?",
                options = listOf("Yes", "No"),
                selectedOption = if(isSensitive) "Yes" else "No",
                onAnswer = { answer ->
                    isSensitive = answer == "Yes"
                    answeredQuestions.add("sensitive")
                    if (currentIndex < questions.lastIndex) currentIndex++ else handleFinish()
                },
                onBack = { if (currentIndex > 0) currentIndex-- },
                onSkip = { if (currentIndex < questions.lastIndex) currentIndex++ else handleFinish() }
            )

            "ageRange" -> SimpleQuestion(
                question = "Select your age range",
                options = listOf("<18", "18–25", "26–40", "40+"),
                selectedOption = ageRange,
                onAnswer = { selected ->
                    ageRange = selected
                    answeredQuestions.add("ageRange")
                    if (currentIndex < questions.lastIndex) currentIndex++ else handleFinish()
                },
                onBack = { if (currentIndex > 0) currentIndex-- },
                onSkip = { if (currentIndex < questions.lastIndex) currentIndex++ else handleFinish() }
            )
            
            "allergies" -> AllergyQuestion(
                currentAllergies = allergies,
                onNext = { list ->
                    allergies = list
                    answeredQuestions.add("allergies")
                    if (currentIndex < questions.lastIndex) currentIndex++ else handleFinish()
                },
                onBack = { if (currentIndex > 0) currentIndex-- },
                onSkip = { if (currentIndex < questions.lastIndex) currentIndex++ else handleFinish() }
            )
        }
    }
}

@Composable
fun SkinTypeQuestion(
    selectedType: String,
    onAnswer: (String) -> Unit,
    onSkip: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Select your skin type", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))

            val skinTypes = listOf("Dry" to R.drawable.cracked, "Oily" to R.drawable.leaf_oily, "Combination" to R.drawable.combo, "Normal" to R.drawable.normal)

            for (pair in skinTypes.chunked(2)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    for ((label, imageRes) in pair) {
                        ElevatedButton(
                            onClick = { onAnswer(label) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(8.dp).weight(1f).height(140.dp),
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = if (selectedType == label) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(painter = painterResource(id = imageRes), contentDescription = label, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(label)
                            }
                        }
                    }
                }
            }
        }
        TextButton(onClick = onSkip, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Text("Skip for now", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun SimpleQuestion(
    question: String,
    options: List<String>,
    selectedOption: String,
    onAnswer: (String) -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
            Text("⬅ Back", color = MaterialTheme.colorScheme.primary)
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = question, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            options.forEach { option ->
                OutlinedButton(
                    onClick = { onAnswer(option) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedOption == option) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                ) {
                    Text(option)
                }
            }
        }
        TextButton(onClick = onSkip, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Text("Skip for now", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun AllergyQuestion(
    currentAllergies: List<String>,
    onNext: (List<String>) -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    var selectedOptions by remember { mutableStateOf(currentAllergies.toSet()) }
    var customAllergy by remember { mutableStateOf("") }
    var otherSelected by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
            Text("⬅ Back", color = MaterialTheme.colorScheme.primary)
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Any allergies?", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))

            val options = listOf("Perfume", "Alcohol")
            options.forEach { option ->
                OutlinedButton(
                    onClick = { 
                        selectedOptions = if (selectedOptions.contains(option)) selectedOptions - option else selectedOptions + option
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedOptions.contains(option)) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                ) {
                    Text(option)
                }
            }
            OutlinedButton(
                onClick = { otherSelected = !otherSelected },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (otherSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
            ) {
                Text("Other")
            }

            if (otherSelected) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = customAllergy,
                    onValueChange = { customAllergy = it },
                    label = { Text("Please specify and press continue") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { 
                val finalList = selectedOptions.toMutableList()
                if (otherSelected && customAllergy.isNotBlank()) {
                    finalList.add(customAllergy)
                }
                onNext(finalList)
            }) {
                Text("Continue")
            }
        }
        TextButton(onClick = onSkip, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Text("Skip for now", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
        }
    }
}
