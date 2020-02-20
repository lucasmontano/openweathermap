package com.lucasmontano.openweathermap.model.dto

import com.google.gson.annotations.SerializedName

data class CityNetworkDto(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("coord")
    var coord: CoordNetworkDto,
    @SerializedName("main")
    var forecastMain: ForecastMainNetworkDto,
    @SerializedName("dt")
    var unixDateTime: Long,
    @SerializedName("wind")
    var wind: WindNetworkDto?,
    @SerializedName("rain")
    var rain: RainNetworkDto?,
    @SerializedName("snow")
    var snow: SnowNetworkDto?,
    @SerializedName("weather")
    var weather: List<WeatherNetworkDto>,
    @SerializedName("sys")
    var sys: ApiSysObjectNetworkDto?,
    @SerializedName("clouds")
    var clouds: CloudsNetworkDto?
)
