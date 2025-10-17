package com.app.smartscan.ocr

import android.content.Context
import android.net.Uri
import android.util.Log
import android.graphics.BitmapFactory
import com.app.smartscan.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

/**
 * 🔹 OCR Helper – Nora
 *
 * This file handles text recognition (OCR) using Google ML Kit.
 * For now, it runs on a static test image from `drawable` (test_label1),
 * but will later be replaced with dynamic input from the camera.
 */

/**
 * Runs OCR (text recognition) on a test image stored in drawable.
 * Used only for *local testing* right now.
 *
 * TODO: Replace `R.drawable.test_label1` with live image input from CameraX
 * once the camera functionality is fully integrated.
 */
suspend fun runOcrOnTestImage(context: Context): String {
    return try {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // ⚠️ TEMPORARY: Static image used for testing
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test_label1)
        val image = InputImage.fromBitmap(bitmap, 0)

        // Process image using ML Kit
        val result = recognizer.process(image).await()

        // Return recognized text or fallback message
        result.text.ifBlank { "No text detected in the image." }

    } catch (e: Exception) {
        Log.e("OCR", "Error during text recognition", e)
        "Error: ${e.message}"
    }
}

/**
 * Runs OCR (text recognition) on an image selected or taken by the user.
 *
 *  This function will be used once CameraX or gallery import is connected.
 *
 * @param context Context from the activity or composable
 * @param imageUri The URI of the image to analyze
 * @return Recognized text or an error message
 */
suspend fun runOcrOnImageUri(context: Context, imageUri: Uri): String {
    return try {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // 🔄 Converts selected or captured image into ML Kit format
        val image = InputImage.fromFilePath(context, imageUri)

        val result = recognizer.process(image).await()

        // Return recognized text or fallback message
        result.text.ifBlank { "No text detected in the image." }

    } catch (e: Exception) {
        Log.e("OCR", "Error during text recognition", e)
        "Error: ${e.message}"
    }
}
