package com.app.smartscan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
        }
    }
}

@Composable
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
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(width = 220.dp, height = 50.dp)
        ) {
            Text("Skin Analyzer")
        }

        Button(
            onClick = { onOptionSelected("ocr") },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(width = 220.dp, height = 50.dp)
        ) {
            Text("OCR Text Reader")
        }

        Button(
            onClick = { onOptionSelected("barcode") },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(width = 220.dp, height = 50.dp)
        ) {
            Text("Barcode Scanner")
        }
    }
}
