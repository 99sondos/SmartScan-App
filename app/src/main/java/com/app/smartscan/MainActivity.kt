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
        }
    }
}

@Composable
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
}
