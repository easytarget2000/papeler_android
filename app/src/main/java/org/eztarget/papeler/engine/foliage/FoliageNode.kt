package org.eztarget.papeler.engine.foliage

import org.eztarget.papeler.engine.common.Vector2


class FoliageNode(var positionVector: Vector2, var radius: Double) {

    var nextNode: FoliageNode? = null
    var moving = true

    constructor(x: Double, y: Double, radius: Double) : this(Vector2(x, y), radius)

    fun update(
            maxPushDistance: Double,
            preferredNeighbourDistance: Double,
            pushForce: Double,
            neighbourGravity: Double
    ) {
        var otherNode: FoliageNode? = nextNode

        var force = 0.0
        var angle = 0.0

        do {
            if (otherNode == null || otherNode.nextNode === this) {
                break
            }

            val distance = distanceToOtherNode(otherNode)

            if (distance > maxPushDistance) {
                otherNode = otherNode.nextNode
                continue
            }

            angle = angleToOtherNode(otherNode) + angle * 0.05
            force *= 0.05

            if (otherNode === nextNode) {
                if (distance > preferredNeighbourDistance) {
                    //                        force = mPreferredNeighbourDistanceHalf;
                    force += distance / pushForce
                } else {
                    force -= neighbourGravity
                }

            } else {

                force -= if (distance < radius) {
                    radius
                } else {
                    pushForce / distance
                }

            }

//            positionVector = positionVector.move(force, angle)

            otherNode = otherNode.nextNode
        } while (moving)

        positionVector = positionVector.move(force, angle)
    }

    fun distanceToOtherNode(otherNode: FoliageNode): Double {
        return positionVector.distance(otherNode.positionVector)
    }

    fun angleToOtherNode(otherNode: FoliageNode): Double {
        return positionVector.angle(otherNode.positionVector)
    }
}