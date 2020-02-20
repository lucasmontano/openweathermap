package com.lucasmontano.openweathermap.core.di

import com.lucasmontano.openweathermap.core.network.VolleySingleton
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single {
        VolleySingleton(
            androidApplication()
        )
    }
}
