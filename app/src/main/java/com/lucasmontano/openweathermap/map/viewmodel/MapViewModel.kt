package com.lucasmontano.openweathermap.map.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasmontano.openweathermap.model.domain.LocationWeatherModel
import com.lucasmontano.openweathermap.model.dto.CityNetworkDto
import com.lucasmontano.openweathermap.usecase.AddBookmarkUseCase
import com.lucasmontano.openweathermap.usecase.GetBookmarksUseCase
import com.lucasmontano.openweathermap.usecase.GetWeatherUseCase
import com.lucasmontano.openweathermap.usecase.RemoveBookmarkUseCase
import kotlinx.coroutines.launch

class MapViewModel(
    private var getWeatherUseCase: GetWeatherUseCase,
    private var addBookmarkUseCase: AddBookmarkUseCase,
    private var removeBookmarkUseCase: RemoveBookmarkUseCase,
    private var getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel() {

    var pinWeatherLiveData = MutableLiveData<LocationWeatherModel>()
    var bookmarksWeatherLiveData = MutableLiveData<List<LocationWeatherModel>>()

    fun refreshPinForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            getWeatherUseCase.execute(lat, lon).let {
                pinWeatherLiveData.value = it.toUiModel()
            }
        }
    }

    fun bookmarkLocation(locationWeather: LocationWeatherModel) {
        viewModelScope.launch {
            addBookmarkUseCase.execute(locationWeather)
            loadBookmarks()
        }
    }

    fun removeBookMark(locationWeather: LocationWeatherModel) {
        viewModelScope.launch {
            removeBookmarkUseCase.execute(locationWeather)
            loadBookmarks()
        }
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            bookmarksWeatherLiveData.value = getBookmarksUseCase.execute()
                .map { bookmark ->
                    getWeatherUseCase
                        .execute(bookmark.latitude, bookmark.longitude)
                        .toUiModel()
                }
        }
    }

    private fun CityNetworkDto.toUiModel() = LocationWeatherModel(
        id = this.id,
        name = this.name,
        lat = this.coord.lat,
        lon = this.coord.lon,
        threeHourlyRainVolume = this.rain?.threeHourlyVolume ?: 0.toDouble(),
        threeHourlySnowVolume = this.snow?.threeHourlyVolume ?: 0.toDouble(),
        weatherDescription = this.weather.firstOrNull()?.description,
        windSpeed = this.wind?.speed,
        temp = this.forecastMain.temp.toInt(),
        tempMax = this.forecastMain.tempMax.toInt(),
        tempMin = this.forecastMain.tempMin.toInt(),
        humidity = this.forecastMain.humidity,
        pressure = this.forecastMain.pressure,
        feelsLike = this.forecastMain.feelsLike.toInt(),
        clouds = this.clouds?.all ?: 0
    )
}
