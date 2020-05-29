package com.leapsoftware.leapforwanikani.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.leapsoftware.leapforwanikani.networking.WKApiResponse
import com.leapsoftware.leapforwanikani.utils.LeapNotificationManager
import com.leapsoftware.leapforwanikani.MainActivity
import com.leapsoftware.leapforwanikani.data.source.WaniKaniRepository
import com.leapsoftware.leapforwanikani.utils.PreferencesManager
import kotlinx.coroutines.coroutineScope
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Makes a periodic network request, managed by the Android OS, every hour to fetch a new summary.
 * Sends a notification if the request successful and a user has new lessons or reviews.
 */
class SummaryNotifyWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val TAG by lazy { SummaryNotifyWorker::class.java.simpleName }

    companion object {
        const val PERIODIC_WORK_REQUEST_UNIQUE_NAME_SUMMARY = "leap_wanikani_periodic_work_summary"

        fun enqueueUniquePeriodicWork(context: Context, existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val notificationPref = PreferencesManager.getNotificationPref(context).toLong()

            val summaryWorkRequest =
                PeriodicWorkRequestBuilder<SummaryNotifyWorker>(notificationPref, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_REQUEST_UNIQUE_NAME_SUMMARY,
                existingPeriodicWorkPolicy,
                summaryWorkRequest
            )
        }
    }

    override suspend fun doWork(): Result = coroutineScope {
        val repository = WaniKaniRepository.getInstance(applicationContext)
        when (val summary = repository.getSummaryRemote(Date().time)) {
            is WKApiResponse.ApiError -> {
                Log.w(TAG, "SummaryWorker error")
                Result.failure()
            }
            is WKApiResponse.ApiNotModified -> {
                Log.d(TAG, "SummaryWorker not modified.")
                Result.success()
            }
            is WKApiResponse.ApiSuccess -> {
                Log.d(TAG, "SummaryWorker success.")
                val requestCode = LeapNotificationManager.getRequestCode(summary.responseData.data)
                LeapNotificationManager.sendNotification(applicationContext, requestCode)
                repository.refreshLocalSummary(summary.responseData)
                Result.success()
            }
            else -> Result.failure()
        }
    }

}