package com.lucasmontano.openweathermap.core.network

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.lucasmontano.openweathermap.OpenWeatherMapApp
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VolleyHelper {

    companion object {

        suspend fun get(url: String, forceRefresh: Boolean = false) =
            suspendCoroutine<String> { cont ->

                val cache = OpenWeatherMapApp.volleyRequestQue.cache
                val entry = cache.get(url)
                if (entry != null && !forceRefresh) {
                    try {
                        cont.resume(String(entry.data, Charsets.UTF_8))
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                        cont.resume(makeJsonException(e))
                    }

                } else {
                    //Make network call and invalidate
                    OpenWeatherMapApp.volleyRequestQue.cache.invalidate(url, true)
                    val request = RequestWithHeaders(Request.Method.GET, url, null, cont)
                    OpenWeatherMapApp.volleyRequestQue.add(request)
                }
            }

        private fun makeJsonException(throwable: Throwable): String {
            return Gson().toJson(BaseNetworkResponse("500", throwable.message))
        }
    }

    data class BaseNetworkResponse(
        @SerializedName("responseCode")
        var responseCode: String,

        @SerializedName("message")
        var message: String?
    ) {
        val isSuccess: Boolean get() = responseCode == "200"
    }

    class RequestWithHeaders(
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
            continuation.resume(makeJsonException(it))
        }) {
        //TODO override headers here for example auth
    }
}
