package com.app.smartscan.aiCamera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.smartscan.ResultActivity
import com.app.smartscan.analysis.analyzeSkinTypeFromBitmap
import com.app.smartscan.ocr.BarCodeHelper
import com.app.smartscan.ocr.OcrHelper
import com.app.smartscan.ocr.formatIngredientsSmart
import com.app.smartscan.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.File

class AiCameraActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkSelfPermission(android.Manifest.permission.CAMERA)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 0)
        }

        val analysisType = intent.getStringExtra("analysis_type") ?: "ocr"
        Log.d("AiCamera", "Started with analysis type: $analysisType")

        setContent {
            CameraPreviewScreen(analysisType = analysisType)
        }
    }
}

@Composable
fun CameraPreviewScreen(analysisType: String) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)

    val coroutineScope = rememberCoroutineScope()

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                capturedBitmap = bitmap
                Toast.makeText(context, "Gallery image loaded", Toast.LENGTH_SHORT).show()
                Log.d("AiCamera", "Gallery image loaded successfully")
            } catch (e: Exception) {
                Log.e("AiCamera", "Gallery load failed: ${e.message}", e)
            }
        }
    }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as ComponentActivity,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                Log.d("AiCamera", "Camera initialized successfully")
            } catch (e: Exception) {
                Log.e("AiCamera", "Camera initialization failed: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    if (capturedBitmap == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { previewView }
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray.copy(alpha = 0.2f))
                    .border(3.dp, Color.White, CircleShape)
                    .clickable {
                        captureImage(imageCapture, context) { bitmap ->
                            capturedBitmap = bitmap
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Capture",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(36.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Open Gallery",
                tint = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 32.dp, bottom = 32.dp)
                    .size(32.dp)
                    .clickable { galleryLauncher.launch("image/*") }
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Image(
                bitmap = capturedBitmap!!.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retake",
                tint = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 32.dp, bottom = 32.dp)
                    .size(40.dp)
                    .clickable { capturedBitmap = null }
            )

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Use Image",
                tint = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 32.dp, bottom = 32.dp)
                    .size(40.dp)
                    .clickable {
                        val bitmap = capturedBitmap ?: return@clickable
                        coroutineScope.launch {
                            try {
                                Log.d("AiCamera", "Running analysis: $analysisType")

                                val resultText = when (analysisType) {
                                    "ocr" -> {
                                        val ocr = OcrHelper(context)
                                        val rawText = ocr.analyze(bitmap)
                                        formatIngredientsSmart(rawText)
                                    }

                                    "barcode" -> {
                                        val barcodeHelper = BarCodeHelper(context)
                                        barcodeHelper.decode(bitmap) ?: ""
                                    }

                                    "skin" -> analyzeSkinTypeFromBitmap(bitmap)

                                    else -> "Unknown analysis type"
                                }

                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                if (uid != null) {
                                    when (analysisType) {
                                        "ocr" -> viewModel.saveOcrScan(resultText)
                                        "skin" -> viewModel.saveSkinScan(resultText)
                                        "barcode" -> viewModel.saveBarcodeScan(resultText)
                                    }
                                } else {
                                    Log.e("AiCamera", "User not logged in — cannot save scan")
                                }

                                val intent = Intent(context, ResultActivity::class.java).apply {
                                    putExtra("TYPE", analysisType.uppercase())
                                    putExtra("RESULT", resultText)
                                }
                                context.startActivity(intent)
                                (context as ComponentActivity).finish()

                            } catch (e: Exception) {
                                Log.e("AiCamera", "Analysis error: ${e.message}", e)
                                Toast.makeText(context, "Analysis failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            )
        }
    }
}

fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    onBitmapCaptured: (Bitmap) -> Unit
) {
    val photoFile = File(context.cacheDir, "ai_capture_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {

            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                Log.e("AiCamera", "Capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                try {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    val exif = ExifInterface(photoFile.absolutePath)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )

                    val matrix = android.graphics.Matrix()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    }

                    val rotatedBitmap = Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                    )

                    (context as ComponentActivity).runOnUiThread {
                        onBitmapCaptured(rotatedBitmap)
                    }

                    Toast.makeText(context, "Image captured", Toast.LENGTH_SHORT).show()
                    Log.d("AiCamera", "Image captured and rotated correctly")
                } catch (e: Exception) {
                    Log.e("AiCamera", "onImageSaved failed: ${e.message}", e)
                }
            }
        }
    )
}
