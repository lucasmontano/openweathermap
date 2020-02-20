package com.lucasmontano.openweathermap

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class OpenWeatherMapApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}