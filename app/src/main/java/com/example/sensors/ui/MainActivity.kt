package com.example.sensors.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sensors.ui.ui.theme.SensorsTheme

class MainActivity : ComponentActivity(), SensorEventListener
{
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        viewModel.init(this)
        setContent {
            SensorsTheme { // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    MainView(viewModel)
                }
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        viewModel.registerListener(this)
    }

    override fun onPause()
    {
        viewModel.unregisterAll(this)
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent?)
    {
        event?.let {
            viewModel.loadSensorData(it)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int)
    {
        //do nothing yet
    }
}

@Composable
fun MainView(model: MainViewModel, modifier: Modifier = Modifier)
{
    val text = remember { model.orientationDataString }
    val text1 = remember { model.accelerateDataString }
    val sensors = remember { model.sensorsInfo }
    Column {
        Text(text = text.value, modifier = modifier)
        Text(text = text1.value, modifier = modifier)
        Text(text = sensors.value)
        Button(onClick = { model.getAvailableSensors() }) {
            Text(text = "Show available sensors")
        }
        Button(onClick = { model.sensorsInfo.value = "" }) {
            Text(text = "Hide available sensors")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    SensorsTheme {
        MainView(MainViewModel())
    }
}