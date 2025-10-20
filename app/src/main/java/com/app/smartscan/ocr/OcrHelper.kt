package com.app.smartscan.ocr

import android.content.Context
import android.net.Uri
import android.util.Log
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.app.smartscan.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

/**
 * OCR Helper – Nora
 *
 * Handles text recognition (OCR) using Google ML Kit.
 * It can run on either:
 *  - A static test image (for testing)
 *  - A live image or gallery import (URI or Bitmap)
 */
class OcrHelper(private val context: Context) {

    /**
     * Runs OCR (text recognition) directly on a Bitmap.
     * Used when the user takes a picture or selects one from the gallery.
     */
    suspend fun analyze(bitmap: Bitmap): String {
        return try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            result.text.ifBlank { "No text detected in the image." }
        } catch (e: Exception) {
            Log.e("OCR", "Error during text recognition (bitmap)", e)
            "Error: ${e.message}"
        }
    }

    /**
     * Runs OCR on an image from a URI (used for gallery selections).
     */
    suspend fun analyzeUri(imageUri: Uri): String {
        return try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromFilePath(context, imageUri)
            val result = recognizer.process(image).await()
            result.text.ifBlank { "No text detected in the image." }
        } catch (e: Exception) {
            Log.e("OCR", "Error during text recognition (uri)", e)
            "Error: ${e.message}"
        }
    }

    /**
     * Temporary test function.
     * Runs OCR on a static drawable resource for local testing only.
     */
    suspend fun runOcrOnTestImage(): String {
        return try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test_label1)
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            result.text.ifBlank { "No text detected in the test image." }
        } catch (e: Exception) {
            Log.e("OCR", "Error during text recognition (test image)", e)
            "Error: ${e.message}"
        }
    }
}
