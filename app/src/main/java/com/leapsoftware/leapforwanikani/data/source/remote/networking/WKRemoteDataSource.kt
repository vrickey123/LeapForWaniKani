package com.leapsoftware.leapforwanikani.data.source.remote.networking

import com.leapsoftware.leapforwanikani.networking.WKApiResponse
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKCollection
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport

interface WKRemoteDataSource {

    suspend fun getSummaryAsync(updatedAfter: Long): WKApiResponse<WKReport.Summary>

    suspend fun getAssignmentsAsync(pageAfterId: Int): WKApiResponse<WKCollection<WKReport.WKResource.Assignment>>

    suspend fun getUserAsync(): WKApiResponse<WKReport.User>

    fun clearCache()

}