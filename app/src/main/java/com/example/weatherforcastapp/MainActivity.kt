package com.example.weatherforcastapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.weatherforcastapp.ui.screen.SplashScreen
import com.example.weatherforcastapp.ui.screen.WeatherScreen
import com.example.weatherforcastapp.ui.theme.WeatherForcastAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherForcastAppTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen()
                    LaunchedEffect(Unit) {
                        delay(2000L) // Show splash for 2 seconds
                        showSplash = false
                    }
                } else {
                    WeatherScreen()
                }
            }
        }
    }
}
