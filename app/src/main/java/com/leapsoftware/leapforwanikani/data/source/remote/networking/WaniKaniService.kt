package com.leapsoftware.leapforwanikani.networking

import android.content.Context
import android.util.Log
import com.leapsoftware.leapforwanikani.data.source.WaniKaniRepository
import com.leapsoftware.leapforwanikani.data.source.remote.WANIKANI_BASE_URL
import com.leapsoftware.leapforwanikani.data.source.remote.WANIKANI_JSON_KEY_OBJECT
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKCollection
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import com.leapsoftware.leapforwanikani.data.source.remote.api.types.WKReportType
import com.leapsoftware.leapforwanikani.data.source.remote.api.types.WKResourceType
import com.leapsoftware.leapforwanikani.data.source.remote.networking.WKRemoteDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.UnknownHostException
import java.util.*

class WaniKaniService private constructor(
    context: Context
) : WKRemoteDataSource {

    private val TAG by lazy { WaniKaniRepository::class.java.simpleName }

    private val CACHE_SIZE_BYTES: Long = 1024 * 1024 * 2 // 2 MB

    private val moshiKotlin = buildMoshi()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(context.cacheDir, CACHE_SIZE_BYTES))
        .addInterceptor(WKApiKeyRequestInterceptor(context))
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit = buildRetrofit(moshiKotlin, okHttpClient).build()

    private val waniKaniApi: WaniKaniApi = retrofit.create(WaniKaniApi::class.java)

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: WaniKaniService? = null

        fun getInstance(context: Context) =
                instance ?: synchronized(this) {
                    instance ?: WaniKaniService(context)
                        .also { instance = it }
                }

        fun buildMoshi() : Moshi {
            return Moshi.Builder()
                .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()) // Support null safe for dates by default
                .add(PolymorphicJsonAdapterFactory.of(WKReport.WKResource::class.java, WANIKANI_JSON_KEY_OBJECT)
                    .withSubtype(WKReport.WKResource.Assignment::class.java, WKResourceType.assignment.name))
                .add(PolymorphicJsonAdapterFactory.of(WKReport::class.java, WANIKANI_JSON_KEY_OBJECT)
                    .withSubtype(WKReport.Summary::class.java, WKReportType.report.name))
                .add(KotlinJsonAdapterFactory()) // Order matters! Place Kotlin adapter last.
                .build()
        }

        fun buildRetrofit(moshiKotlin :Moshi, okHttpClient: OkHttpClient) : Retrofit.Builder {
            return buildRetrofit(moshiKotlin)
                .client(okHttpClient)
        }

        fun buildRetrofit(moshiKotlin :Moshi) : Retrofit.Builder {
            return Retrofit.Builder()
                .baseUrl(WANIKANI_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshiKotlin))
        }
    }

    override suspend fun getSummaryAsync(updatedAfter: Long): WKApiResponse<WKReport.Summary> {
        return try {
            WKApiResponse.create(waniKaniApi.getSummaryAsync(updatedAfter))
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Exception getting summary" + e.message)
            WKApiResponse.offline()
        }
    }

    override suspend fun getAssignmentsAsync(pageAfterId: Int): WKApiResponse<WKCollection<WKReport.WKResource.Assignment>> {
        return try {
            WKApiResponse.create(waniKaniApi.getAssignmentsAsync(pageAfterId))
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Exception getting assignments" + e.message)
            WKApiResponse.offline()
        }
    }

    override suspend fun getUserAsync(): WKApiResponse<WKReport.User> {
        return try {
            WKApiResponse.create(waniKaniApi.getUserAsync())
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Exception getting user" + e.message)
            WKApiResponse.offline()
        }
    }

    override fun clearCache() {
        okHttpClient.cache()?.evictAll()
    }

}