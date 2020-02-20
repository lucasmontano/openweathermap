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
                    threeHourlyRainVolume = it.rain?.threeHourlyVolume,
                    threeHourlySnowVolume = it.snow?.threeHourlyVolume,
                    weatherDescription = it.weather.firstOrNull()?.description,
                    windSpeed = it.wind?.speed,
                    temp = it.forecastMain.temp
                )
            }
        }
    }
}
