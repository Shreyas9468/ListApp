package com.example.listapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BaseURL = "https://maps.googleapis.com/"

    fun create(): GeocodingApiService {
        val retrofit =
            Retrofit.Builder().baseUrl(BaseURL).
            addConverterFactory(GsonConverterFactory.create()).build()

        return retrofit.create(GeocodingApiService::class.java)
    }

}