package com.app.smartscan.aiCamera

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33]) // använder Android API 33 för Robolectric
class ImageUtilsTest {

    @Test
    fun `rotate 90 degrees swaps width and height`() {
        val original = Bitmap.createBitmap(100, 200, Bitmap.Config.ARGB_8888)
        val rotated = rotateBitmapIfNeeded(original, ExifInterface.ORIENTATION_ROTATE_90)
        assertEquals(200, rotated.width)
        assertEquals(100, rotated.height)
    }

    @Test
    fun `no rotation keeps same size`() {
        val original = Bitmap.createBitmap(120, 80, Bitmap.Config.ARGB_8888)
        val rotated = rotateBitmapIfNeeded(original, ExifInterface.ORIENTATION_NORMAL)
        assertEquals(120, rotated.width)
        assertEquals(80, rotated.height)
    }
}
