package org.eztarget.papeler.engine.foliage

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class CanvasFoliageNodeDrawer(val paint: Paint = Paint()) {

    var canvas: Canvas? = null

    fun drawNode(node: FoliageNode) {
        paint.color = Color.BLUE
        canvas!!.drawPoint(
                node.positionVector.x.toFloat(),
                node.positionVector.y.toFloat(),
                paint
        )
    }
}