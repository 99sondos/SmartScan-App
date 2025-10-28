package com.app.smartscan

<<<<<<< HEAD
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.smartscan.aiCamera.AiCameraActivity
import com.app.smartscan.analysis.SkinAnalyzerActivity

/**
 * MainActivity – Entry point of the app.
 * Lets the user choose between different analysis modes:
 * - Skin Analysis
 * - OCR (Text Recognition)
 * - Barcode Scanning
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AnalysisSelectionScreen(onOptionSelected = { type ->
                when (type) {
                    "skin" -> {
                        startActivity(Intent(this, SkinAnalyzerActivity::class.java))
                    }
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
            })
=======
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.smartscan.ui.theme.SmartScanTheme
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
                    FirestoreTestScreen(modifier = Modifier.padding(innerPadding))
                }
            }
>>>>>>> origin/main
        }
    }
}

@Composable
<<<<<<< HEAD
fun AnalysisSelectionScreen(onOptionSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Analysis Mode",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Button(
            onClick = { onOptionSelected("skin") },
            modifier = Modifier.padding(vertical = 8.dp).size(width = 220.dp, height = 50.dp)
        ) {
            Text("Skin Analyzer")
        }

        Button(
            onClick = { onOptionSelected("ocr") },
            modifier = Modifier.padding(vertical = 8.dp).size(width = 220.dp, height = 50.dp)
        ) {
            Text("OCR Text Reader")
        }

        Button(
            onClick = { onOptionSelected("barcode") },
            modifier = Modifier.padding(vertical = 8.dp).size(width = 220.dp, height = 50.dp)
        ) {
            Text("Barcode Scanner")
        }
    }
=======
fun FirestoreTestScreen(modifier: Modifier = Modifier) {
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }
    var result by remember { mutableStateOf("Signing in…") }
    val scope = rememberCoroutineScope()

    // Sign in anonymously once so Firestore rules with request.auth != null will pass
    LaunchedEffect(Unit) {
        try {
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }
            result = "Signed in: ${auth.currentUser?.uid}"
        } catch (e: Exception) {
            result = "Auth error: ${e.message}"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Firestore connectivity test")
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            scope.launch {
                try {
                    val ref = db.collection("_health").document("ping")
                    val ts = System.currentTimeMillis()
                    // write then read back
                    ref.set(mapOf("ts" to ts), SetOptions.merge()).await()
                    val snap = ref.get().await()
                    result = "OK ts=${snap.getLong("ts")}"
                } catch (e: Exception) {
                    result = "Error: ${e.message}"
                }
            }
        }) {
            Text("Test Firestore")
        }
        Spacer(Modifier.height(8.dp))
        Text(result)
    }
}

@Preview(showBackground = true)
@Composable
fun FirestoreTestPreview() {
    SmartScanTheme {
        FirestoreTestScreen()
    }
>>>>>>> origin/main
}
