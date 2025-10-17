package com.app.smartscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.smartscan.ui.theme.SmartScanTheme
import com.app.smartscan.ocr.runOcrOnTestImage
import com.app.smartscan.ocr.runBarcodeOnTestImage // TODO: change to com.app.smartscan.ocr when merged
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * 🔹 MainActivity – Nora (Temporary Testing Version)
 *
 * This file currently serves as a *test interface* to verify:
 *  - Firestore connection
 *  - OCR (text recognition)
 *  - Barcode scanning
 *
 *  These buttons are only for development testing.
 * Once all modules (camera, AI, UI) are integrated,
 * this logic will be moved into the appropriate screens
 * (e.g. CameraScreen, AnalysisScreen, or ResultView).
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartScanTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // 🔹 Firestore & Authentication setup
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }

    // MutableState for displaying results on screen
    var result by remember { mutableStateOf("App started...") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 TEMPORARY: Anonymous login to Firestore (for testing)
    // TODO: Replace with real user authentication if needed later
    LaunchedEffect(Unit) {
        try {
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }
            result = "Signed in as: ${auth.currentUser?.uid}"
        } catch (e: Exception) {
            result = "Login error: ${e.message}"
        }
    }

    // 🔹 Simple test layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(" SmartScan Test", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        // 🔹 Firestore Connection Test
        // Verifies if the app can read/write to Firebase successfully
        Button(onClick = {
            scope.launch {
                try {
                    val ref = db.collection("_health").document("ping")
                    val ts = System.currentTimeMillis()
                    ref.set(mapOf("ts" to ts), SetOptions.merge()).await()
                    val snap = ref.get().await()
                    result = "Firestore OK: ${snap.getLong("ts")}"
                } catch (e: Exception) {
                    result = "Firestore error: ${e.message}"
                }
            }
        }) {
            Text("Test Firestore")
        }

        Spacer(Modifier.height(12.dp))

        // 🔹 OCR Test Button
        //  Temporary test button for OCR scanning using test image
        // TODO: Replace with runOcrOnImageUri(context, imageUri)
        // once camera input is integrated
        Button(onClick = {
            scope.launch {
                result = "Analyzing image..."
                val textResult = runOcrOnTestImage(context)
                result = "OCR Result:\n$textResult"
            }
        }) {
            Text("Run OCR on Test Image")
        }

        Spacer(Modifier.height(12.dp))

        // 🔹 Barcode Test Button
        //  Temporary test button for barcode scanning using test image
        // TODO: Replace with runBarcodeOnImageUri(context, imageUri)
        // once camera input is integrated
        Button(onClick = {
            scope.launch {
                result = "Scanning barcode..."
                val barcodeResult = runBarcodeOnTestImage(context)
                result = "Barcode Result:\n$barcodeResult"
                // 🔸 TODO (Backend team):
                // When ready, send `barcodeResult` + `ocrText` to Firestore or OpenAI API.

            }
        }) {
            Text("Run Barcode on Test Image")
        }

        Spacer(Modifier.height(20.dp))

        // 🔹 Result display (shows Firestore, OCR, or Barcode output)
        Text(result)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    SmartScanTheme {
        MainScreen()
    }
}

