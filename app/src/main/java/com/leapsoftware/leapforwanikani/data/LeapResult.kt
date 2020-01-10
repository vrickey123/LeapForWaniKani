package com.leapsoftware.leapforwanikani.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class LeapResult<out R> {
    data class Success<out T>(val resultData: T) : LeapResult<T>()
    data class Error(val exception: Exception) : LeapResult<Nothing>()
    object Loading : LeapResult<Nothing>()
    object Offline : LeapResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "ApiSuccess[responseData=$resultData]"
            is Error -> "ApiError[exception=$exception]"
            Loading -> "Loading"
            Offline -> "NoConnection"
        }
    }
}

/**
 * `true` if [LeapResult] is of type [Success] & holds non-null [Success.data].
 */
val LeapResult<*>.succeeded
    get() = this is LeapResult.Success && resultData != null