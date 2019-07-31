/*
 * Modified by michel@easy-target.eu.
 * Based on https://github.com/romainguy/kotlin-math/blob/master/src/main/kotlin/com/curiouscreature/kotlin/math/Vector.kt:
 *
 * Copyright (C) 2017 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE")

package org.eztarget.papeler.engine.common

import java.lang.Math.pow
import kotlin.math.*

data class Vector2(val x: Double = 0.0, val y: Double = 0.0) {

    operator fun get(index: Int) = when (index) {
        0 -> x
        1 -> y
        else -> throw IllegalArgumentException("index must be in 0..1")
    }

    fun between(v2: Vector2): Vector2 {
        return (this + v2) / 2.0
    }

    fun move(distance: Double, angle: Double): Vector2 {
        return Vector2(
                x = x + (cos(angle) * distance),
                y = y + (sin(angle) * distance)
        )
    }

    fun angle(v2: Vector2): Double {
        val calcAngle = atan2(y = -(y - v2.y), x = v2.x - x)

        return if (calcAngle < 0) {
            calcAngle + TWO_PI
        } else {
            calcAngle
        }
    }

    inline operator fun plus(v: Double) = Vector2(x + v, y + v)
    inline operator fun minus(v: Double) = Vector2(x - v, y - v)
    inline operator fun times(v: Double) = Vector2(x * v, y * v)
    inline operator fun div(v: Double) = Vector2(x / v, y / v)

    inline operator fun plus(v: Vector2) = Vector2(x + v.x, y + v.y)
    inline operator fun minus(v: Vector2) = Vector2(x - v.x, y - v.y)
    inline operator fun times(v: Vector2) = Vector2(x * v.x, y * v.y)
    inline operator fun div(v: Vector2) = Vector2(x / v.x, y / v.y)

    inline fun distance(v: Vector2) = sqrt(pow(x - v.x, 2.0) + pow(y - v.y, 2.0))

    companion object {
        private const val TWO_PI = 2.0 * PI
    }
}