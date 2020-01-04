package com.leapsoftware.leapforwanikani.networking

import android.util.Log
import retrofit2.Response
import java.net.HttpURLConnection

/**
 * Call adapters and suspend functions with Retrofit
 * https://stackoverflow.com/questions/56483235/how-to-create-a-call-adapter-for-suspending-functions-in-retrofit
 *
 * E-tags for not modified responses:
 * https://android.jlelse.eu/reducing-your-networking-footprint-with-okhttp-etags-and-if-modified-since-b598b8dd81a1
 *
 */
sealed class WKApiResponse<T> {

    companion object {
        private val TAG by lazy { WKApiResponse::class.java.simpleName }
        fun <T> create(response: Response<T>): WKApiResponse<T> {
            return if (response.isSuccessful) {
                if (response.raw().networkResponse() != null &&
                    response.raw().networkResponse()!!.code() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    Log.d(TAG, "Not Modified.")
                    return ApiNotModified(response.raw().message(), HttpURLConnection.HTTP_NOT_MODIFIED)
                }
                val body = response.body()
                // Empty body
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    Log.d(
                        TAG, "WaniKani API Response Successful. Received response at = " +
                                response.raw().receivedResponseAtMillis())
                    Log.d(
                        TAG, "WaniKani API Response Successful. Raw request url = " +
                                response.raw().request().url())
                    Log.d(
                        TAG, "WaniKani API Response Successful. Raw response = " +
                                response.raw())
                    ApiSuccess(body, response.raw().receivedResponseAtMillis(), response.code())
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMessage = if(msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                Log.e(TAG, "WaniKani API Response ApiError. " + errorMessage)
                ApiError(errorMessage ?: "Unknown error", response.code())
            }
        }

        fun <T> offline(): WKApiResponse<T> {
            return NoConnection<T>()
        }
    }

    // Since WaniKani's resultData structure returns object.resultData, our wrapper field will be named "responseData"
    class ApiSuccess<T>(val responseData: T, val receivedResponseAtMillis: Long, val code: Int): WKApiResponse<T>()
    class ApiEmptyResponse<T>: WKApiResponse<T>()
    class ApiNotModified<T>(val message: String, val code: Int) : WKApiResponse<T>()
    class ApiError<T>(val errorMessage: String, val code: Int): WKApiResponse<T>()
    class NoConnection<T>: WKApiResponse<T>()

}