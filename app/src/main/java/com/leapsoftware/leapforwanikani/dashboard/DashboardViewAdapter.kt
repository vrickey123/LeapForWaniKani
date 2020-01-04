package com.leapsoftware.leapforwanikani.dashboard

import android.content.Context
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.leapsoftware.leapforwanikani.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DashboardViewAdapter(val context: Context) {

    fun bindAvailableStatus(availableStatus: TextView, nextReviewsAtDate: Date?) {
        availableStatus.text = getNextReviewStatusMessage(nextReviewsAtDate)
    }

    fun bindLessonsCount(lessonsCardView: MaterialCardView, lessonsCount: Int) {
        val lessonsCountTextView = lessonsCardView.findViewById<TextView>(R.id.available_count)
        lessonsCountTextView.text = lessonsCount.toString()
    }

    fun bindLessonsTitle(lessonsCardView: MaterialCardView, title: String) {
        val lessonsTitle = lessonsCardView.findViewById<TextView>(R.id.available_title)
        lessonsTitle.text = title
    }

    fun bindReviewsCount(reviewsCardView: MaterialCardView, reviewsCount: Int) {
        val reviewsCountTextView = reviewsCardView.findViewById<TextView>(R.id.available_count)
        reviewsCountTextView.text = reviewsCount.toString()
    }

    fun bindReviewsTitle(reviewsCardView: MaterialCardView, title: String) {
        val reviewsTitle = reviewsCardView.findViewById<TextView>(R.id.available_title)
        reviewsTitle.text = title
    }

    fun bindStageApprenticeTextView(stageProgressCardView: MaterialCardView, countApprentice: Int) {
        stageProgressCardView.findViewById<TextView>(R.id.apprentice_count).text = countApprentice.toString()
    }

    fun bindStageGuruTextView(stageProgressCardView: MaterialCardView, countGuru: Int) {
        stageProgressCardView.findViewById<TextView>(R.id.guru_count).text = countGuru.toString()
    }

    fun bindStageMasterTextView(stageProgressCardView: MaterialCardView, masterCount: Int) {
        stageProgressCardView.findViewById<TextView>(R.id.master_count).text = masterCount.toString()
    }

    fun bindStageEnlightenedTextView(stageProgressCardView: MaterialCardView, enlightenedCount: Int) {
        stageProgressCardView.findViewById<TextView>(R.id.enlightened_count).text = enlightenedCount.toString()
    }

    fun bindStageBurnedTextView(stageProgressCardView: MaterialCardView, burnedCount: Int) {
        stageProgressCardView.findViewById<TextView>(R.id.burned_count).text = burnedCount.toString()
    }

    fun getNextReviewStatusMessage(nextReviewsAtDate: Date?) : String {
        val now = Date()
        if (nextReviewsAtDate == null) {
            return context.getString(R.string.error_available_status)
        } else if (nextReviewsAtDate.before(now) || nextReviewsAtDate == now) {
            return "Now"
        } else {
            val diffInMillis: Long = nextReviewsAtDate.time - now.time
            val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMillis)
            val diffInMin: Long = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            if (diffInHours < 1) {
                return String.format(context.getString(R.string.review_status_format_minutes), diffInMin)
            } else if (diffInHours in 1..3) {
                return String.format(context.getString(R.string.review_status_format_hours), diffInHours)
            } else {
                val formatter = SimpleDateFormat(context.getString(R.string.date_format_review_status), Locale.getDefault())
                return formatter.format(nextReviewsAtDate)
            }
        }
    }
}