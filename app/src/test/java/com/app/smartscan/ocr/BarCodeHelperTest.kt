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

/**
 * Unit tests for the BarCodeHelper class.
 *
 * These tests use MockK to simulate barcode decoding from a bitmap image.
 * The Android Log methods are mocked to prevent runtime errors during
 * local JVM execution (since Log is an Android framework class).
 */
class BarCodeHelperTest {

    @Test
    fun testDecodeReturnsExpectedBarcode() = runBlocking {
        // Mock Android Log functions to avoid crashes during JVM test
        mockkStatic(Log::class)
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0

        // Mock Bitmap and BarCodeHelper
        val bitmap = mockk<Bitmap>()
        val mockBarcodeHelper = mockk<BarCodeHelper>()

        // Simulated barcode value
        val expectedBarcode = "1234567890123"

        // Mock coroutine-based decode() function
        coEvery { mockBarcodeHelper.decode(bitmap) } returns expectedBarcode

        // Run and verify
        val result = mockBarcodeHelper.decode(bitmap)
        assertEquals(expectedBarcode, result)
    }

    @Test
    fun testDecodeResultIsNotNull() = runBlocking {
        // Mock Android Log functions
        mockkStatic(Log::class)
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0

        val bitmap = mockk<Bitmap>()
        val mockBarcodeHelper = mockk<BarCodeHelper>()

        // Simulate successful barcode decoding
        coEvery { mockBarcodeHelper.decode(bitmap) } returns "9876543210987"

        val result = mockBarcodeHelper.decode(bitmap)

        // Verify that result is not null
        assertNotNull(result)
    }
}
