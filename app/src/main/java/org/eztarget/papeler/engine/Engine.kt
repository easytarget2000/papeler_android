package org.eztarget.papeler.engine

import android.graphics.Bitmap
import android.graphics.Canvas

class BitmapCanvasEngine {

    var width = 0
    var height = 0
    var bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    var canvas: Canvas = Canvas(bitmap)

    fun clear() {

    }

    fun start(width: Int, height: Int) {
        this.width = width
        this.height = height

//        canvas.setBitmap(bitmap)
        bitmap.reconfigure(width, height, Bitmap.Config.ARGB_8888)
    }

    fun pause() {

    }
}