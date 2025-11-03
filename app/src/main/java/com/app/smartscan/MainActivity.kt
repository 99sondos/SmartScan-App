package com.app.smartscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.smartscan.frontendScreens.FrontendNavGraph
import com.app.smartscan.ui.theme.SmartScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartScanTheme {
                // 🔹 Kör hela navigationsflödet
                FrontendNavGraph()
            }
        }
    }
}
