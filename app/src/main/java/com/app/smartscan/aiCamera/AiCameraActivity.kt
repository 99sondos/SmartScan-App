package com.app.smartscan.aiCamera

import android.content.Context
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import android.view.WindowManager
import android.view.Surface

class AiCameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request camera permission at runtime
        if (checkSelfPermission(android.Manifest.permission.CAMERA) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 0)
        }

        // Launch the Compose camera screen
        setContent {
            CameraPreviewScreen()
        }
    }
}

@Composable
fun CameraPreviewScreen() {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    // Get current screen rotation to correctly orient saved image
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
    val rotation = windowManager.defaultDisplay?.rotation ?: Surface.ROTATION_0

    // Build ImageCapture with correct rotation
    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetRotation(rotation)
            .build()
    }

    // Holds the captured or selected image. When not null, preview UI will be shown.
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Gallery picker to load an image from storage
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            capturedBitmap = bitmap // Switch to preview screen

            Toast.makeText(context, "Gallery image loaded", Toast.LENGTH_SHORT).show()
            Log.d("AiCamera", "GALLERY BITMAP READY – send to OCR next")
        }
    }

    // Initialize CameraX preview
    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
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
        }, ContextCompat.getMainExecutor(context))
    }

    // If no image is captured, show camera UI. Else show preview screen.
    if (capturedBitmap == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Live camera view
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { previewView }
            )

            // Capture button
            Button(
                onClick = {
                    captureImage(imageCapture, context) { bitmap ->
                        capturedBitmap = bitmap // Switch to preview
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Text("Capture")
            }

            // Gallery button
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 32.dp, bottom = 32.dp)
            ) {
                Text("Gallery")
            }
        }
    } else {
        // Preview screen after an image is captured or selected
        Box(modifier = Modifier.fillMaxSize()) {

            // Show the selected or captured image fullscreen
            Image(
                bitmap = capturedBitmap!!.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Use button - this is where OCR will be connected
            Button(
                onClick = {
                    Toast.makeText(context, "Sending to OCR", Toast.LENGTH_SHORT).show()
                    Log.d("AiCamera", "USER CONFIRMED IMAGE – ready for OCR next")
                    // TODO: OCR Module example -> OcrHelper.processImage(capturedBitmap)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 32.dp, bottom = 32.dp)
            ) {
                Text("Use")
            }

            // Retake button - resets back to camera
            Button(
                onClick = {
                    capturedBitmap = null
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 32.dp, bottom = 32.dp)
            ) {
                Text("Retake")
            }
        }
    }
}

// Handles image capture with callback to return the Bitmap to Compose
fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    onBitmapCaptured: (Bitmap) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        "ai_capture_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                Log.e("AiCamera", "Capture failed: ${exc.message}")
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                // Decode file into Bitmap
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                // Return bitmap to Composable via callback
                (context as ComponentActivity).runOnUiThread {
                    onBitmapCaptured(bitmap)
                }

                Toast.makeText(context, "Image captured", Toast.LENGTH_SHORT).show()
                Log.d("AiCamera", "BITMAP READY – triggering preview")
            }
        }
    )
}






