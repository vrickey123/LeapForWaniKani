package com.leapsoftware.leapforwanikani.networking

import com.leapsoftware.leapforwanikani.data.source.remote.WANIKANI_QUERY_PARAM_PAGE_AFTER_ID
import com.leapsoftware.leapforwanikani.data.source.remote.WANIKANI_QUERY_PARAM_UPDATED_AFTER
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKCollection
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.UnknownHostException

interface WaniKaniApi {

    @GET("assignments")
    suspend fun getAssignmentsAsync(@Query(WANIKANI_QUERY_PARAM_PAGE_AFTER_ID) pageAfterId: Int):
            Response<WKCollection<WKReport.WKResource.Assignment>>

    /**
     * E-tags were not always reliable after completing lessons and reviews (when returning from a
     * WebView back to the MainActivity), so we guarantee a fresh summary by using the
     * updatedAfter time to request the latest summary.
     *
     * @param updatedAfter the epoch time used to filter summaries.
     */
    @GET("summary")
    suspend fun getSummaryAsync(@Query(WANIKANI_QUERY_PARAM_UPDATED_AFTER) updatedAfter: Long):
            Response<WKReport.Summary>

    @Throws(UnknownHostException::class)
    @GET("user")
    suspend fun getUserAsync(): Response<WKReport.User>
}