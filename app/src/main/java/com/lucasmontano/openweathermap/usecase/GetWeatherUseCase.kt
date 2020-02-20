package com.lucasmontano.openweathermap.usecase

import com.google.gson.Gson
import com.lucasmontano.openweathermap.model.dto.CityNetworkDto
import com.lucasmontano.openweathermap.service.network.WeatherApiService

class GetWeatherUseCase(private val weatherApiService: WeatherApiService) {

    suspend fun execute(lat: Double, lon: Double): CityNetworkDto {
        val response = weatherApiService.byGeoCoordinates(lat, lon)
        return Gson().fromJson(response, CityNetworkDto::class.java)
    }
}
