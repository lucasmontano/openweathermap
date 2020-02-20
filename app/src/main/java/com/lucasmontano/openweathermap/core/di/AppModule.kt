package com.lucasmontano.openweathermap.core.di

import androidx.room.Room
import com.lucasmontano.openweathermap.core.database.WeatherDatabase
import com.lucasmontano.openweathermap.core.network.VolleySingleton
import com.lucasmontano.openweathermap.map.viewmodel.MapViewModel
import com.lucasmontano.openweathermap.service.network.WeatherApiService
import com.lucasmontano.openweathermap.usecase.AddBookmarkUseCase
import com.lucasmontano.openweathermap.usecase.GetBookmarksUseCase
import com.lucasmontano.openweathermap.usecase.GetWeatherUseCase
import com.lucasmontano.openweathermap.usecase.RemoveBookmarkUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(androidApplication(), WeatherDatabase::class.java, "WEATHER_DB")
            .build()
    }
    single { get<WeatherDatabase>().bookmarkDao }

    single {
        VolleySingleton(
            androidApplication()
        )
    }

    single {
        WeatherApiService()
    }

    single {
        GetWeatherUseCase(get())
    }

    single {
        AddBookmarkUseCase(get())
    }

    single {
        RemoveBookmarkUseCase(get())
    }

    single {
        GetBookmarksUseCase(get())
    }

    viewModel { MapViewModel(get(), get(), get(), get()) }
}
