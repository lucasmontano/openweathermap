package com.lucasmontano.openweathermap.model.dto

import com.google.gson.annotations.SerializedName

data class ForecastMainNetworkDto(
    @SerializedName("temp")
    var temp: Double,
    @SerializedName("temp_min")
    var tempMin: Double,
    @SerializedName("temp_max")
    var tempMax: Double,
    @SerializedName("pressure")
    var pressure: Int,
    @SerializedName("feels_like")
    var feelsLike: Double,
    @SerializedName("sea_level")
    var seaLevel: Double,
    @SerializedName("grnd_level")
    var groundLevel: Double,
    @SerializedName("humidity")
    var humidity: Int,
    @SerializedName("temp_kf")
    var tempKf: Double
)
