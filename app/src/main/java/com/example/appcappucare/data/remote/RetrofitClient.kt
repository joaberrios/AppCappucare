package com.example.appcappucare.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val URL_BASE = "http://10.0.2.2:8081/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productoApi: ProductoApi by lazy {
        retrofit.create(ProductoApi::class.java)
    }
}
