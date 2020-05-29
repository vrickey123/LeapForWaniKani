package com.leapsoftware.leapforwanikani.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.leapsoftware.leapforwanikani.MainActivity
import com.leapsoftware.leapforwanikani.R
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKData
import java.util.*

class LeapNotificationManager(val context: Context) {

    private val TAG by lazy { LeapNotificationManager::class.java.simpleName }

    companion object {
        const val EXTRAS_REQUEST_CODE = "leap_notification_action_request_code"
        const val REQUEST_CODE_LESSONS = 1
        const val REQUEST_CODE_REVIEWS = 2
        const val REQUEST_CODE_LESSONS_AND_REVIEWS = 3

        private const val NOTIFICATION_CHANNEL_LESSONS_ID = "channel_id_lessons"
        private const val NOTIFICATION_CHANNEL_REVIEWS_ID = "channel_id_reviews"
        private const val NOTIFICATION_CHANNEL_LESSONS_AND_REVIEWS_ID = "channel_id_lessons_reviews"

        private const val NOTIFICATION_ID_LESSONS = 1
        private const val NOTIFICATION_ID_REVIEWS = 2
        private const val NOTIFICATION_ID_LESSONS_AND_REVIEWS = 3

        private const val PENDING_INTENT_LESSONS = 100
        private const val PENDING_INTENT_REVIEWS = 101
        private const val PENDING_INTENT_LESSONS_AND_REVIEWS = 102

        fun getRequestCode(summaryData: WKData.SummaryData): Int {
            return if (summaryData.lessons.isNotEmpty()
                && summaryData.lessons[0].hasAvailableLessons()
                && summaryData.hasAvailableReviews()
            ) {
                REQUEST_CODE_LESSONS_AND_REVIEWS
            } else if (summaryData.lessons.isNotEmpty()
                && summaryData.lessons[0].hasAvailableLessons()
            ) {
                REQUEST_CODE_LESSONS
            } else if (summaryData.hasAvailableReviews()) {
                REQUEST_CODE_REVIEWS
            } else {
                -1
            }
        }

        fun sendNotification(context: Context, requestCode: Int) {
            val intent = Intent(context, MainActivity::class.java)
                .putExtra(EXTRAS_REQUEST_CODE, requestCode)

            var channelId = ""
            var notificationId = -1
            var pendingIntentCode = -1
            var title = ""
            var text = ""

            when (requestCode) {
                REQUEST_CODE_LESSONS -> {
                    channelId = NOTIFICATION_CHANNEL_LESSONS_ID
                    notificationId = NOTIFICATION_ID_LESSONS
                    pendingIntentCode = PENDING_INTENT_LESSONS
                    title = context.getString(R.string.notification_title_lessons)
                    text = context.getString(R.string.notification_text_lessons)
                }
                REQUEST_CODE_REVIEWS -> {
                    channelId = NOTIFICATION_CHANNEL_REVIEWS_ID
                    notificationId = NOTIFICATION_ID_REVIEWS
                    pendingIntentCode = PENDING_INTENT_REVIEWS
                    title = context.getString(R.string.notification_title_reviews)
                    text = context.getString(R.string.notification_text_reviews)
                }
                REQUEST_CODE_LESSONS_AND_REVIEWS -> {
                    channelId = NOTIFICATION_CHANNEL_LESSONS_AND_REVIEWS_ID
                    notificationId = NOTIFICATION_ID_LESSONS_AND_REVIEWS
                    pendingIntentCode = PENDING_INTENT_LESSONS_AND_REVIEWS
                    title = context.getString(R.string.notification_title_lessons_and_reviews)
                    text = context.getString(R.string.notification_text_lessons_and_reviews)
                }
                else -> {
                    Log.e("LeapNotification", "Unknown channel id")
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                pendingIntentCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder =
                NotificationCompat.Builder(context, channelId)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        }
    }

    fun createNotificationChannels() {
        createNotificationChannel(
            NOTIFICATION_CHANNEL_REVIEWS_ID,
            context.getString(R.string.notification_channel_reviews_name),
            context.getString(R.string.notification_channel_lessons_and_reviews_description)
        )
        createNotificationChannel(
            NOTIFICATION_CHANNEL_LESSONS_ID,
            context.getString(R.string.notification_channel_lessons_name),
            context.getString(R.string.notification_channel_lessons_description)
        )
        createNotificationChannel(
            NOTIFICATION_CHANNEL_LESSONS_AND_REVIEWS_ID,
            context.getString(R.string.notification_channel_lessons_and_reviews_name),
            context.getString(R.string.notification_channel_lessons_and_reviews_description)
        )
    }

    fun createNotificationChannel(id: String, name: String, desc: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(id, name, importance).apply {
                    description = desc
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}