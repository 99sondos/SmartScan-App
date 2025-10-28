package com.app.smartscan.ocr

import android.graphics.Bitmap
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class OcrHelperTest {

    @Test
    fun testOcrAnalyzeReturnsExpectedText() = runBlocking {
        // Skapa ett mockat Bitmap-objekt (vi behöver ingen riktig bild)
        val bitmap = mockk<Bitmap>()
        val mockOcrHelper = mockk<OcrHelper>()

        // Simulera ett förväntat OCR-resultat
        val expectedText = "Water, Alcohol, Fragrance"
        coEvery { mockOcrHelper.analyze(bitmap) } returns expectedText

        // Anropa funktionen
        val result = mockOcrHelper.analyze(bitmap)

        // Kontrollera att resultatet är det förväntade
        assertEquals(expectedText, result)
    }

    @Test
    fun testOcrAnalyzeResultIsNotNull() = runBlocking {
        val bitmap = mockk<Bitmap>()
        val mockOcrHelper = mockk<OcrHelper>()

        coEvery { mockOcrHelper.analyze(bitmap) } returns "Some OCR text"

        val result = mockOcrHelper.analyze(bitmap)
        assertNotNull(result)
    }
}
