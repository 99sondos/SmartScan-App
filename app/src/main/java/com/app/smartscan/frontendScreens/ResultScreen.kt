package com.app.smartscan.frontendScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.app.smartscan.R

// import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavController

@Composable
fun ResultScreen(
    navController: NavController,
    imageUri: String?,
    onBack: () -> Unit
){
//fun ResultScreen(
//    imageUri: String?,
//    onSaveFavorite: () -> Unit,
//    onSaveBlacklist: () -> Unit,
//    onBack: () -> Unit
//) {
    var productName by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var compatibilitySummary by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("⬅ Back", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.smartskin_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(140.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Favorite / Blacklist buttons
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.navigate("blacklist") }) {
                Icon(painter = painterResource(R.drawable.ic_broken_heart), contentDescription = "Blacklist", tint = Color.Red)
            }

            IconButton(onClick = { navController.navigate("favorites") }) {
                Icon(painter = painterResource(R.drawable.ic_heart), contentDescription = "Favorite", tint = Color.Red)
            }


        // Product image if one exists
        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Product Image",
                modifier = Modifier.size(150.dp)
            )
        }

        // Product / Type fields
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        OutlinedTextField(
            value = productType,
            onValueChange = { productType = it },
            label = { Text("Type") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        // Skin Compatibility Summary
        OutlinedTextField(
            value = compatibilitySummary,
            onValueChange = { compatibilitySummary = it },
            label = { Text("Skin Compatibility") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(top = 12.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "⚠️ AI-generated result — may contain errors.\nConsult a professional if unsure.",
            color = Color.Red,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
}




