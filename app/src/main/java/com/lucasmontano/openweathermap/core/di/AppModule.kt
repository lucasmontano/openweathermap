package com.lucasmontano.openweathermap.core.di

import com.lucasmontano.openweathermap.core.network.VolleySingleton
import com.lucasmontano.openweathermap.map.viewmodel.MapViewModel
import com.lucasmontano.openweathermap.service.network.WeatherApiService
import com.lucasmontano.openweathermap.usecase.GetWeatherUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
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

    viewModel { MapViewModel(get()) }
}
