package com.leapsoftware.leapforwanikani.networking

import android.content.Context
import com.leapsoftware.leapforwanikani.utils.PreferencesManager
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class WKApiKeyRequestInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val apiKey = PreferencesManager.getApiKey(context)

        val headers: Headers = Headers.Builder()
            .add("Authorization", "Bearer $apiKey")
            .add("Wanikani-Revision", "20170710")
            .build()

        val requestWithHeaders: Request = originalRequest.newBuilder()
            .headers(headers)
            .build()

        val response: Response = chain.proceed(requestWithHeaders)

        return response
    }
}