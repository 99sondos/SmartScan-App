package com.app.smartscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.smartscan.frontendScreens.FrontendNavGraph
import com.app.smartscan.ui.theme.SmartScanTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Logga in anonymt om ingen användare redan finns
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }

        // ✅ Starta Compose Navigation (inkl. Splash → Welcome)
        setContent {
            SmartScanTheme {
                FrontendNavGraph()   // <-- Den här nav graphen MÅSTE ha splash som startDestination
            }
        }
    }
}
