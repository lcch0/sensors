package com.example.sensors.ui

import android.content.Context
import android.hardware.SensorManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kircherelectronics.fsensor.observer.SensorSubject
import com.kircherelectronics.fsensor.sensor.acceleration.KalmanLinearAccelerationSensor
import com.kircherelectronics.fsensor.sensor.gyroscope.KalmanGyroscopeSensor


class MainViewModel: ViewModel()
{
    private var linearAccelerationSensor: KalmanLinearAccelerationSensor? = null
    private var orientationSensor: KalmanGyroscopeSensor? = null

    private var orientation = Orientation()

    val orientationDataString = mutableStateOf("No data")
    val accelerateDataString = mutableStateOf("No data")
    //val sensorsInfo = mutableStateOf("Available sensors")

    private val accelerometerObserver =
        SensorSubject.SensorObserver { data -> // Do interesting things here
            data?.let {
                loadAccelerationSensorData(it)
            }
        }

    private val orientationObserver =
        SensorSubject.SensorObserver { data -> // Do interesting things here
            data?.let {
                loadOrientationSensorData(it)
            }
        }

    fun registerAllSensors(context: Context)
    {
        linearAccelerationSensor = KalmanLinearAccelerationSensor(context).also {
            it.setSensorDelay(SensorManager.SENSOR_DELAY_UI)
            it.register(accelerometerObserver)
            it.start()
        }

        orientationSensor = KalmanGyroscopeSensor(context).also {
            it.setSensorDelay(SensorManager.SENSOR_DELAY_UI)
            it.register(orientationObserver)
            it.start()
        }
    }

    fun unregisterAllSensors()
    {
        linearAccelerationSensor?.let {
            it.unregister(accelerometerObserver)
            it.stop()
        }
        orientationSensor?.let {
            it.unregister(orientationObserver)
            it.stop()
        }
    }

    private fun loadAccelerationSensorData(array: FloatArray)
    {
        orientation.linearAcceleration.init(array)
        accelerateDataString.value = orientation.accelerationToString()
    }

    private fun loadOrientationSensorData(array: FloatArray)
    {
        orientation.eulerAngle.init(array)
        orientationDataString.value = orientation.angleToString()
    }
}