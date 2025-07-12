package com.example.cs446_fit4me.network

import android.content.Context
import android.util.Log
import com.example.cs446_fit4me.datastore.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.getToken(context)

        Log.d("JWT_DEBUG", "Token in interceptor: $token")

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {

            requestBuilder.addHeader("Authorization", token)
            Log.d("JWT_DEBUG", "Authorization header set with token: $token")
        } else {
            Log.w("JWT_DEBUG", "Token is null or blank — Authorization header not set.")
        }

        return chain.proceed(requestBuilder.build())
    }
}