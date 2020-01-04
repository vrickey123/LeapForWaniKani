package com.leapsoftware.leapforwanikani.data.source.local

import com.leapsoftware.leapforwanikani.data.LeapResult
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import com.leapsoftware.leapforwanikani.data.source.remote.api.types.WKSrsStageType

interface WKLocalDataSource {

    suspend fun getSummary(): LeapResult<WKReport.Summary>

    suspend fun saveSummary(summary: WKReport.Summary)

    suspend fun getAssignments(): LeapResult<List<WKReport.WKResource.Assignment>>

    suspend fun saveAssignments(assignments: List<WKReport.WKResource.Assignment>)

    suspend fun getCountAssignmentsBySrsStage(stage: WKSrsStageType): LeapResult<Int>

    suspend fun getUser(): LeapResult<WKReport.User>

    suspend fun saveUser(user: WKReport.User)

    suspend fun clearCache()

}