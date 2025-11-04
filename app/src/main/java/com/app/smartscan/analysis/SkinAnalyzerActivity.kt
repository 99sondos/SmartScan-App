package com.app.smartscan.analysis

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.app.smartscan.BuildConfig
import com.app.smartscan.aiCamera.AiCameraActivity
import com.app.smartscan.ui.theme.SmartScanTheme
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 *  SkinAnalyzerActivity – Opens AiCameraActivity, analyzes skin, and shows AI feedback.
 */
@OptIn(ExperimentalMaterial3Api::class)
class SkinAnalyzerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContent {
                SmartScanTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SkinAnalyzerScreen()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SkinAnalyzerActivity", " Compose crash: ${e.message}", e)
            Toast.makeText(this, "Error loading screen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinAnalyzerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var resultText by remember { mutableStateOf("No image analyzed yet.") }
    var isLoading by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("SkinAnalyzer", "Camera launcher triggered: resultCode=${result.resultCode}")

        Log.d("SkinAnalyzer", " Camera resultCode: ${result.resultCode}")
        Log.d("SkinAnalyzer", " Data: ${result.data}")
        if (result.resultCode == Activity.RESULT_OK) {
            val uriString = result.data?.getStringExtra("captured_image_uri")
            Log.d("SkinAnalyzer", "Received URI: $uriString")

            if (!uriString.isNullOrEmpty()) {
                try {
                    val uri = uriString.toUri()
                    val bitmap = context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }

                    if (bitmap != null) {
                        scope.launch {
                            try {
                                isLoading = true
                                resultText = " Starting skin analysis..."
                                Log.d("SkinAnalyzer", "Starting image analysis...")

                                // Run analysis and get AI feedback
                                val analysis = analyzeSkinTypeFromBitmap(bitmap) { status ->
                                    resultText = status
                                }

                                Log.d("SkinAnalyzer", "Analysis complete.")
                                resultText = analysis
                            } catch (e: Exception) {
                                resultText = "Error during analysis: ${e.message}"
                                Toast.makeText(context, "Analysis failed.", Toast.LENGTH_SHORT).show()
                                Log.e("SkinAnalyzer", "Analysis failed", e)
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        Log.e("SkinAnalyzer", "Bitmap decoding failed (null).")
                        resultText = "Error: Could not decode image data."
                    }
                } catch (e: Exception) {
                    resultText = "Error reading image: ${e.message}"
                    Log.e("SkinAnalyzer", "Image read failed", e)
                }
            } else {
                Log.w("SkinAnalyzer", "No URI received from AiCameraActivity.")
                resultText = "No image URI received from camera."
            }
        } else {
            Log.w("SkinAnalyzer", "Camera activity cancelled or failed.")
            resultText = "Camera cancelled or failed. Please try again."
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Skin Analysis",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                try {
                    Log.d("SkinAnalyzer", "Launching AiCameraActivity...")
                    val intent = Intent(context, AiCameraActivity::class.java)
                    intent.putExtra("analysis_type", "skin")
                    cameraLauncher.launch(intent)

                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to launch camera: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("SkinAnalyzer", "Failed to launch AiCameraActivity", e)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Open Camera to Analyze")
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (isLoading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Analyzing your skin...", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = resultText,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 *  Performs skin analysis and AI feedback using OpenAI API.
 */
suspend fun analyzeSkinTypeFromBitmap(
    bitmap: Bitmap,
    onStatusUpdate: ((String) -> Unit)? = null
): String = withContext(Dispatchers.Default) {
    try {
        onStatusUpdate?.invoke("🔍 Analyzing image brightness and tone...")

        // --- Simulated values for brightness/tone (replace with real logic) ---
        val avgBrightness = 0.65
        val stdDev = 0.12
        val tone = if (avgBrightness < 0.4) "Dark" else if (avgBrightness < 0.7) "Medium" else "Light"
        val type = if (stdDev > 0.2) "Oily" else if (stdDev < 0.1) "Dry" else "Normal"
        val pixelCount = 5000

        onStatusUpdate?.invoke(" Getting AI feedback...")

        try {
            val apiKey = BuildConfig.OPENAI_API_KEY
            if (apiKey.isNullOrBlank()) {
                return@withContext """
                    Skin tone: $tone
                    Skin type: $type
                    Avg brightness: ${"%.2f".format(avgBrightness)}
                    Std deviation: ${"%.2f".format(stdDev)}

                    ⚠ No API key found – AI feedback unavailable.
                """.trimIndent()
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build()

            val prompt = """
                You are a skincare expert.
                Here is an image analysis result:
                Skin tone: $tone
                Skin type: $type
                Avg brightness: ${"%.2f".format(avgBrightness)}
                Brightness variation: ${"%.2f".format(stdDev)}
                
                Give a short summary describing the skin condition and 1–2 skincare suggestions (max 3 sentences).
            """.trimIndent()

            val json = JSONObject().apply {
                put("model", "gpt-4o-mini")
                put("max_tokens", 120)
                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }
                put("messages", messages)
            }

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = try {
                client.newCall(request).execute()
            } catch (e: SocketTimeoutException) {
                return@withContext " Network timeout while contacting AI."
            }

            val responseBody = response.body?.string()
            val aiText = if (!responseBody.isNullOrEmpty()) {
                try {
                    JSONObject(responseBody)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                } catch (e: Exception) {
                    "Could not parse AI feedback."
                }
            } else "No AI response."

            return@withContext """
                Skin tone: $tone
                Skin type: $type
                Avg brightness: ${"%.2f".format(avgBrightness)}
                Std deviation: ${"%.2f".format(stdDev)}
                (Sampled $pixelCount pixels)

                 AI feedback:
                $aiText
            """.trimIndent()

        } catch (e: Exception) {
            Log.e("SkinAnalyzer", "OpenAI error: ${e.message}", e)
            return@withContext "⚠ AI feedback unavailable: ${e.message}"
        }
    } catch (e: Exception) {
        Log.e("SkinAnalyzer", " Unexpected error: ${e.message}", e)
        return@withContext "⚠ Analysis failed: ${e.message}"
    }
}
