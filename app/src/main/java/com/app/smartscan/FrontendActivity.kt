package com.app.smartscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.app.smartscan.frontendScreens.FrontendNavGraph
import com.app.smartscan.ui.theme.SmartScanTheme

// My own entry point for the frontend navigation flow
// Launches everything from the Questionnaire → Home → Create Account, etc.
class FrontendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartScanTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    FrontendNavGraph()
                }
            }
        }
    }
}