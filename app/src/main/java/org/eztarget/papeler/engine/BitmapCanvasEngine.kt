package org.eztarget.papeler.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import org.eztarget.papeler.engine.common.CanvasBackgroundDrawer
import org.eztarget.papeler.engine.foliage.CanvasFoliageGenerator
import kotlin.math.min

class BitmapCanvasEngine(
        val backgroundDrawer: CanvasBackgroundDrawer = CanvasBackgroundDrawer(),
        val foliageGenerator: CanvasFoliageGenerator = CanvasFoliageGenerator()
) {
    var backgroundColor: Int = Color.parseColor("#FFFF00FF")
    var bitmapConfig = Bitmap.Config.ARGB_8888
    var bitmap: Bitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, bitmapConfig)

    fun setup(canvas: Canvas) {
        bindBitmapToCanvas(canvas)
        drawBackground(canvas)
        foliageGenerator.initFoliages(sizeOfCanvas(canvas))
    }

    fun updateAndDrawFrameOnCanvas(canvas: Canvas) {
        bindBitmapToCanvas(canvas)
//        updateAndDrawFoliages(canvas)
    }

    private fun bindBitmapToCanvas(canvas: Canvas) {
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height
        if (bitmap.width != canvasWidth || bitmap.height != canvasHeight) {
            bitmap.reconfigure(canvasWidth, canvasHeight, bitmapConfig)
        }
        canvas.setBitmap(bitmap)
    }

    private fun drawBackground(canvas: Canvas) {
        backgroundDrawer.canvas = canvas
        backgroundDrawer.drawColor(backgroundColor)
        backgroundDrawer.canvas = null
    }

    private fun updateAndDrawFoliages(canvas: Canvas) {
        foliageGenerator.canvas = canvas
        foliageGenerator.updateAndDrawFoliages(true)
        foliageGenerator.canvas = null
    }

    companion object {
        private const val DEFAULT_WIDTH = 1920
        private const val DEFAULT_HEIGHT = 1080

        private fun sizeOfCanvas(canvas: Canvas): Double {
            return min(canvas.width, canvas.height).toDouble()
        }
    }
}