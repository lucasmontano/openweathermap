package com.lucasmontano.openweathermap.core.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton(context: Context) {

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    companion object {

        @Volatile
        private var INSTANCE: VolleySingleton? = null

        fun getInstance(context: Context) = INSTANCE
            ?: synchronized(this) {
            INSTANCE
                ?: VolleySingleton(
                    context
                ).also {
                INSTANCE = it
            }
        }
    }
}
