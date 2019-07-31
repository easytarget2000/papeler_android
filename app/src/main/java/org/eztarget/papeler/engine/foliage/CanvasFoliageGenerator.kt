package org.eztarget.papeler.engine.foliage

class CanvasFoliageGenerator {

    var foliages: Array<Foliage>
    var nodeDrawer: CanvasFoliageNodeDrawer = CanvasFoliageNodeDrawer()

    fun updateAndDrawFoliages(addNodes: Boolean = true) {
        for (foliage in foliages) {
            updateAndDrawFoliage(foliage, addNodes)
        }
    }

    fun updateAndDrawFoliage(foliage: Foliage, addNodes: Boolean = true) {
        foliage.updateNodes(addNodes)
        foliage.iterateNodes {
            nodeDrawer.drawNode(it)
        }
    }
}