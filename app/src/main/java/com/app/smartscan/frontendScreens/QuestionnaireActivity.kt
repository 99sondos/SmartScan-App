package com.app.smartscan.frontendScreens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class QuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuestionnaireScreen(onFinish = { })
        }
    }
}
