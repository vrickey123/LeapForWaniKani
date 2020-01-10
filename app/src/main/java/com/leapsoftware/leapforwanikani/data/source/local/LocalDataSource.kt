package com.leapsoftware.leapforwanikani.data.source.local

import com.leapsoftware.leapforwanikani.data.LeapResult
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import com.leapsoftware.leapforwanikani.data.source.remote.api.types.WKSrsStageType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSource internal constructor(
    private val waniKaniDatabase: WaniKaniDatabase,
    private val wkReportDao: WKReportDao,
    private val wkResourceDao: WKResourceDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): WKLocalDataSource {

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: LocalDataSource? = null

        fun getInstance(waniKaniDatabase: WaniKaniDatabase,
                        ioDispatcher: CoroutineDispatcher): LocalDataSource {
            return instance ?: synchronized(this) {
                instance ?: LocalDataSource(
                    waniKaniDatabase,
                    waniKaniDatabase.wkReportDao(),
                    waniKaniDatabase.wkResourceDao(),
                    ioDispatcher
                ).also { instance = it }
            }
        }
    }

    override suspend fun getSummary(): LeapResult<WKReport.Summary> =
        withContext(ioDispatcher) {
            try {
                val summary = wkReportDao.getSummary()
                if (summary != null) {
                    return@withContext LeapResult.Success(summary)
                } else {
                    return@withContext LeapResult.Error(Exception("Summary not found!"))
                }
            } catch (e: Exception) {
                return@withContext LeapResult.Error(e)
            }
        }

    override suspend fun saveSummary(summary: WKReport.Summary) =
        withContext(ioDispatcher) {
            wkReportDao.insertSummary(summary)
        }

    override suspend fun getAssignments(): LeapResult<List<WKReport.WKResource.Assignment>> =
        withContext(ioDispatcher) {
            try {
                val assignments = wkResourceDao.getAllAssignments()
                return@withContext LeapResult.Success(assignments)
            } catch (e: Exception) {
                return@withContext LeapResult.Error(e)
            }
        }

    override suspend fun saveAssignments(assignments: List<WKReport.WKResource.Assignment>) =
        withContext(ioDispatcher) {
            wkResourceDao.insertAssignments(assignments)
        }

    override suspend fun getCountAssignmentsBySrsStage(stage: WKSrsStageType): LeapResult<Int> =
        withContext(ioDispatcher) {
            try {
                val count = when (stage) {
                    WKSrsStageType.initiate -> wkResourceDao.countAssignmentsByInitiateSrsStage()
                    WKSrsStageType.apprentice -> wkResourceDao.countAssignmentsByApprenticeSrsStage()
                    WKSrsStageType.guru -> wkResourceDao.countAssignmentsByGuruSrsStage()
                    WKSrsStageType.master -> wkResourceDao.countAssignmentsByMasterSrsStage()
                    WKSrsStageType.enlightened -> wkResourceDao.countAssignmentsByEnlightenedSrsStage()
                    WKSrsStageType.burned -> wkResourceDao.countAssignmentsByBurnedSrsStage()
                }
                return@withContext LeapResult.Success(count)
            } catch (e: Exception) {
                return@withContext LeapResult.Error(e)
            }
        }

    override suspend fun getUser(): LeapResult<WKReport.User> =
        withContext(ioDispatcher) {
            try {
                val user = wkReportDao.getUser()
                if (user != null) {
                    return@withContext LeapResult.Success(user)
                } else {
                    return@withContext LeapResult.Error(Exception("User not found!"))
                }
            } catch (e: Exception) {
                return@withContext LeapResult.Error(e)
            }
        }

    override suspend fun saveUser(user: WKReport.User) =
        withContext(ioDispatcher) {
            wkReportDao.insertUser(user)
        }

    override suspend fun clearCache() =
        withContext(ioDispatcher) {
            waniKaniDatabase.clearAllTables()
        }

}