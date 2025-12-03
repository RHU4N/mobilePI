package com.example.ceos

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val log = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder().addInterceptor(log).build()
    // Auth API (loginAPI)
    private const val AUTH_BASE = "https://loginapiceos.onrender.com/"
    val authApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_BASE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Math API (mathApi)
    private const val MATH_BASE = "https://mathapi.onrender.com/"
    val mathApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MATH_BASE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}