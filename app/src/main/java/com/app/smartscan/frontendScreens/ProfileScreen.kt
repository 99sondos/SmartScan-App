package com.app.smartscan.frontendScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    userName: String,
    onBack: () -> Unit,
    onFavorites: () -> Unit,
    onBlacklist: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(24.dp)
    ) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Text("⬅ Back", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Welcome, $userName!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onFavorites,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAEAEA)),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("View Favorites", color = Color.Black, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBlacklist,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAEAEA)),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("View Blacklist", color = Color.Black, fontSize = 16.sp)
        }
    }
}
