package org.eztarget.papeler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Environment
import android.util.Log

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

internal class BackgroundImageOpener {

    val file: File?
        get() {
            val destinationDir: File
            destinationDir = File(Environment.getExternalStorageDirectory().path + "/waypr/")
            if (destinationDir.exists()) {
                Log.d(TAG, "Directory " + destinationDir.absolutePath + " exists.")
            } else {
                Log.d(TAG, "Creating " + destinationDir.absolutePath + ".")
                if (!destinationDir.mkdirs()) {
                    Log.e(TAG, "Could not create " + destinationDir.absolutePath + ".")
                    return null
                }
            }

            return File(destinationDir, "w.png")
        }

    @Throws(Exception::class)
    fun load(): Bitmap {

        val imageFile = file
        if (imageFile == null) {
            Log.e(TAG, "Image file is null.")
            throw FileNotFoundException("Image File is null")
        }

        val imageStream = FileInputStream(file)
        return BitmapFactory.decodeStream(imageStream)
    }

    @Throws(Exception::class)
    fun loadRatioPreserved(targetWidth: Int, targetHeight: Int): Bitmap {

        val bitmap = load()
        val sourceWidth = bitmap.width
        val sourceHeight = bitmap.height

        val xScale = targetWidth.toFloat() / sourceWidth
        val yScale = targetHeight.toFloat() / sourceHeight
        val scale = Math.max(xScale, yScale)

        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight

        val left = (targetWidth - scaledWidth) / 2
        val top = (targetHeight - scaledHeight) / 2

        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        val dest = Bitmap.createBitmap(targetWidth, targetHeight, bitmap.config)
        val canvas = Canvas(dest)
        canvas.drawBitmap(bitmap, null, targetRect, null)

        return dest
    }

    companion object {
        private val TAG = BackgroundImageOpener::class.java.simpleName
    }

}
