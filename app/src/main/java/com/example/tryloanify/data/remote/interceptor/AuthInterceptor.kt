package com.example.tryloanify.data.remote.interceptor

import com.example.tryloanify.data.local.prefs.SecurePrefs
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val securePrefs: SecurePrefs,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = securePrefs.accessToken
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
