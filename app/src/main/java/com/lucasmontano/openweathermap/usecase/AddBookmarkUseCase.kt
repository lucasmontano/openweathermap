package com.lucasmontano.openweathermap.usecase

import com.lucasmontano.openweathermap.core.database.dao.BookmarkDao
import com.lucasmontano.openweathermap.core.database.entities.Bookmark
import com.lucasmontano.openweathermap.model.domain.LocationWeatherModel

class AddBookmarkUseCase(private val dao: BookmarkDao) {

    suspend fun execute(locationWeather: LocationWeatherModel) = dao.insert(
        Bookmark(
            id = locationWeather.id,
            latitude = locationWeather.lat,
            longitude = locationWeather.lon
        )
    )
}
