package com.app.smartscan.analysis

import android.graphics.Bitmap
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SkinAnalysisTest {

    @Test
    fun testAnalyzeSkinTypeReturnsResult() = runBlocking {
        // Skapar en mockad bitmap (behöver inte en riktig bild)
        val bitmap: Bitmap = mockk()

        // Kör funktionen och kollar att resultatet inte är tomt
        val result = analyzeSkinTypeFromBitmap(bitmap)

        assertTrue(result.isNotEmpty())
    }
}
