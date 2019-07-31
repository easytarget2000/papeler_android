package org.eztarget.papeler.engine.common

import android.graphics.Canvas
import android.graphics.PorterDuff

class CanvasBackgroundDrawer {

    var canvas: Canvas? = null

    fun drawColor(color: Int) {
        canvas!!.drawColor(color)
    }
}