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
    private var magnetData= FloatArray(3)
    private val rawAcceleration = FloatArray(3)
    private var filteredGravity = FloatArray(3)
    private var filteredAcceleration = FloatArray(3)

    private var orientation = Orientation()

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
            Sensor.TYPE_ACCELEROMETER       -> updateAcceleration(event.values)
            Sensor.TYPE_LINEAR_ACCELERATION -> updateLinearAcceleration(event.values)
            Sensor.TYPE_GRAVITY             -> updateGravity(event.values)
            Sensor.TYPE_MAGNETIC_FIELD      -> magnetData = event.values.clone()
        }

        updateRotation()

        orientationDataString.value = orientation.angleToString()
        accelerateDataString.value = orientation.accelerationToString()
    }

    private fun updateRotation()
    {
        val eulerAngle = FloatArray(3)
        SensorManager.getRotationMatrix(rotationMatrix, null, orientation.calculated.rawAcceleration.array, magnetData)
        SensorManager.getOrientation(rotationMatrix, eulerAngle)

        orientation.eulerAngle.init(eulerAngle)
    }

    private fun updateLinearAcceleration(values: FloatArray)
    {
        orientation.linearAcceleration.init(values.clone())
    }

    private fun updateGravity(values: FloatArray)
    {
        orientation.gravity.init(values.clone())
    }

    private fun updateAcceleration(values: FloatArray)
    {
        val accelerateData = values.clone()
        for (i in 0..2)
        {
            rawAcceleration[i] = values[i]
            filteredGravity[i] = (0.1f * values[i] + 0.9f * filteredGravity[i])
            filteredAcceleration[i] = (values[i] - filteredGravity[i])
        }

        orientation.calculated.rawAcceleration.init(rawAcceleration)
        orientation.calculated.linearAcceleration.init(filteredAcceleration)
        orientation.calculated.gravity.init(filteredGravity)
    }
}