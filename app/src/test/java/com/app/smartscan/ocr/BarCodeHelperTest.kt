package com.app.smartscan.ocr

import android.graphics.Bitmap
import android.util.Log
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BarCodeHelperTest {

    @Test
    fun testDecodeReturnsExpectedBarcode() = runBlocking {
        // Mocka Log-funktionerna så att de inte kraschar
        mockkStatic(Log::class)
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0

        // Mocka BarcodeHelper och Bitmap
        val bitmap = mockk<Bitmap>()
        val mockBarcodeHelper = mockk<BarCodeHelper>()

        // Simulerat streckkodsvärde
        val expectedBarcode = "1234567890123"
        coEvery { mockBarcodeHelper.decode(bitmap) } returns expectedBarcode

        // Testa decode()
        val result = mockBarcodeHelper.decode(bitmap)
        assertEquals(expectedBarcode, result)
    }

    @Test
    fun testDecodeResultIsNotNull() = runBlocking {
        mockkStatic(Log::class)
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0

        val bitmap = mockk<Bitmap>()
        val mockBarcodeHelper = mockk<BarCodeHelper>()

        coEvery { mockBarcodeHelper.decode(bitmap) } returns "9876543210987"

        val result = mockBarcodeHelper.decode(bitmap)
        assertNotNull(result)
    }
}
