package com.app.smartscan.frontendScreens

import androidx.compose.foundation.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.smartscan.R

@Composable
fun QuestionnaireScreen(onFinish: (Boolean) -> Unit) {
    var currentIndex by remember { mutableStateOf(0) }
    val totalQuestions = 4

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9)) // samma rena bakgrund som HomeScreen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            when (currentIndex) {
                0 -> SkinTypeQuestion(
                    onAnswer = { currentIndex++ },
                    onSkip = { currentIndex++ }
                )

                1 -> GridQuestion(
                    question = "Is your skin sensitive?",
                    options = listOf("Yes", "No"),
                    onAnswer = { currentIndex++ },
                    onBack = { currentIndex-- },
                    onSkip = { currentIndex++ }
                )

                2 -> GridQuestion(
                    question = "Do you have any allergies?",
                    options = listOf("Perfume", "Alcohol", "Other", "None"),
                    onAnswer = { currentIndex++ },
                    onBack = { currentIndex-- },
                    onSkip = { currentIndex++ }
                )

                3 -> GridQuestion(
                    question = "Select your age range",
                    options = listOf("<18", "18–25", "26–40", "40+"),
                    onAnswer = { onFinish(true) },
                    onBack = { currentIndex-- },
                    onSkip = { onFinish(false) }
                )
            }
        }

        // 🔹 Prickar längst ner för progress
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            repeat(totalQuestions) { index ->
                val isActive = index == currentIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(if (isActive) 12.dp else 8.dp)
                        .background(
                            color = if (isActive) Color.Black else Color.LightGray,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

// 🔹 Fråga 1 – Skin type
@Composable
fun SkinTypeQuestion(onAnswer: () -> Unit, onSkip: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select your skin type",
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        val skinTypes = listOf(
            "Dry" to R.drawable.ic_launcher_foreground,
            "Oily" to R.drawable.ic_launcher_foreground,
            "Combination" to R.drawable.ic_launcher_foreground,
            "Normal" to R.drawable.ic_launcher_foreground
        )

        var selected by remember { mutableStateOf<String?>(null) }

        for (pair in skinTypes.chunked(2)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for ((label, imageRes) in pair) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .aspectRatio(1f)
                            .background(
                                color = if (selected == label)
                                    Color(0xFFEAEAEA) else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selected == label) Color.Black else Color(0xFFDADADA),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selected = label
                                onAnswer()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = label,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = label,
                                color = Color.Black,
                                fontWeight = if (selected == label) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        TextButton(onClick = onSkip, modifier = Modifier.padding(top = 20.dp)) {
            Text("Skip for now", color = Color.Gray)
        }
    }
}

// 🔹 Övriga frågor i samma gridlayout
@Composable
fun GridQuestion(
    question: String,
    options: List<String>,
    onAnswer: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    var selected by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = question,
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Visa i grid-format (två kolumner)
        for (pair in options.chunked(2)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (option in pair) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .aspectRatio(1f)
                            .background(
                                color = if (selected == option)
                                    Color(0xFFEAEAEA) else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selected == option) Color.Black else Color(0xFFDADADA),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selected = option
                                onAnswer()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = if (selected == option) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back och Skip-knappar
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            TextButton(onClick = onBack) {
                Text("⬅ Back", color = Color.Gray)
            }
            TextButton(onClick = onSkip) {
                Text("Skip", color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuestionnairePreview() {
    QuestionnaireScreen(onFinish = {})
}
