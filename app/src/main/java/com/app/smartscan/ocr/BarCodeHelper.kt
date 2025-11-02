package com.app.smartscan.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.app.smartscan.R
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await

/**
 * Barcode Helper – Nora
 *
 * Handles barcode scanning using Google ML Kit.
 * It supports both static test images and dynamic images
 * provided from the camera or gallery.
 */
class BarCodeHelper(private val context: Context) {

    /**
     * Scans barcodes from a Bitmap.
     * Used when the user captures or selects an image.
     */
    suspend fun decode(bitmap: Bitmap): String {
        return try {
            val scanner = BarcodeScanning.getClient()
            val image = InputImage.fromBitmap(bitmap, 0)
            val barcodes = scanner.process(image).await()

            if (barcodes.isNotEmpty()) {
                val code = barcodes[0].rawValue ?: "No value found"
                Log.d("BARCODE", "Found code: $code")
                code
            } else {
                "No barcode detected in the image."
            }
        } catch (e: Exception) {
            Log.e("BARCODE", "Error during barcode scanning (bitmap)", e)
            "Error: ${e.message}"
        }
    }

    /**
     * Scans barcodes from an image URI (gallery or camera file).
     */
    suspend fun decodeUri(imageUri: Uri): String {
        return try {
            val scanner = BarcodeScanning.getClient()
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
            Log.e("BARCODE", "Error during barcode scanning (uri)", e)
            "Error: ${e.message}"
        }
    }

    /**
     * Test function for local development.
     * Runs barcode scanning on a static drawable image.
     */
    suspend fun runBarcodeOnTestImage(): String {
        return try {
            val scanner = BarcodeScanning.getClient()
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test_label1)
            val image = InputImage.fromBitmap(bitmap, 0)
            val barcodes = scanner.process(image).await()

            if (barcodes.isNotEmpty()) {
                val code = barcodes[0].rawValue ?: "No value found"
                Log.d("BARCODE", "Found code: $code")
                code
            } else {
                "No barcode detected in the test image."
            }
        } catch (e: Exception) {
            Log.e("BARCODE", "Error during barcode scanning (test image)", e)
            "Error: ${e.message}"
        }
    }
}
