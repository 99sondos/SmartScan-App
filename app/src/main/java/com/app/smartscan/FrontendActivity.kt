package com.app.smartscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.smartscan.frontendScreens.FrontendNavGraph

// This launches the frontend navigation flow (QuestionnaireScreen to MainScreen), completely separate from backend logic in MainActivity
// My own entry point
class FrontendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontendNavGraph()
        }
    }
}
