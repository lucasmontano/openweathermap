package com.lucasmontano.openweathermap.usecase

import com.google.gson.Gson
import com.lucasmontano.openweathermap.core.network.ApiUrls
import com.lucasmontano.openweathermap.core.network.VolleyHelper
import com.lucasmontano.openweathermap.model.dto.CityNetworkDto

class GetWeatherUseCase {

    suspend fun execute(lat: Double, lon: Double): CityNetworkDto {
        val url = ApiUrls.coordinates.format(lat.toString(), lon.toString())
        val response = VolleyHelper.get(url)

        return Gson().fromJson(response, CityNetworkDto::class.java)
    }
}
