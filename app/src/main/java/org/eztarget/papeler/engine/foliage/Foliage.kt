package org.eztarget.papeler.engine.foliage

import org.eztarget.papeler.BuildConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Foliage(worldSize: Double, randomNumberGenerator: Random = Random.Default) {

    var maxAge = 240
    var nodeRadius: Double = worldSize / 600.0
    var totalNodeCounter = 0
    var numOfInitialNodes = 64
    var maxNumOfTotalNodes = 512
    var nodesAddedPerRoundLimit = 8
    var nodeDensity: Int
    var neighbourGravity: Double
    var preferredNeighbourDistance = 0.0
    var maxPushDistance: Double
    var pushForce = 8.0
    var moving = true
    private lateinit var firstNode: FoliageNode

    init {
        neighbourGravity = nodeRadius * NODE_RADIUS_TO_GRAVITY_RATIO
        maxPushDistance = worldSize * WORLD_SIZE_TO_PUSH_DISTANCE_RATIO
        nodeDensity = randomNumberGenerator.nextInt(
                from = MIN_NODE_DENSITY,
                until = MAX_NODE_DENSITY
        )
    }

    fun initNodesInCircles(
            x: Double,
            y: Double,
            worldSize: Double,
            randomNumberGenerator: Random = Random.Default
    ) {
        val numberOfCircles = randomNumberGenerator.nextInt(
                from = MIN_NUM_OF_INIT_CIRCLES,
                until = MAX_NUM_OF_INIT_CIRCLES
        )
        val numOfNodesPerCircle = numOfInitialNodes / numberOfCircles
        val minCircleRadius = worldSize * WORLD_SIZE_TO_MIN_CIRCLE_RADIUS_RATIO
        val maxCircleRadius = worldSize * WORLD_SIZE_TO_MAX_CIRCLE_RADIUS_RATIO
        val maxJitter = worldSize * WORLD_SIZE_TO_JITTER_RATIO

        var lastNode: FoliageNode? = null
        for (circleIndex in 0 until numberOfCircles) {
            val xJitter = jitter(maxJitter, randomNumberGenerator)
            val circleCenterX = x + xJitter //* 10.0
            val yJitter = jitter(maxJitter, randomNumberGenerator)
            val circleCenterY = y + yJitter //* 10.0
            val radius = randomNumberGenerator.nextDouble(
                    from = minCircleRadius,
                    until = maxCircleRadius
            )
            val squeezeFactor = randomNumberGenerator.nextDouble(
                    from = MIN_CIRCLE_SQUEEZE_FACTOR,
                    until = MAX_CIRCLE_SQUEEZE_FACTOR
            )

            for (relativeNodeIndex in 0 until numOfNodesPerCircle) {

                val angleOfNode = TWO_PI * ((relativeNodeIndex + 1).toDouble() / numOfNodesPerCircle.toDouble())

                val nodeX = (circleCenterX
                        + cos(angleOfNode).toFloat() * radius * squeezeFactor
                        + jitter(maxJitter, randomNumberGenerator))
                val nodeY = (circleCenterY
                        + sin(angleOfNode).toFloat() * radius
                        + jitter(maxJitter, randomNumberGenerator))

                val currentNode = FoliageNode(nodeX, nodeY, nodeRadius)

                if (lastNode == null) {
                    firstNode = currentNode
                } else {
                    lastNode.nextNode = currentNode

                    val absoluteNodeIndex = (circleIndex * numOfNodesPerCircle) + relativeNodeIndex
                    if (absoluteNodeIndex == numOfInitialNodes - 1) {
                        val distanceToLastNode = currentNode.distanceToOtherNode(lastNode)
                        preferredNeighbourDistance = distanceToLastNode
                        currentNode.nextNode = firstNode
                        return
                    }
                }

                lastNode = currentNode
            }
        }
    }

    fun updateNodes(addNodes: Boolean = true) {
        iterateNodes {
            if (!moving) {
                return@iterateNodes
            }

            it.update(maxPushDistance, preferredNeighbourDistance, pushForce, neighbourGravity)
            if (addNodes
                    && totalNodeCounter < maxNumOfTotalNodes
                    && ++totalNodeCounter % nodeDensity == 0
            ) {
                addNodeNextTo(it)
            }
        }
    }

    fun iterateNodes(lambda: (FoliageNode)->Unit) {
        var currentNode: FoliageNode? = firstNode

        do {
            lambda(currentNode ?: return)
            currentNode = currentNode.nextNode
        } while (currentNode !== firstNode)
    }

    private fun addNodeNextTo(node: FoliageNode) {
        val oldNeighbour = node.nextNode ?: return

        val newNeighbourVector = node.positionVector.between(oldNeighbour.positionVector)
        val newNeighbour = FoliageNode(newNeighbourVector, nodeRadius)
        node.nextNode = newNeighbour
        newNeighbour.nextNode = oldNeighbour
    }

    companion object {
        private val tag = Foliage::class.java.simpleName
        private val verbose = BuildConfig.DEBUG
        private const val TWO_PI = PI * 2.0
        private const val MIN_NODE_DENSITY = 1
        private const val MAX_NODE_DENSITY = 11
        private const val NODE_RADIUS_TO_GRAVITY_RATIO = 0.5
        private const val WORLD_SIZE_TO_PUSH_DISTANCE_RATIO = 0.3
        private const val WORLD_SIZE_TO_JITTER_RATIO = 0.001
        private const val MIN_NUM_OF_INIT_CIRCLES = 1
        private const val MAX_NUM_OF_INIT_CIRCLES = 6
        private const val WORLD_SIZE_TO_MIN_CIRCLE_RADIUS_RATIO = 0.01
        private const val WORLD_SIZE_TO_MAX_CIRCLE_RADIUS_RATIO = 0.06
        private const val MIN_CIRCLE_SQUEEZE_FACTOR = 0.66
        private const val MAX_CIRCLE_SQUEEZE_FACTOR = MIN_CIRCLE_SQUEEZE_FACTOR * 2.0

        private fun jitter(maxValue: Double, randomNumberGenerator: Random): Double {
            return randomNumberGenerator.nextDouble(from = -maxValue, until = maxValue)
        }
    }
}