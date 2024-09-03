package com.example.nzweather.network

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    var main: ForecastMain,
    val weather: List<ForecastWeather>,
    val wind: Wind,
    val dt_txt: String
)

data class ForecastMain(
    var temp: Float,
    var feels_like: Float,
    val pressure: Int,
    val humidity: Int
)

data class ForecastWeather(
    val description: String,
    val icon: String
)

