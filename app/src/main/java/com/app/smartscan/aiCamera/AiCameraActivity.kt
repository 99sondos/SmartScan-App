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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.io.File
import com.app.smartscan.aiCamera.rotateBitmapIfNeeded



class AiCameraActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request camera permission at runtime.
        // CameraX requires explicit camera permission to function.
        if (checkSelfPermission(android.Manifest.permission.CAMERA) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 0)
        }

        // Set the Compose content and launch the camera preview screen.
        setContent {
            CameraPreviewScreen()
        }
    }
}

@Composable
fun CameraPreviewScreen() {
    val context = LocalContext.current

    // PreviewView is the actual camera feed surface managed by CameraX (rendered inside Compose using AndroidView)
    val previewView = remember { PreviewView(context) }

    // ImageCapture is responsible for capturing high-resolution still images (separate pipeline from Preview)
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Holds the captured or gallery-selected bitmap.
    // When this is non-null, the screen switches from live camera to preview mode.
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Launcher to pick an existing image from gallery.
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Convert gallery image Uri into a Bitmap.
            // No EXIF correction applied here — relies on gallery apps usually returning upright bitmaps.
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            capturedBitmap = bitmap

            Toast.makeText(context, "Gallery image loaded", Toast.LENGTH_SHORT).show()
            Log.d("AiCamera", "GALLERY BITMAP READY – send to OCR next")
        }
    }

    // Initialize CameraX only once using LaunchedEffect.
    // Binds Preview and ImageCapture use cases to lifecycle.
    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up Preview pipeline and connect it to PreviewView surface provider.
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Select the back camera.
            val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()

            // Bind both preview and image capture to the lifecycle, enabling live camera and capture functionality.
            cameraProvider.bindToLifecycle(
                context as ComponentActivity,
                cameraSelector,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(context))
    }

    // UI logic: if no image is captured yet, show live camera.
    // If an image is present, show preview mode with Retake/Use options.

    if (capturedBitmap == null) {
        //  CAMERA MODE (Live Preview + Icon Buttons)
        Box(modifier = Modifier.fillMaxSize()) {

            //  Live camera feed using CameraX PreviewView
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { previewView }
            )

            //  Capture button (round, transparent, centered)
            Box(
                modifier = Modifier
                    .size(80.dp) // Button size
                    .align(Alignment.BottomCenter) // Position at bottom center
                    .padding(bottom = 32.dp)
                    .clip(CircleShape) // Make it round
                    .background(Color.DarkGray.copy(alpha = 0.2f)) // Slight transparent BG
                    .border(3.dp, Color.White, CircleShape) // White circular border
                    .clickable {
                        // When clicked → trigger capture image
                        captureImage(imageCapture, context) { bitmap ->
                            capturedBitmap = bitmap
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Camera, // Camera icon
                    contentDescription = "Capture",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(36.dp)
                )
            }

            //  Gallery icon button (bottom-left corner)
            Icon(
                imageVector = Icons.Default.Image, // Gallery icon
                contentDescription = "Open Gallery",
                tint = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.BottomStart) // Position bottom-left
                    .padding(start = 32.dp, bottom = 32.dp)
                    .size(32.dp)
                    .clickable { galleryLauncher.launch("image/*") } // Open gallery
            )
        }
    } else {
        //  PREVIEW MODE (User selected or captured image)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black) // Add black background to avoid white screen
        ) {

            //  Display the image fullscreen
            Image(
                bitmap = capturedBitmap!!.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop

            )

            //  Retake icon (refresh symbol) - bottom-left
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retake",
                tint = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 32.dp, bottom = 32.dp)
                    .size(40.dp)
                    .clickable {
                        capturedBitmap = null // Return to camera mode
                    }
            )

            //  Use icon (checkmark symbol) - bottom-right
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Use Image",
                tint = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 32.dp, bottom = 32.dp)
                    .size(40.dp)
                    .clickable {
                        Toast.makeText(context, "Sending to OCR", Toast.LENGTH_SHORT).show()
                        Log.d("AiCamera", "USER CONFIRMED IMAGE – ready for OCR next")
                        // TODO: Send capturedBitmap to OCR handler
                    }
            )
        }
    }


}

    // Handles still image capture and returns a physically correct rotated Bitmap.
fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    onBitmapCaptured: (Bitmap) -> Unit
) {
    // Create a temporary file to store the captured JPEG image.
    val photoFile = File(
        context.cacheDir,
        "ai_capture_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // Trigger ImageCapture — this runs on a separate pipeline from the Preview.
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {

            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                Log.e("AiCamera", "Capture failed: ${exc.message}")
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                // Load raw pixel data from the JPEG file — EXIF is ignored here.
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                // Read EXIF orientation to know how the image SHOULD be rotated.
                val exif = ExifInterface(photoFile.absolutePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                val rotatedBitmap = rotateBitmapIfNeeded(bitmap, orientation)

                // Return the rotated bitmap back to Compose on main thread.
                (context as ComponentActivity).runOnUiThread {
                    onBitmapCaptured(rotatedBitmap)
                }

                Toast.makeText(context, "Image captured", Toast.LENGTH_SHORT).show()
                Log.d("AiCamera", "BITMAP READY – triggering preview")
            }
        }
    )
}








