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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }
    var result by remember { mutableStateOf("App started...") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 Anonymous login (required for Firestore testing)
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🧠 SmartScan Test", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        // 🔹 Button to test Firestore
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

        // 🔹 Button to test OCR
        Button(onClick = {
            scope.launch {
                result = "Analyzing image..."
                val textResult = runOcrOnTestImage(context)
                result = "OCR Result:\n$textResult"
            }
        }) {
            Text("Run OCR on Test Image")
        }

        Spacer(Modifier.height(20.dp))
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

