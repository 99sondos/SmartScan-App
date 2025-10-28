package com.app.smartscan.aiCamera

import android.graphics.Bitmap
import com.app.smartscan.analysis.analyzeSkinTypeFromBitmap
import com.app.smartscan.ocr.BarCodeHelper
import com.app.smartscan.ocr.OcrHelper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiCameraActivityTest {

    @Test
    fun testOcrAnalysisMock() = runBlocking {
        val mockOcr = mockk<OcrHelper>()
        val bitmap = mockk<Bitmap>()

        // Simulerar ett OCR-resultat
        coEvery { mockOcr.analyze(bitmap) } returns "Water, Alcohol, Fragrance"

        val result = mockOcr.analyze(bitmap)
        assertEquals("Water, Alcohol, Fragrance", result)
    }

    @Test
    fun testBarcodeAnalysisMock() = runBlocking {
        val mockBarcodeHelper = mockk<BarCodeHelper>()
        val bitmap = mockk<Bitmap>()

        // Simulerar ett avläst streckkodsvärde
        coEvery { mockBarcodeHelper.decode(bitmap) } returns "1234567890123"

        val result = mockBarcodeHelper.decode(bitmap)
        assertEquals("1234567890123", result)
    }

    @Test
    fun testSkinAnalysisMock() = runBlocking {
        val bitmap = mockk<Bitmap>()

        // Testar att funktionen returnerar något icke-tomt resultat
        val result = analyzeSkinTypeFromBitmap(bitmap)
        assertTrue(result.isNotEmpty())
    }
}
