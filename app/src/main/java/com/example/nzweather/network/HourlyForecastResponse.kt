package com.example.nzweather.network

data class HourlyForecastResponse(
    val list: List<HourlyForecastItem>
)

data class HourlyForecastItem(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val dt_txt: String
)



data class Weather(
    val description: String,
    val icon: String
)
