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
 * Runs OCR (text recognition) on a test image stored in drawable.
 * Used for local testing.
 */
suspend fun runOcrOnTestImage(context: Context): String {
    return try {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test_label)
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = recognizer.process(image).await()
        result.text.ifBlank { "No text detected in the image." }
    } catch (e: Exception) {
        Log.e("OCR", "Error during text recognition", e)
        "Error: ${e.message}"
    }
}

/**
 * Runs OCR (text recognition) on an image selected or taken by the user.
 *
 * @param context Context from the activity or composable
 * @param imageUri The URI of the image to analyze
 * @return A string with the recognized text or an error message
 */
suspend fun runOcrOnImageUri(context: Context, imageUri: Uri): String {
    return try {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromFilePath(context, imageUri)
        val result = recognizer.process(image).await()
        result.text.ifBlank { "No text detected in the image." }
    } catch (e: Exception) {
        Log.e("OCR", "Error during text recognition", e)
        "Error: ${e.message}"
    }
}
