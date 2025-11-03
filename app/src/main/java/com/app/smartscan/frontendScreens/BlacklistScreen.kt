package com.app.smartscan.frontendScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BlacklistScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(24.dp)
    ) {
        // 🔹 Back button top-left
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text("⬅ Back", color = Color.Gray)
        }

        // 🔹 Title centered at the top
        Text(
            text = "Blacklist",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        )

        // 🔹 Placeholder text centered
        Text(
            text = "Products you want to avoid will appear here.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
