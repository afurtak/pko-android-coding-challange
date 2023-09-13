package com.furtak.movielist.data.http

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

val retrofit: Retrofit get() = Retrofit.Builder()
    .baseUrl(TMDB_BASE_URL)
    .addConverterFactory(provideKotlinSerialization)
    .client(okHttpClient)
    .build()

private val provideKotlinSerialization: Converter.Factory get() {
    val contentType = "application/json".toMediaType()
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    return json.asConverterFactory(contentType)
}

private val okHttpClient: OkHttpClient get() = OkHttpClient.Builder()
    .addInterceptor(TMDBAuthInterceptor())
    .build()
