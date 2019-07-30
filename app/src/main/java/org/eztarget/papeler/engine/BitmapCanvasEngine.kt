package org.eztarget.papeler.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import org.eztarget.papeler.engine.common.CanvasBackgroundDrawer

class BitmapCanvasEngine {

    var backgroundColor: Int = Color.parseColor("#FFFFFFFF")
    var bitmapConfig = Bitmap.Config.ARGB_8888
    var bitmap: Bitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, bitmapConfig)
    var backgroundDrawer: CanvasBackgroundDrawer = CanvasBackgroundDrawer()

    fun updateAndDrawFrameOnCanvas(canvas: Canvas) {
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height
        if (bitmap.width != canvasWidth || bitmap.height != canvasHeight) {
            bitmap.reconfigure(canvasWidth, canvasHeight, bitmapConfig)
        }
        canvas.setBitmap(bitmap)

        drawBackground(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        backgroundDrawer.canvas = canvas
        backgroundDrawer.drawColor(backgroundColor)
        backgroundDrawer.canvas = null
    }

    companion object {
        private const val DEFAULT_WIDTH = 1920
        private const val DEFAULT_HEIGHT = 1080
    }
}