package com.example.nzweather.network

data class WeatherResponse(
    val coord: Coord,
    var main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val name: String
)

data class Main(
    var temp: Double,
    var feels_like: Double,
    val humidity: Int,
    val pressure: Int
)
data class Coord(
    val lat: Double,
    val lon: Double
)

data class Wind(
    val speed: Double,
    val deg: Int? = null
)


