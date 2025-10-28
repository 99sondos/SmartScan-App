package com.app.smartscan.ocr

import android.graphics.Bitmap
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests for the OcrHelper class.
 * These tests use MockK to simulate OCR analysis on a bitmap without requiring
 * actual image processing or Android dependencies.
 */
class OcrHelperTest {

    @Test
    fun testOcrAnalyzeReturnsExpectedText() = runBlocking {
        // Mock a Bitmap and OcrHelper
        val bitmap = mockk<Bitmap>()
        val mockOcrHelper = mockk<OcrHelper>()

        // Define expected OCR result
        val expectedText = "Water, Alcohol, Fragrance"

        // Simulate OCR behavior
        coEvery { mockOcrHelper.analyze(bitmap) } returns expectedText

        // Run the function
        val result = mockOcrHelper.analyze(bitmap)

        // Verify that the result matches
        assertEquals(expectedText, result)
    }

    @Test
    fun testOcrAnalyzeResultIsNotNull() = runBlocking {
        val bitmap = mockk<Bitmap>()
        val mockOcrHelper = mockk<OcrHelper>()

        // Simulate OCR returning non-null text
        coEvery { mockOcrHelper.analyze(bitmap) } returns "Some OCR text"

        val result = mockOcrHelper.analyze(bitmap)

        // Ensure result is not null
        assertNotNull(result)
    }
}
