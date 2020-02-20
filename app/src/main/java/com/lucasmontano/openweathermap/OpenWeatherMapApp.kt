package com.lucasmontano.openweathermap

import android.app.Application
import com.android.volley.RequestQueue
import com.lucasmontano.openweathermap.core.di.appModule
import com.lucasmontano.openweathermap.core.network.VolleySingleton
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class OpenWeatherMapApp : Application() {

    override fun onCreate() {
        super.onCreate()
        volleyRequestQue = VolleySingleton.getInstance(applicationContext).requestQueue

        startKoin {
            androidContext(this@OpenWeatherMapApp)
            modules(appModule)
        }
    }

    companion object {
        lateinit var volleyRequestQue: RequestQueue
    }
}
