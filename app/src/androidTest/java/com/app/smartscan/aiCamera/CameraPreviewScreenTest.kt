package com.app.smartscan.aiCamera

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class CameraPreviewScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun captureButton_is_visible_on_start() {
        // Startar composable
        composeRule.setContent {
            CameraPreviewScreen()
        }

        // Verifierar att "Capture"-knappen visas
        composeRule
            .onNodeWithContentDescription("Capture")
            .assertIsDisplayed()
    }
}