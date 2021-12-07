package com.srini.encora.app.Retrofit

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers("Content-Type:application/json")
    @GET("/countries")
    fun signin(): retrofit2.Call<ApiInterface>


}