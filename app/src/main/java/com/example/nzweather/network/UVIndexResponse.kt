package com.example.nzweather.network

data class UVIndexResponse(
    val value: Float?,       // UV index value as a Float
    val description: String  // Description or category of the UV index (e.g., "Low", "Moderate", etc.)
)