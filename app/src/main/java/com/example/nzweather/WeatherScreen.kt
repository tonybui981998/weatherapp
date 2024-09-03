package com.example.nzweather

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.nzweather.network.ForecastItem
import com.example.nzweather.ui.theme.NZWeatherTheme
import com.example.nzweather.viewmodel.WeatherViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    var selectedCity by remember { mutableStateOf("Auckland") }
    var expanded by remember { mutableStateOf(false) }
    val cities = listOf("Auckland", "Wellington", "Hamilton", "Christchurch", "Dunedin")

    val currentWeather = weatherViewModel.currentWeather.observeAsState()
    val forecastWeather = weatherViewModel.forecastWeather.observeAsState()
    val errorMessage = weatherViewModel.errorMessage.observeAsState()
    var unit by remember { mutableStateOf("metric") } // Default to Celsius

    val currentDate = LocalDate.now()
    val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val month = currentDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val dayOfMonth = currentDate.dayOfMonth

    NZWeatherTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA))
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = selectedCity,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B)
                    )
                    Text(
                        text = "Today is $dayOfWeek",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B)
                    )
                    Text(
                        text = "$dayOfMonth $month",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B)
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            // Temperature Unit Selector
                            Button(
                                onClick = {
                                    unit = if (unit == "metric") "imperial" else "metric"
                                    weatherViewModel.getWeatherData(selectedCity, unit)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "Unit: ${if (unit == "metric") "Celsius (°C)" else "Fahrenheit (°F)"}")
                            }

                            // Change City Button
                            Button(
                                onClick = { expanded = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "Change City")
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        selectedCity = city
                                        weatherViewModel.getWeatherData(city, unit)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    currentWeather.value?.let { weather ->
                        // Determine background color based on weather condition
                        val backgroundColor = when {
                            weather.weather[0].description.contains("rain", true) -> Color(0xFF80D8FF)
                            weather.weather[0].description.contains("cloud", true) -> Color(0xFFB3E5FC)
                            weather.weather[0].description.contains("clear", true) -> Color(0xFFFFF176)
                            weather.weather[0].description.contains("snow", true) -> Color(0xFFE1F5FE)
                            else -> Color(0xFF0097A7)
                        }

                        // Adjust text color based on background color
                        val textColor = if (backgroundColor.luminance() > 0.5) Color.Black else Color.White

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                WeatherIcon(weather.weather[0].icon)
                                Text(
                                    text = "Current temperature: ${weather.main.temp}°${if (unit == "metric") "C" else "F"}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = "Feels like: ${weather.main.feels_like}°${if (unit == "metric") "C" else "F"}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = "Humidity: ${weather.main.humidity}%",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = "Pressure: ${weather.main.pressure} hPa",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = "Wind Speed: ${weather.wind.speed} ${if (unit == "metric") "m/s" else "mph"}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = "Weather: ${weather.weather[0].description.capitalize()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = getMessageForWeather(weather.weather[0].description),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red // Set the message color to black
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        text = "7-Day Forecast",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Limit forecast to 7 days
                items(forecastWeather.value?.take(7)?.size ?: 0) { index ->
                    forecastWeather.value?.let { forecast ->
                        val nextDay = currentDate.plusDays(index.toLong() + 1)
                        val nextDayName = nextDay.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        val month = nextDay.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        val dayOfMonth = nextDay.dayOfMonth

                        DayForecastItem(dayName = "$nextDayName, $dayOfMonth $month", forecastItem = forecast[index], unit)
                    }
                }

                item {
                    errorMessage.value?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = it,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherIcon(weatherIconCode: String) {
    val iconUrl = "https://openweathermap.org/img/wn/${weatherIconCode}@2x.png"

    Image(
        painter = rememberImagePainter(iconUrl),
        contentDescription = null,
        modifier = Modifier.size(64.dp)
    )
}

@Composable
fun DayForecastItem(dayName: String, forecastItem: ForecastItem, unit: String) {
    // Determine background color based on weather condition
    val backgroundColor = when {
        forecastItem.weather[0].description.contains("rain", true) -> Color(0xFF80D8FF)
        forecastItem.weather[0].description.contains("cloud", true) -> Color(0xFFB3E5FC)
        forecastItem.weather[0].description.contains("clear", true) -> Color(0xFFFFF176)
        forecastItem.weather[0].description.contains("snow", true) -> Color(0xFFE1F5FE)
        else -> Color(0xFF0097A7)
    }

    // Adjust text color based on background color
    val textColor = if (backgroundColor.luminance() > 0.5) Color.Black else Color.White

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            WeatherIcon(forecastItem.weather[0].icon)
            Text(
                text = dayName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Temperature: ${forecastItem.main.temp}°${if (unit == "metric") "C" else "F"}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "Feels like: ${forecastItem.main.feels_like}°${if (unit == "metric") "C" else "F"}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "Humidity: ${forecastItem.main.humidity}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "Pressure: ${forecastItem.main.pressure} hPa",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "Wind Speed: ${forecastItem.wind.speed} ${if (unit == "metric") "m/s" else "mph"}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "Weather: ${forecastItem.weather[0].description.capitalize()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getMessageForWeather(forecastItem.weather[0].description),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red // Set the message color to black
            )
        }
    }
}

fun getMessageForWeather(description: String): String {
    return when {
        description.contains("rain", true) -> "Don't forget your umbrella!"
        description.contains("cloud", true) -> "It might be a bit cloudy today."
        description.contains("clear", true) -> "Clear skies ahead, enjoy the sun!"
        description.contains("snow", true) -> "Snowy weather, stay warm!"
        else -> "Have a great day!"
    }
}
