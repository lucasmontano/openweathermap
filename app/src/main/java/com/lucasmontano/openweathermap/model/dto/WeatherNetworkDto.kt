package com.lucasmontano.openweathermap.model.dto

import com.google.gson.annotations.SerializedName

data class WeatherNetworkDto(
    @SerializedName("id")
    var id: Int,
    @SerializedName("main")
    var main: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("icon")
    var icon: String
)
