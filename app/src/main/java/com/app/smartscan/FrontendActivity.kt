package com.app.smartscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import com.app.smartscan.frontendScreens.FrontendNavGraph
import com.app.smartscan.ui.theme.SmartScanTheme

class FrontendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartScanTheme {
                // We need a Scaffold to host the Snackbar
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { paddingValues ->
                    // The paddingValues are important to prevent UI from hiding behind system bars
                    Surface(color = MaterialTheme.colorScheme.background) {
                        FrontendNavGraph(
                            snackbarHostState = snackbarHostState, // Pass the state down
                            paddingValues = paddingValues
                        )
                    }
                }
            }
        }
    }
}