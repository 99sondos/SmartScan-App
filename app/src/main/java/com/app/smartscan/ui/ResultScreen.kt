package com.app.smartscan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.smartscan.data.model.Scan
import com.app.smartscan.ui.auth.AuthViewModel

@Composable
fun ResultScreen(
    scanId: String,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    val scan by authViewModel.observeScan(scanId).collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (scan == null || scan?.explanation == null) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Fetching your personalized analysis...")
        } else {
            val explanation = scan!!.explanation!!
            val suitability = explanation["suitability"] as? String
            val summary = explanation["summary"] as? String
            val reasons = explanation["reasons"] as? List<String>
            val recommendations = explanation["recommendations"] as? List<Map<String, Any>>

            Text("Analysis Complete", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Suitability: $suitability", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Text(summary ?: "No summary available.")
            Spacer(modifier = Modifier.height(16.dp))

            if (!reasons.isNullOrEmpty()) {
                Text("Reasons:", style = MaterialTheme.typography.titleMedium)
                reasons.forEach { reason ->
                    Text("- $reason")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (!recommendations.isNullOrEmpty()) {
                Text("Recommended Alternatives:", style = MaterialTheme.typography.titleMedium)
                recommendations.forEach { rec ->
                    val name = rec["name"] as? String
                    val brand = rec["brand"] as? String
                    Text("- $name by $brand")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                Button(onClick = { 
                    scan?.barcode?.let { authViewModel.onAddToFavoritesClicked(it) }
                    navController.popBackStack()
                }) {
                    Text("Add to Favorites")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { 
                    scan?.barcode?.let { authViewModel.onAddToBlacklistClicked(it) }
                    navController.popBackStack()
                }) {
                    Text("Add to Blacklist")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Done")
            }
        }
    }
}