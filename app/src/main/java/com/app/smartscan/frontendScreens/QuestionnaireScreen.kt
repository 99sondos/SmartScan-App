package com.app.smartscan.frontendScreens

// --- Import all necessary Compose and Android libraries ---
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
    onFinish: () -> Unit   // Called when user finishes or skips the last question
) {
    // List of all the questions in order
    val questions = listOf("skinType", "sensitive", "allergies", "ageRange")

    // Keeps track of which question we're currently on
    var currentIndex by remember { mutableStateOf(0) }

    // The main layout of the questionnaire
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress bar at the top
        LinearProgressIndicator(
            progress = (currentIndex + 1) / questions.size.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // Show different question screens depending on where we are
        when (questions[currentIndex]) {
            // Skin type question
            "skinType" -> SkinTypeQuestion(
                onAnswer = { currentIndex++ }, // Go to next question when answered
                onSkip = { currentIndex++ }    // Skip also goes forward
            )

            // Sensitive skin question
            "sensitive" -> SimpleQuestion(
                question = "Is your skin sensitive?",
                options = listOf("Yes", "No"),
                onAnswer = { currentIndex++ },
                onBack = { if (currentIndex > 0) currentIndex-- },
                onSkip = { currentIndex++ }
            )

            // Allergies question (with text input for "Other")
            "allergies" -> AllergyQuestion(
                onNext = { currentIndex++ },
                onBack = { if (currentIndex > 0) currentIndex-- },
                onSkip = { currentIndex++ }
            )

            // Age range question (last one)
            "ageRange" -> SimpleQuestion(
                question = "Select your age range",
                options = listOf("<18", "18–25", "26–40", "40+"),
                onAnswer = { onFinish() },
                onBack = { if (currentIndex > 0) currentIndex-- },
                onSkip = { onFinish() }
            )
        }
    }
}

// Skin type selection screen (first question)
@Composable
fun SkinTypeQuestion(
    onAnswer: () -> Unit,
    onSkip: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Centered content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Select your skin type",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // List of skin types (placeholder images for now)
            val skinTypes = listOf(
                "Dry" to R.drawable.ic_launcher_foreground,
                "Oily" to R.drawable.ic_launcher_foreground,
                "Combination" to R.drawable.ic_launcher_foreground,
                "Normal" to R.drawable.ic_launcher_foreground
            )

            // Display 2 buttons per row
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

        // Skip button
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

// Simple multiple-choice question (used for most screens)
@Composable
fun SimpleQuestion(
    question: String,
    options: List<String>,
    onAnswer: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Back button
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text("⬅ Back", color = MaterialTheme.colorScheme.primary)
        }

        // Main question + options
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

        // Skip button
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

// Allergy question (shows text field if "Other" selected)
@Composable
fun AllergyQuestion(
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    // Remember which option was selected + the custom input
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var customAllergy by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        // Back button
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text("⬅ Back", color = MaterialTheme.colorScheme.primary)
        }

        // Main question and options
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Any allergies?",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // List of choices
            val options = listOf("Perfume", "Alcohol", "Other")

            options.forEach { option ->
                OutlinedButton(
                    onClick = { selectedOption = option },
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

            // If "Other" is selected, show a text field
            if (selectedOption == "Other") {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = customAllergy,
                    onValueChange = { customAllergy = it },
                    label = { Text("Please specify your allergy") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button — only active when user selected something valid
            Button(
                onClick = { onNext() },
                enabled = selectedOption != null &&
                        (selectedOption != "Other" || customAllergy.isNotBlank())
            ) {
                Text("Continue")
            }
        }

        // Skip button (bottom-right)
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
