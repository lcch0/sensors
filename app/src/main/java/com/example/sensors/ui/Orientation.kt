package com.example.sensors.ui

class Orientation
{
    //see https://google-developer-training.github.io/android-developer-advanced-course-concepts/unit-1-expand-the-user-experience/lesson-3-sensors/3-2-c-motion-and-position-sensors/3-2-c-motion-and-position-sensors.html

    val eulerAngle = EulerAngle()
    val linearAcceleration = LinearAcceleration()
    val gravity = Gravity()
    val calculated = CalculatedAccelerationAndGravity()
    val flatEarthAcceleration = FlatEarthAcceleration()

    fun updateXYEarthAcceleration()
    {

    }

    fun angleToString(): String
    {
        return "Euler angle:\n${eulerAngle}"
    }
    fun accelerationToString(): String
    {
        val sb = StringBuilder()

        sb.appendLine("Linear accelerate data:")
        sb.appendLine(linearAcceleration)
        sb.appendLine("Accelerate data:")
        sb.appendLine(calculated.rawAcceleration)
        sb.appendLine("Accelerate motion data:")
        sb.appendLine(calculated.linearAcceleration)
        sb.appendLine("Accelerate gravity data:")
        sb.appendLine(calculated.gravity)
        sb.appendLine("Real gravity data:")
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

    class CalculatedAccelerationAndGravity
    {
        val rawAcceleration = LinearAcceleration()
        val linearAcceleration = LinearAcceleration()
        val gravity = Gravity()
    }
}