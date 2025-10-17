package com.app.smartscan.ocr   // <--- ditt paketnamn här

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await
import com.app.smartscan.R

/**
 * 🔹 Barcode Helper – Nora
 *
 * This file handles barcode scanning using Google ML Kit.
 * For now, it runs on a static test image from `drawable` (test_label1),
 * but will later be connected to CameraX for real product scanning.
 *
 * Related to OCR functions in the same module (`ocr/` folder).
 */

/**
 * Runs barcode scanning on a test image stored in drawable.
 *  Used for *local testing only* until CameraX integration is ready.
 *
 * TODO: Replace `R.drawable.test_label1` with live camera input
 * once the camera functionality is implemented by the frontend team.
 */
suspend fun runBarcodeOnTestImage(context: Context): String {
    return try {
        val scanner = BarcodeScanning.getClient()

        // ⚠️ TEMPORARY: Uses a static image for development testing
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test_label1)
        val image = InputImage.fromBitmap(bitmap, 0)

        val barcodes = scanner.process(image).await()

        // ✅ Return the first barcode found
        if (barcodes.isNotEmpty()) {
            val code = barcodes[0].rawValue ?: "No value found"
            Log.d("BARCODE", "Found code: $code")
            code
        } else {
            "No barcode detected in the image."
        }

    } catch (e: Exception) {
        Log.e("BARCODE", "Error during barcode scanning", e)
        "Error: ${e.message}"
    }
}

/**
 * Runs barcode scanning on an image selected or taken by the user.
 *
 *  This version will be used later when the app passes in a real `imageUri`
 * from the CameraX or gallery input.
 *
 * @param context Context from the activity or composable
 * @param imageUri The URI of the image to analyze
 * @return Detected barcode string or error message
 *
 * TODO: Integrate this with Firestore and OpenAI once the product lookup flow is finished.
 */
suspend fun runBarcodeOnImageUri(context: Context, imageUri: Uri): String {
    return try {
        val scanner = BarcodeScanning.getClient()

        // 🔄 Converts selected or captured image to ML Kit's InputImage
        val image = InputImage.fromFilePath(context, imageUri)
        val barcodes = scanner.process(image).await()

        if (barcodes.isNotEmpty()) {
            val code = barcodes[0].rawValue ?: "No value found"
            Log.d("BARCODE", "Found code: $code")
            code
        } else {
            "No barcode detected in the image."
        }

    } catch (e: Exception) {
        Log.e("BARCODE", "Error during barcode scanning", e)
        "Error: ${e.message}"
    }
}
