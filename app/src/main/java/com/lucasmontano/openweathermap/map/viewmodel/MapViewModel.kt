package com.lucasmontano.openweathermap.map.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.lucasmontano.openweathermap.model.domain.CityForecastModel
import com.lucasmontano.openweathermap.usecase.GetWeatherUseCase
import kotlinx.coroutines.launch

class MapViewModel(private var getWeatherUseCase: GetWeatherUseCase): ViewModel() {

    var pinForecastLiveData = MutableLiveData<CityForecastModel>()

    fun refreshPinForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            getWeatherUseCase.execute(lat, lon).let {
                pinForecastLiveData.value = CityForecastModel(
                    name = it.name,
                    lat = it.coord.lat,
                    lon = it.coord.lon,
                    threeHourlyRainVolume = it.rain?.threeHourlyVolume ?: 0.toDouble(),
                    threeHourlySnowVolume = it.snow?.threeHourlyVolume ?: 0.toDouble(),
                    weatherDescription = it.weather.firstOrNull()?.description,
                    windSpeed = it.wind?.speed,
                    temp = it.forecastMain.temp.toInt(),
                    tempMax = it.forecastMain.tempMax.toInt(),
                    tempMin = it.forecastMain.tempMin.toInt(),
                    humidity = it.forecastMain.humidity,
                    pressure = it.forecastMain.pressure,
                    feelsLike = it.forecastMain.feelsLike.toInt(),
                    clouds = it.clouds?.all ?: 0
                )
            }
        }
    }
}
