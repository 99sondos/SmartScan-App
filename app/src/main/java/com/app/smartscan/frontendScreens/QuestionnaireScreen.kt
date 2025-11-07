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

// Main composable for showing all the questions one by one
@Composable
fun QuestionnaireScreen(
    skipFirstTwo: Boolean = false,
    cameFromAnalyzer: Boolean = false,
    onFinish: (Boolean) -> Unit
)
{
    val questions = listOf("skinType", "sensitive", "allergies", "ageRange")

    var currentIndex by remember {
        mutableStateOf(if (skipFirstTwo) 2 else 0)
    }

    var answeredQuestions by remember { mutableStateOf(mutableSetOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = (currentIndex + 1) / questions.size.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        when (questions[currentIndex]) {

            // Skin type question
            "skinType" -> SkinTypeQuestion(
                onAnswer = {
                    answeredQuestions.add("skinType")
                    if (currentIndex < questions.lastIndex) currentIndex++ else {
                        val allAnswered = answeredQuestions.size == questions.size
                        onFinish(allAnswered)
                    }
                },
                onSkip = {
                    if (currentIndex < questions.lastIndex) currentIndex++ else {
                        onFinish(false)
                    }
                }
            )

            // Sensitive skin question
            "sensitive" -> SimpleQuestion(
                question = "Is your skin sensitive?",
                options = listOf("Yes", "No"),
                onAnswer = {
                    answeredQuestions.add("sensitive")
                    if (currentIndex < questions.lastIndex) currentIndex++ else {
                        val allAnswered = answeredQuestions.size == questions.size
                        onFinish(allAnswered)
                    }
                },
                onBack = { if (currentIndex > 0) currentIndex-- },
                onSkip = {
                    if (currentIndex < questions.lastIndex) currentIndex++ else {
                        onFinish(false)
                    }
                }
            )

            // Allergies question
            "allergies" -> AllergyQuestion(
                onNext = {
                    answeredQuestions.add("allergies")
                    if (currentIndex < questions.lastIndex) currentIndex++ else {
                        val allAnswered = answeredQuestions.size == questions.size
                        onFinish(allAnswered)
                    }
                },
                onBack = { if (currentIndex > 0) currentIndex-- },
                onSkip = {
                    if (currentIndex < questions.lastIndex) currentIndex++ else {
                        onFinish(false)
                    }
                }
            )

            // Age range question (last one)
            "ageRange" -> SimpleQuestion(
                question = "Select your age range",
                options = listOf("<18", "18–25", "26–40", "40+"),
                onAnswer = {
                    answeredQuestions.add("ageRange")
                    val allAnswered = answeredQuestions.size == questions.size
                    onFinish(allAnswered)
                },
                onBack = { if (currentIndex > 0) currentIndex-- },
                onSkip = { onFinish(false) }
            )
        }
    }
}

// Skin type selection screen
@Composable
fun SkinTypeQuestion(
    onAnswer: () -> Unit,
    onSkip: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Select your skin type",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            val skinTypes = listOf(
                "Dry" to R.drawable.cracked,
                "Oily" to R.drawable.leaf_oily,
                "Combination" to R.drawable.combo,
                "Normal" to R.drawable.normal
            )

            for (pair in skinTypes.chunked(2)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for ((label, imageRes) in pair) {
                        ElevatedButton(
                            onClick = onAnswer,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f)
                                .height(140.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = label,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(label)
                            }
                        }
                    }
                }
            }
        }

        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(
                "Skip for now",
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
            )
        }
    }
}

// Simple multiple-choice question
@Composable
fun SimpleQuestion(
    question: String,
    options: List<String>,
    onAnswer: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        TextButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text("⬅ Back", color = MaterialTheme.colorScheme.primary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            options.forEach { option ->
                OutlinedButton(
                    onClick = onAnswer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Text(option)
                }
            }
        }

        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(
                "Skip for now",
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
            )
        }
    }
}

// Allergy question
@Composable
fun AllergyQuestion(
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var customAllergy by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val isValidAllergy = customAllergy.length >= 3 &&
            customAllergy.all { it.isLetter() || it.isWhitespace() }

    Box(modifier = Modifier.fillMaxSize()) {

        TextButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text("⬅ Back", color = MaterialTheme.colorScheme.primary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Any allergies?",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            val options = listOf("Perfume", "Alcohol", "Other")

            options.forEach { option ->
                OutlinedButton(
                    onClick = {
                        selectedOption = option
                        showError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedOption == option)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(option)
                }
            }

            if (selectedOption == "Other") {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = customAllergy,
                    onValueChange = {
                        customAllergy = it
                        showError = false
                    },
                    label = { Text("Please specify your allergy") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isValidAllergy && customAllergy.isNotBlank()
                )

                if (!isValidAllergy && customAllergy.isNotBlank()) {
                    Text(
                        text = "Please enter a valid allergy name (letters only, minimum 3 letters)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedOption == "Other" && !isValidAllergy) {
                        showError = true
                    } else {
                        onNext()
                    }
                },
                enabled = selectedOption != null &&
                        (selectedOption != "Other" || customAllergy.isNotBlank())
            ) {
                Text("Continue")
            }
        }

        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(
                "Skip for now",
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
            )
        }
    }
}
