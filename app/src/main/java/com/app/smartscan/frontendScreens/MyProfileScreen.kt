package com.app.smartscan.frontendScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyProfileScreen(
    onScanProduct: () -> Unit,
    onViewMyProducts: () -> Unit,
    onBack: () -> Unit,
    onSignOut: () -> Unit // New parameter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "My Profile")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onScanProduct, modifier = Modifier.fillMaxWidth()) {
            Text("Scan Product")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onViewMyProducts, modifier = Modifier.fillMaxWidth()) {
            Text("View My Products")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes sign out to bottom

        OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
            Text("Sign Out")
        }
    }
}
