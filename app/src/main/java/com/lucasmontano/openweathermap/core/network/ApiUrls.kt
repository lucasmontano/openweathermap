package com.lucasmontano.openweathermap.core.network

class ApiUrls {
    companion object {
        private var baseUrl = "http://api.openweathermap.org/"
        private var appId = "&APPID=c6e381d8c7ff98f0fee43775817cf6ad"

        var coordinates = baseUrl + "data/2.5/weather?lat=%s&lon=%s" + appId
    }
}