package com.app.smartscan.frontendScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.smartscan.R
import androidx.compose.ui.tooling.preview.Preview
@Composable
fun HomeScreen(
    questionnaireCompleted: Boolean,
    cameFromAnalyzer: Boolean,      //  NY parameter
    onCreateAccount: () -> Unit,
    onScanProduct: () -> Unit,
    onGoToProfile: () -> Unit       // DU HAR DET I NAVGRAPH SEN
) {
    // Simulated value (for now). Later, this should come from ViewModel or SharedPrefs.
    // val questionnaireCompleted = remember { mutableStateOf(false) }

    // Whole screen layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- App logo section ---
        Image(
            painter = painterResource(id = R.drawable.smartskin_logo), // temporary logo
            contentDescription = "SmartSkin Logo",
            modifier = Modifier
                .size(255.dp)
                .padding(top = 32.dp)
        )

        // --- Main buttons ---
// --- Main buttons ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // "Create Account" button (disabled until questionnaire is done)
            Button(
                onClick = onCreateAccount,
                enabled = questionnaireCompleted || cameFromAnalyzer,   // ✅ ändrat här
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (questionnaireCompleted || cameFromAnalyzer)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(64.dp)
            ) {
                Text(
                    "Create Account",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }


            // Message if questionnaire not completed
            if (!questionnaireCompleted && !cameFromAnalyzer) {
                Text(
                    text = "Complete the questionnaire to create an account.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }


            // "Scan Product" button (always enabled)
            OutlinedButton(
                onClick = onScanProduct,
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(64.dp)
            ) {
                Text(
                    "Scan Product",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // --- App tagline text at bottom ---
        Spacer(modifier = Modifier.height(5.dp)) // moves it up a bit

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
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onCreateAccount = {},
            questionnaireCompleted = false, // change to true
            cameFromAnalyzer = false,
            onScanProduct = {},
            onGoToProfile = {}
        )
    }
}
