package com.app.smartscan.frontendScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ViewMyProductsScreen(
    favourites: List<String> = emptyList(),
    blacklist: List<String> = emptyList(),
    onBack: () -> Unit
) {
    var showFavourites by remember { mutableStateOf(false) }
    var showBlacklist by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("My Products", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Favorites
        Button(onClick = { showFavourites = !showFavourites }, modifier = Modifier.fillMaxWidth()) {
            Text(if (showFavourites) "Hide Favourites" else "Show Favourites")
        }

        if (showFavourites) {
            if (favourites.isEmpty()) {
                Text("No favourites saved.")
            } else {
                favourites.forEach { product ->
                    Text("⭐ $product")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Blacklist
        Button(onClick = { showBlacklist = !showBlacklist }, modifier = Modifier.fillMaxWidth()) {
            Text(if (showBlacklist) "Hide Blacklist" else "Show Blacklist")
        }

        if (showBlacklist) {
            if (blacklist.isEmpty()) {
                Text("No blacklisted products.")
            } else {
                blacklist.forEach { product ->
                    Text("⛔ $product")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
