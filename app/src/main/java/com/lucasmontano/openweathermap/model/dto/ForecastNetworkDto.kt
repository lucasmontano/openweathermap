package com.lucasmontano.openweathermap.model.dto

import com.google.gson.annotations.SerializedName

data class ForecastNetworkDto(
    @SerializedName("dt")
    var unixDateTime: Long,
    @SerializedName("dt_txt")
    var dateTimeString: String,
    @SerializedName("main")
    var main: ForecastMainNetworkDto,
    @SerializedName("weather")
    var weather: List<WeatherNetworkDto>,
    @SerializedName("clouds")
    var clouds: CloudsNetworkDto?,
    @SerializedName("wind")
    var wind: WindNetworkDto?,
    @SerializedName("snow")
    var snow: SnowNetworkDto?,
    @SerializedName("rain")
    var rain: RainNetworkDto?
)
