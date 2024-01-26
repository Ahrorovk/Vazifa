package com.tj.vazifa.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApplicationApi {
    @GET("getStatus")
    fun getData(
        @Header("X-AppApiToken") token: String = "WE85eldUalVLVzVIdXpQelIzTk91bEtPSlRiWU9GVXc="
    ): Call<StatusResponse>
}