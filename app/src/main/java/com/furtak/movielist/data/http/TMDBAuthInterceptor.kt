package com.furtak.movielist.data.http

import com.furtak.movielist.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class TMDBAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authRequest = chain.request()
            .newBuilder()
            .addHeader(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
            .build()

        return chain.proceed(authRequest)
    }

    companion object {
        const val AUTHORIZATION_HEADER_NAME = "Authorization"
        const val AUTHORIZATION_HEADER_VALUE = "Bearer ${BuildConfig.TMDB_API_KEY}"
    }
}