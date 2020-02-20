package com.lucasmontano.openweathermap.service.network

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.lucasmontano.openweathermap.OpenWeatherMapApp
import com.lucasmontano.openweathermap.core.network.ApiUrls
import org.json.JSONObject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WeatherApiService {

    suspend fun byGeoCoordinates(lat: Double, lon: Double) =
        suspendCoroutine<String> { cont ->
            val url = ApiUrls.coordinates.format(lat.toString(), lon.toString())
            val cache = OpenWeatherMapApp.volleyRequestQue.cache
            val entry = cache.get(url)
            if (entry != null) {
                cont.resume(String(entry.data, Charsets.UTF_8))
            } else {
                //Make network call and invalidate
                OpenWeatherMapApp.volleyRequestQue.cache.invalidate(url, true)
                val request = RequestWithHeaders(Request.Method.GET, url, null, cont)
                OpenWeatherMapApp.volleyRequestQue.add(request)
            }
        }

    private class RequestWithHeaders(
        method: Int,
        url: String,
        postObj: JSONObject?,
        continuation: Continuation<String>
    ) : JsonObjectRequest(
        method,
        url,
        postObj,
        Response.Listener {
            continuation.resume(it.toString())
        },
        Response.ErrorListener {
            // TODO handle connection errors
        }) {
        //TODO override headers here for example auth
    }
}
