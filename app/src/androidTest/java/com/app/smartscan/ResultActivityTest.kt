package com.app.smartscan

import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue

/**
 * UI test class for ResultActivity.
 * Verifies that:
 * 1. The correct analysis type and result are displayed.
 * 2. The Back button closes the activity.
 */
class ResultActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ResultActivity>()

    /**
     * Launches ResultActivity manually with mock data.
     */
    private fun launchWithMockData(): ActivityScenario<ResultActivity> {
        val intent = Intent(
            androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            ResultActivity::class.java
        ).apply {
            putExtra("TYPE", "Skin Analysis")
            putExtra("RESULT", "Your skin is healthy and hydrated!")
        }

        return ActivityScenario.launch(intent)
    }

    /**
     * Test 1:
     * Verifies that mock type and result text appear correctly on the screen.
     */
    @Test
    fun showsCorrectTypeAndResult() {
        val scenario = launchWithMockData()

        composeRule.onNodeWithText("Analysis Type: Skin Analysis").assertExists()
        composeRule.onNodeWithText("Your skin is healthy and hydrated!").assertExists()

        scenario.close()
    }

    /**
     * Test 2:
     * Checks that pressing the Back button closes the activity.
     */
    @Test
    fun backButtonClosesActivity() {
        val scenario = launchWithMockData()

        composeRule.onNodeWithText("Back").performClick()
        composeRule.waitForIdle()

        scenario.onActivity {
            assertTrue("Expected activity to be finishing", it.isFinishing)
        }

        scenario.close()
    }
}
