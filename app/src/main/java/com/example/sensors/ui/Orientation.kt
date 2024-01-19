package com.example.sensors.ui

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round

class Orientation
{
    //see https://google-developer-training.github.io/android-developer-advanced-course-concepts/unit-1-expand-the-user-experience/lesson-3-sensors/3-2-c-motion-and-position-sensors/3-2-c-motion-and-position-sensors.html

    val eulerAngle = EulerAngle()
    val linearAcceleration = LinearAcceleration()
    val gravity = Gravity()
    val flatEarthAcceleration = FlatEarthAcceleration()

    fun updateXYEarthAcceleration()
    {

    }

    fun angleToString(): String
    {
        return "Euler angle:\n${eulerAngle.toDegrees()}"
    }
    fun accelerationToString(): String
    {
        val sb = StringBuilder()

        sb.appendLine("Linear accelerate data:")
        sb.appendLine(linearAcceleration)
        sb.appendLine("Gravity data:")
        sb.appendLine(gravity)
        return sb.toString()
    }
    class FlatEarthAcceleration
    {
        var x: Float = 0f
        var y: Float = 0f
    }

    abstract class BaseOrientationValue
    {
        var array = FloatArray(32)
        open fun init(array: FloatArray){ this.array = array}
    }
    class EulerAngle: BaseOrientationValue()
    {
        var azimuth: Float = 0f
        var pitch: Float = 0f
        var roll: Float = 0f

        val azimuthDegree
            get() = Math.toDegrees(azimuth.toDouble())

        val pitchDegree
            get() = Math.toDegrees(pitch.toDouble())

        val rollDegree
            get() = Math.toDegrees(roll.toDouble())

        override fun init(array: FloatArray)
        {
            super.init(array)
            azimuth = array[0]
            pitch = array[1]
            roll = array[2]
        }

        override fun toString(): String
        {
            return "Azimuth:$azimuth, Pitch:$pitch, Roll:$roll"
        }

        fun toDegrees(): String
        {
            return "Azimuth:${convertToDegreesStr(azimuthDegree)}\n " +
                    "Pitch:${convertToDegreesStr(pitchDegree)}\n" +
                    "Roll:${convertToDegreesStr(rollDegree)}\n"
        }
        private fun convertToDegreesStr(decDegree: Double): String
        {
            val res = convertToDegrees(decDegree)
            return "${res.first}°${res.second}'${res.third}''"
        }
        private fun convertToDegrees(dfDecimal: Double): Triple<Double, Double, Double> {
            // The following conversion routine is copied from Neptune and Company
            // http://www.neptuneandco.com/~jtauxe/bits/LatLonConvert.java
            // Begin conversion.
            var dfDegree: Double = floor(dfDecimal)
            var dfMinute: Double
            var dfSecond: Double
            // define variables local to this method
            val dfFrac: Double            // fraction after decimal
            val dfSec: Double            // fraction converted to seconds
            // Get degrees by chopping off at the decimal
            // correction required since floor() is not the same as int()
            if (dfDegree < 0) {
                dfDegree += 1
            }
            // Get fraction after the decimal
            dfFrac = abs(dfDecimal - dfDegree)
            // Convert this fraction to seconds (without minutes)
            dfSec = dfFrac * 3600
            // Determine number of whole minutes in the fraction
            dfMinute = floor(dfSec / 60)
            // Put the remainder in seconds
            dfSecond = dfSec - dfMinute * 60
            // Fix roundoff errors
            if (round(dfSecond) == 60.0) {
                dfMinute += 1
                dfSecond = 0.0
            }
            if (round(dfMinute) == 60.0) {
                if (dfDegree < 0) {
                    dfDegree -= 1
                } else
                // ( dfDegree => 0 )
                {
                    dfDegree += 1
                }
                dfMinute = 0.0
            }

            return Triple(dfDegree, dfMinute, dfSecond)
        }
    }
    class LinearAcceleration: BaseOrientationValue()
    {
        var x: Float = 0f
        var y: Float = 0f
        var z: Float = 0f
        override fun init(array: FloatArray)
        {
            super.init(array)
            x = array[0]
            y = array[1]
            z = array[2]
        }

        override fun toString(): String
        {
            return "x: $x, y: $y, z: $z"
        }
    }
    class Gravity: BaseOrientationValue()
    {
        var x: Float = 0f
        var y: Float = 0f
        var z: Float = 0f
        override fun init(array: FloatArray)
        {
            super.init(array)
            x = array[0]
            y = array[1]
            z = array[2]
        }

        override fun toString(): String
        {
            return "x: $x, y: $y, z: $z"
        }
    }
}