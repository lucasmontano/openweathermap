package com.lucasmontano.openweathermap.model.dto

import com.google.gson.annotations.SerializedName

data class RainNetworkDto (
    @SerializedName("3h")
    val threeHourlyVolume: Double
)
