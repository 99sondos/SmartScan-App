package com.app.smartscan.frontendScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.smartscan.R

@Composable
fun HomeScreen(
    questionnaireCompleted: Boolean,
    accountCreated: Boolean,
    onCreateAccount: () -> Unit,
    onProfile: () -> Unit,
    onScanProduct: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "SmartSkin Logo",
            modifier = Modifier
                .size(130.dp)
                .padding(top = 32.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SmartSkin",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Discover your perfect skincare match.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        // 🔹 Buttons section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 🔹 Dynamisk knapp
            if (accountCreated) {
                Button(
                    onClick = onProfile,
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAEAEA)),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp)
                ) {
                    Text("My Profile", color = Color.Black, fontSize = 16.sp)
                }
            } else {
                Button(
                    onClick = onCreateAccount,
                    enabled = questionnaireCompleted,
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (questionnaireCompleted)
                            Color(0xFFEAEAEA) else Color(0xFFF2F2F2)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp)
                ) {
                    Text("Create Account", color = Color.Black, fontSize = 16.sp)
                }

                if (!questionnaireCompleted) {
                    Text(
                        text = "Complete the questionnaire to create an account.",
                        color = Color(0xFFCC5C5C),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // 🔹 Scan Product button
            OutlinedButton(
                onClick = onScanProduct,
                shape = RoundedCornerShape(40.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(55.dp)
            ) {
                Text("Scan Product", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }

        Text(
            text = "Your personal skincare assistant\nScan, explore and find what’s best for your skin.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }
}
