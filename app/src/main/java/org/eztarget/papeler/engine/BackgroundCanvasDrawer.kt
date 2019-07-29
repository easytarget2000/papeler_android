package org.eztarget.papeler.engine

import android.graphics.Canvas

class BackgroundCanvasDrawer {

    var canvas: Canvas? = null

    fun drawColor(color: Int) {
        canvas!!.drawColor(color)
    }
}