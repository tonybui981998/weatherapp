package com.example.nzweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nzweather.network.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    val currentWeather: MutableLiveData<WeatherResponse> = MutableLiveData()
    val forecastWeather: MutableLiveData<List<ForecastItem>> = MutableLiveData()
    val uvIndex: MutableLiveData<Float> = MutableLiveData() // Float for the UV index value
    val uvDescription: MutableLiveData<String> = MutableLiveData() // For the UV index description
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun getWeatherData(city: String, unit: String = "metric") { // Added 'unit' parameter
        viewModelScope.launch {
            val apiKey = "12c326de0fa3044b114bc7d650af006a"

            // Fetch current weather
            val weatherResponse = RetrofitInstance.api.getCurrentWeather(city, unit, apiKey)
            weatherResponse.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        currentWeather.postValue(response.body())
                        response.body()?.let { weather ->
                            // Fetch UV index based on coordinates
                            getUVIndex(weather.coord.lat, weather.coord.lon, apiKey)
                        }
                    } else {
                        errorMessage.postValue("Failed to retrieve weather data: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    errorMessage.postValue(t.message)
                }
            })

            // Fetch 7-day forecast weather
            val forecastResponse = RetrofitInstance.api.getWeatherForecast(city, unit, apiKey) // Using 'unit' here as well
            forecastResponse.enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                    if (response.isSuccessful) {
                        forecastWeather.postValue(response.body()?.list)
                    } else {
                        errorMessage.postValue("Failed to retrieve forecast data: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    errorMessage.postValue(t.message)
                }
            })
        }
    }

    private fun getUVIndex(lat: Double, lon: Double, apiKey: String) {
        val uvIndexResponse = RetrofitInstance.api.getUVIndex(lat, lon, apiKey)
        uvIndexResponse.enqueue(object : Callback<UVIndexResponse> {
            override fun onResponse(call: Call<UVIndexResponse>, response: Response<UVIndexResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { uvData ->
                        uvIndex.postValue(uvData.value ?: 0f)  // Safely handle nulls by defaulting to 0.0f
                        uvDescription.postValue(uvData.description)
                    }
                } else {
                    errorMessage.postValue("Failed to retrieve UV index: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UVIndexResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }
}
