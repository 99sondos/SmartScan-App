package com.app.smartscan.frontendScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔹 Rad högst upp med knappar (vänster/höger hörn)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ❤️ Favoritknapp (vänster)
            IconButton(onClick = {
                // TODO: Lägg till funktion för "lägg till i favoriter"
            }) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // 🚫 Blacklistknapp (höger)
            IconButton(onClick = {
                // TODO: Lägg till funktion för "lägg till i blacklist"
            }) {
                Icon(
                    imageVector = Icons.Filled.Block,
                    contentDescription = "Blacklist",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        // 🔹 Mitteninnehåll (för test/design)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Welcome to SmartSkin!",
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "This is where the user can create an account or choose to scan a product.",
                fontSize = 16.sp
            )
        }
    }
}
