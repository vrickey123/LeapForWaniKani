package com.leapsoftware.leapforwanikani.data.source

import com.leapsoftware.leapforwanikani.networking.WKApiResponse
import com.leapsoftware.leapforwanikani.data.LeapResult
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import com.leapsoftware.leapforwanikani.data.source.remote.api.types.WKSrsStageType

interface WKRepository {

    suspend fun getSummary(updatedAfter: Long): LeapResult<WKReport.Summary>

    suspend fun getSummaryRemote(updatedAfter: Long): WKApiResponse<WKReport.Summary>

    suspend fun refreshLocalSummary(summary: WKReport.Summary)

    suspend fun getAssignments(pageAfterId: Int): LeapResult<List<WKReport.WKResource.Assignment>>

    suspend fun getCountAssignmentsBySrsStage(stage: WKSrsStageType): LeapResult<Int>

    suspend fun getUser(): LeapResult<WKReport.User>

    suspend fun clearCache()

}