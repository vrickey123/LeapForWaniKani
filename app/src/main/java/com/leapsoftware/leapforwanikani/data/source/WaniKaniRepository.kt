package com.leapsoftware.leapforwanikani.data.source

import android.app.AuthenticationRequiredException
import android.content.Context
import android.util.Log
import com.leapsoftware.leapforwanikani.networking.WKApiResponse
import com.leapsoftware.leapforwanikani.networking.WaniKaniService
import com.leapsoftware.leapforwanikani.data.LeapResult
import com.leapsoftware.leapforwanikani.data.source.local.LocalDataSource
import com.leapsoftware.leapforwanikani.data.source.local.WKLocalDataSource
import com.leapsoftware.leapforwanikani.data.source.local.WaniKaniDatabase
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKCollection
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport
import com.leapsoftware.leapforwanikani.data.source.remote.api.types.WKSrsStageType
import com.leapsoftware.leapforwanikani.data.source.remote.exceptions.AuthenticationException
import com.leapsoftware.leapforwanikani.data.source.remote.networking.WKRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WaniKaniRepository(
    private val wkRemoteDataSource: WKRemoteDataSource,
    private val wkLocalDataSource: WKLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): WKRepository {

    private val TAG by lazy { WaniKaniRepository::class.java.simpleName }

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: WaniKaniRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                val waniKaniService = WaniKaniService.getInstance(context)
                val waniKaniDatabase = WaniKaniDatabase.getInstance(context)
                val localDataSource = LocalDataSource.getInstance(
                    waniKaniDatabase,
                    ioDispatcher = Dispatchers.IO
                )
                instance ?: WaniKaniRepository(waniKaniService, localDataSource)
                    .also { instance = it }
            }

        fun createException(code: Int, defaultMessage: String): Exception {
            return when (code) {
                WaniKaniService.code_unauthorized -> AuthenticationException()
                else -> Exception(defaultMessage)
            }
        }
    }

    override suspend fun getSummary(updatedAfter: Long): LeapResult<WKReport.Summary> {
        return withContext(ioDispatcher) {
            return@withContext fetchSummaryRemoteOrLocal(updatedAfter)
        }
    }

    override suspend fun getSummaryRemote(updatedAfter: Long): WKApiResponse<WKReport.Summary> {
        return withContext(ioDispatcher) {
            return@withContext wkRemoteDataSource.getSummaryAsync(updatedAfter)
        }
    }

    override suspend fun refreshLocalSummary(summary: WKReport.Summary) {
        withContext(ioDispatcher) {
            wkLocalDataSource.saveSummary(summary)
        }
    }

    override suspend fun getAssignments(pageAfterId: Int): LeapResult<List<WKReport.WKResource.Assignment>> {
        return withContext(ioDispatcher) {
            return@withContext fetchAssignmentsRemoteOrLocal(pageAfterId)
        }
    }

    override suspend fun getCountAssignmentsBySrsStage(stage: WKSrsStageType): LeapResult<Int> {
        return withContext(ioDispatcher) {
            return@withContext wkLocalDataSource.getCountAssignmentsBySrsStage(stage)
        }
    }

    override suspend fun getUser(): LeapResult<WKReport.User> {
        return withContext(ioDispatcher) {
            return@withContext fetchUserRemoteOrLocal()
        }
    }

    override suspend fun clearCache() {
        withContext(ioDispatcher) {
            wkRemoteDataSource.clearCache()
            wkLocalDataSource.clearCache()
        }
    }

    private suspend fun fetchSummaryRemoteOrLocal(updatedAfter: Long): LeapResult<WKReport.Summary> {
        val remoteSummary = wkRemoteDataSource.getSummaryAsync(updatedAfter)
        when (remoteSummary) {
            is WKApiResponse.ApiError -> {
                Log.w(TAG, "Remote summary source fetch failed")
                // Local if remote fails
                val localSummary = getSummaryFromLocal()
                if (localSummary is LeapResult.Success) return localSummary
                val exception = createException(remoteSummary.code, "ApiError fetching summary from remote and local")
                return LeapResult.Error(exception)
            }
            is WKApiResponse.ApiNotModified -> {
                Log.d(TAG, "Remote summary not modified. Returning local.")
                return getSummaryFromLocal()
            }
            is WKApiResponse.ApiSuccess -> {
                Log.d(TAG, "Remote summary success. Returning latest remote.")
                refreshLocalSummary(remoteSummary.responseData)
                return LeapResult.Success(remoteSummary.responseData)
            }
            is WKApiResponse.NoConnection -> {
                Log.e(TAG, "No connection. Could not fetch fresh summary.")
                return LeapResult.Offline
            }
            else -> throw IllegalStateException()
        }
    }

    private suspend fun getSummaryFromLocal(): LeapResult<WKReport.Summary> {
        return wkLocalDataSource.getSummary()
    }

    private suspend fun fetchAssignmentsRemoteOrLocal(pageAfterId: Int): LeapResult<List<WKReport.WKResource.Assignment>> {
        val remoteAssignments: WKApiResponse<WKCollection<WKReport.WKResource.Assignment>> =
            wkRemoteDataSource.getAssignmentsAsync(pageAfterId)

        when (remoteAssignments) {
            is WKApiResponse.ApiError -> {
                Log.w(TAG, "Remote assignments source fetch failed")
                // Local if remote fails
                val localAssignments = getAssignmentsFromLocal()
                if (localAssignments is LeapResult.Success) return localAssignments
                val exception = createException(remoteAssignments.code, "ApiError fetching summary from remote and local")
                return LeapResult.Error(exception)
            }
            is WKApiResponse.ApiNotModified -> {
                Log.d(TAG, "Remote assignments not modified. Returning local.")
                return getAssignmentsFromLocal()
            }
            is WKApiResponse.ApiSuccess -> {
                Log.d(TAG, "Remote assignments success. Returning latest remote.")
                refreshLocalAssignments(remoteAssignments.responseData.data)
                return LeapResult.Success(remoteAssignments.responseData.data)
            }
            is WKApiResponse.NoConnection -> {
                Log.e(TAG, "No connection. Could not fetch fresh assignments.")
                return LeapResult.Offline
            }
            else -> throw IllegalStateException()
        }
    }

    private suspend fun getAssignmentsFromLocal(): LeapResult<List<WKReport.WKResource.Assignment>> {
        return wkLocalDataSource.getAssignments()
    }

    private suspend fun refreshLocalAssignments(assigments: List<WKReport.WKResource.Assignment>) {
        wkLocalDataSource.saveAssignments(assigments)
    }

    private suspend fun fetchUserRemoteOrLocal(): LeapResult<WKReport.User> {
        val remoteUser = wkRemoteDataSource.getUserAsync()
        when (remoteUser) {
            is WKApiResponse.ApiError -> {
                Log.w(TAG, "Remote user responseData source fetch failed")
                // Local if remote fails
                val localUser = getUserFromLocal()
                if (localUser is LeapResult.Success) return localUser
                val exception = createException(remoteUser.code, "ApiError fetching user from remote and local")
                return LeapResult.Error(exception)
            }
            is WKApiResponse.ApiNotModified -> {
                Log.d(TAG, "Remote user not modified. Returning local.")
                return getUserFromLocal()
            }
            is WKApiResponse.ApiSuccess -> {
                Log.d(TAG, "Remote user success. Returning latest remote.")
                refreshLocalUser(remoteUser.responseData)
                return LeapResult.Success(remoteUser.responseData)
            }
            is WKApiResponse.NoConnection -> {
                Log.e(TAG, "No connection. Could not fetch fresh user.")
                return LeapResult.Offline
            }
            else -> throw IllegalStateException()
        }
    }

    private suspend fun getUserFromLocal(): LeapResult<WKReport.User> {
        return wkLocalDataSource.getUser()
    }

    suspend fun refreshLocalUser(user: WKReport.User) {
        withContext(ioDispatcher) {
            wkLocalDataSource.saveUser(user)
        }
    }

}