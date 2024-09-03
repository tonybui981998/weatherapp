package com.example.nzweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.nzweather.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherViewModel: WeatherViewModel by viewModels()

        // Gọi dữ liệu thời tiết cho thành phố mặc định "Auckland"
        weatherViewModel.getWeatherData("Auckland")

        setContent {
            WeatherScreen(weatherViewModel = weatherViewModel)
        }
    }
}
