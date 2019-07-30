package org.eztarget.papeler.engine.foliage

import org.eztarget.papeler.engine.common.Vector2


class FoliageNode(var positionVector: Vector2) {

    var nextNode: FoliageNode? = null

    fun distanceToOtherNode(otherNode: FoliageNode): Double {
        return positionVector.distance(otherNode.positionVector)
    }
}