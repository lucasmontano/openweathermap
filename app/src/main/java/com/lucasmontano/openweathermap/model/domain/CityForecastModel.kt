package com.lucasmontano.openweathermap.model.domain

data class CityForecastModel(
    var name: String,
    val lat: Double,
    val lon: Double,
    val threeHourlyRainVolume: Double?,
    var threeHourlySnowVolume: Double?,
    var windSpeed: Double?,
    var weatherDescription: String?
)
