package org.eztarget.papeler.engine.foliage

import android.graphics.Canvas
import kotlin.random.Random

class CanvasFoliageGenerator(
        private val randomNumberGenerator: Random = Random.Default,
        private val nodeDrawer: CanvasFoliageNodeDrawer = CanvasFoliageNodeDrawer()
) {
    var enabled = true
    var canvas: Canvas? = null
    private lateinit var foliages: Array<Foliage>

    fun initFoliages(imageSize: Double) {
        val firstFoliage = Foliage(
                worldSize = imageSize,
                randomNumberGenerator = randomNumberGenerator
        )
        firstFoliage.initNodesInCircles(
                x = imageSize / 2.0,
                y = imageSize / 2.0,
                worldSize = imageSize,
                randomNumberGenerator = randomNumberGenerator
        )
        foliages = arrayOf(firstFoliage)
    }

    fun updateAndDrawFoliages(addNodes: Boolean = true) {
        for (foliage in foliages) {
            updateAndDrawFoliage(foliage, addNodes)
        }
    }

    private fun updateAndDrawFoliage(foliage: Foliage, addNodes: Boolean = true) {
        foliage.updateNodes(addNodes)
        foliage.iterateNodes {
            if (!enabled) {
                return@iterateNodes
            }
            nodeDrawer.canvas = canvas
            nodeDrawer.drawNode(it)
            nodeDrawer.canvas = null
        }
    }
}