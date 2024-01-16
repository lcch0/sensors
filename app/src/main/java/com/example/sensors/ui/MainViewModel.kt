package com.example.sensors.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference


class MainViewModel: ViewModel()
{
    private var sensorManager: SensorManager? = null
    private var rotationMatrix= FloatArray(16)
    private var accelerateData= FloatArray(3)
    private var magnetData= FloatArray(3)
    private var orientationData= FloatArray(3)
    private var valuesAccel = FloatArray(3)
    private var valuesAccelMotion = FloatArray(3)
    private var valuesAccelGravity = FloatArray(3)
    private var valuesLinAccel = FloatArray(3)
    private var valuesGravity = FloatArray(3)

    private var accelerometer: WeakReference<Sensor?> = WeakReference(null)
    private var linearAccelerometer: WeakReference<Sensor?> = WeakReference(null)
    private var gravitron: WeakReference<Sensor?> = WeakReference(null)
    private var magnetron: WeakReference<Sensor?> = WeakReference(null)

    val orientationDataString = mutableStateOf("No data")
    val accelerateDataString = mutableStateOf("No data")
    val sensorsInfo = mutableStateOf("Available sensors")

    fun init(context: Context)
    {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        sensorManager?.let {
            accelerometer = WeakReference(it.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
            magnetron = WeakReference(it.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
            linearAccelerometer = WeakReference(it.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION))
            gravitron = WeakReference(it.getDefaultSensor(Sensor.TYPE_GRAVITY))
        }
    }

    fun getAvailableSensors(): String
    {
        var res = "No sensors found"
        sensorManager?.let {
            val list = it.getSensorList(Sensor.TYPE_ALL).filterNotNull()
            val sb = StringBuilder("Sensors found:\n")
            for (s in list)
            {
                sb.appendLine(s.name)
            }

            res = sb.toString()
        }

        sensorsInfo.value = res
        return res
    }

    fun registerListener(listener: SensorEventListener)
    {
        sensorManager?.let {manager ->
            accelerometer.get()?.let {
                manager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
            }
            linearAccelerometer.get()?.let {
                manager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
            }
            gravitron.get()?.let {
                manager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
            }
            magnetron.get()?.let {
                manager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    fun unregisterAll(listener: SensorEventListener)
    {
        sensorManager?.unregisterListener(listener)
    }

    fun loadSensorData(event: SensorEvent)
    {
        when (event.sensor.type)
        {
            Sensor.TYPE_ACCELEROMETER       -> getAcceleration(event.values)
            Sensor.TYPE_GRAVITY             -> getGravity(event.values)
            Sensor.TYPE_LINEAR_ACCELERATION -> getLinearAcceleration(event.values)
            Sensor.TYPE_MAGNETIC_FIELD -> magnetData = event.values.clone()
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerateData, magnetData)
        SensorManager.getOrientation(rotationMatrix, orientationData)

        val sb = StringBuilder("Orientation data:\n")
        for (f in orientationData)
            sb.appendLine(f)

        orientationDataString.value = sb.toString()

        sb.clear()
        sb.appendLine("Linear accelerate data:")
        sb.appendLine("x: ${valuesLinAccel[0]}, y: ${valuesLinAccel[1]}, z: ${valuesLinAccel[2]},")
        sb.appendLine("Accelerate data:")
        sb.appendLine("x: ${valuesAccel[0]}, y: ${valuesAccel[1]}, z: ${valuesAccel[2]},")
        sb.appendLine("Accelerate gravity data:")
        sb.appendLine("x: ${valuesAccelGravity[0]}, y: ${valuesAccelGravity[1]}, z: ${valuesAccelGravity[2]},")
        sb.appendLine("Accelerate motion data:")
        sb.appendLine("x: ${valuesAccelMotion[0]}, y: ${valuesAccelMotion[1]}, z: ${valuesAccelMotion[2]},")
        sb.appendLine("Real gravity data:")
        sb.appendLine("x: ${valuesGravity[0]}, y: ${valuesGravity[1]}, z: ${valuesGravity[2]},")

        accelerateDataString.value = sb.toString()
    }

    private fun getLinearAcceleration(values: FloatArray)
    {
        valuesLinAccel = values.clone()
    }

    private fun getGravity(values: FloatArray)
    {
        valuesGravity = values.clone()
    }

    private fun getAcceleration(values: FloatArray)
    {
        accelerateData = values.clone()
        for (i in 0..2)
        {
            valuesAccel[i] = values[i]
            valuesAccelGravity[i] = (0.1f * values[i] + 0.9f * valuesAccelGravity[i])
            valuesAccelMotion[i] = (values[i] - valuesAccelGravity[i])
        }
    }
}