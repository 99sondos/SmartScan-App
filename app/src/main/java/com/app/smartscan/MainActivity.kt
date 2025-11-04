package com.app.smartscan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.smartscan.frontendScreens.FrontendNavGraph
import com.app.smartscan.ui.theme.SmartScanTheme
import com.app.smartscan.analysis.SkinAnalyzerActivity
import com.app.smartscan.aiCamera.AiCameraActivity

/**
 * MainActivity – Entry point of the app.
 * Starts the main frontend navigation flow.
 * Can later be expanded to include AI analysis and scanning.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // (Optional) Connect Firebase to emulator if needed for dev
        if (BuildConfig.USE_EMULATORS) {
            // FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
            // FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
            // FirebaseFunctions.getInstance().useEmulator("10.0.2.2", 5001)
        }

        setContent {
            SmartScanTheme {
                // 🔹 Start the full frontend navigation (questionnaire, profile, etc.)
                FrontendNavGraph()
            }
        }
    }

    /**
     * 🔹 Optionally used later to open AI analysis screens
     */
    private fun openAnalysisScreen(type: String) {
        when (type) {
            "skin" -> startActivity(Intent(this, SkinAnalyzerActivity::class.java))
            "ocr" -> {
                val intent = Intent(this, AiCameraActivity::class.java)
                intent.putExtra("analysis_type", "ocr")
                startActivity(intent)
            }
            "barcode" -> {
                val intent = Intent(this, AiCameraActivity::class.java)
                intent.putExtra("analysis_type", "barcode")
                startActivity(intent)
            }
        }
    }
}
