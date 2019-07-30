package org.eztarget.papeler.engine.common

import android.graphics.Canvas

class CanvasBackgroundDrawer {

    var canvas: Canvas? = null

    fun drawColor(color: Int) {
        canvas!!.drawColor(color)
    }
}