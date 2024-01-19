package com.example.sensors.ui

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.sensors.ui.ui.theme.SensorsTheme


class MainActivity : ComponentActivity()
{
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
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
        viewModel.registerAllSensors(this)
    }

    override fun onPause()
    {
        viewModel.unregisterAllSensors()
        super.onPause()
    }
}

@Composable
fun MainView(model: MainViewModel, modifier: Modifier = Modifier)
{
    val text = remember { model.orientationDataString }
    val text1 = remember { model.accelerateDataString }
    val context = LocalContext.current
    Column {
        Text(text = text.value, modifier = modifier)
        Text(text = text1.value, modifier = modifier)

        Button(onClick = { model.unregisterAllSensors() }) {
            Text(text = "Stop sensors")
        }
        Button(onClick = { model.registerAllSensors(context) }) {
            Text(text = "Start sensors")
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