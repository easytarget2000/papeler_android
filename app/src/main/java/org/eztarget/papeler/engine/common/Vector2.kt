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
import kotlin.math.sqrt

data class Vector2(var x: Double = 0.0, var y: Double = 0.0) {
    constructor(v: Double) : this(v, v)

    inline var r: Double
        get() = x
        set(value) {
            x = value
        }
    inline var g: Double
        get() = y
        set(value) {
            y = value
        }

    inline var s: Double
        get() = x
        set(value) {
            x = value
        }
    inline var t: Double
        get() = y
        set(value) {
            y = value
        }

//    operator fun get(index: VectorComponent) = when (index) {
//        VectorComponent.X, VectorComponent.R, VectorComponent.S -> x
//        VectorComponent.Y, VectorComponent.G, VectorComponent.T -> y
//        else -> throw IllegalArgumentException("index must be X, Y, R, G, S or T")
//    }

//    operator fun get(index1: VectorComponent, index2: VectorComponent): Vector2 {
//        return Vector2(get(index1), get(index2))
//    }

    operator fun get(index: Int) = when (index) {
        0 -> x
        1 -> y
        else -> throw IllegalArgumentException("index must be in 0..1")
    }

    inline operator fun invoke(index: Int) = get(index - 1)

    operator fun set(index: Int, v: Double) = when (index) {
        0 -> x = v
        1 -> y = v
        else -> throw IllegalArgumentException("index must be in 0..1")
    }

    operator fun set(index1: Int, index2: Int, v: Double) {
        set(index1, v)
        set(index2, v)
    }

//    operator fun set(index: VectorComponent, v: Double) = when (index) {
//        VectorComponent.X, VectorComponent.R, VectorComponent.S -> x = v
//        VectorComponent.Y, VectorComponent.G, VectorComponent.T -> y = v
//        else -> throw IllegalArgumentException("index must be X, Y, R, G, S or T")
//    }

//    operator fun set(index1: VectorComponent, index2: VectorComponent, v: Double) {
//        set(index1, v)
//        set(index2, v)
//    }


    inline operator fun plus(v: Double) = Vector2(x + v, y + v)
    inline operator fun minus(v: Double) = Vector2(x - v, y - v)
    inline operator fun times(v: Double) = Vector2(x * v, y * v)
    inline operator fun div(v: Double) = Vector2(x / v, y / v)

    inline operator fun plus(v: Vector2) = Vector2(x + v.x, y + v.y)
    inline operator fun minus(v: Vector2) = Vector2(x - v.x, y - v.y)
    inline operator fun times(v: Vector2) = Vector2(x * v.x, y * v.y)
    inline operator fun div(v: Vector2) = Vector2(x / v.x, y / v.y)

    inline fun distance(v: Vector2) = sqrt(pow(x - v.x, 2.0) + pow(y - v.y, 2.0))

    inline fun transform(block: (Double) -> Double): Vector2 {
        x = block(x)
        y = block(y)
        return this
    }
}