package com.leapsoftware.leapforwanikani.utils

import com.leapsoftware.leapforwanikani.data.Lesson
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKData
import java.util.*

fun WKData.SummaryData.hasAvailableReviews(): Boolean {
    return when (next_reviews_at?.before(Date())) {
        null -> { // next_views_at is null
            false // so return false
        }
        false -> { // next reviews available after now
            false
        }
        true -> { // next reviews available before now
            true
        }
    }
}

fun Lesson.hasAvailableLessons(): Boolean {
    return available_at.before(Date())
}