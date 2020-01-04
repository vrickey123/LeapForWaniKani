package com.leapsoftware.leapforwanikani.data.source.remote.api

import androidx.room.Embedded
import com.leapsoftware.leapforwanikani.data.Lesson
import com.leapsoftware.leapforwanikani.data.ReviewRef
import java.util.*

sealed class WKData() {
    data class AssignmentData(
        val available_at: Date?,
        val burned_at: Date?,
        val created_at: Date,
        val hidden: Boolean,
        val passed_at: Date?,
        val passed: Boolean,
        val resurrected_at: Date?,
        val srs_stage_name: String,
        val srs_stage: Int,
        val started_at: Date?,
        val subject_id: Int,
        val subject_type: String,
        val unlocked_at: Date?
    ) : WKData()

    data class UserData(
        val id: String?, // TODO: this was in the example response, but not documented with a description.
        val current_vacation_started_at: Date?,
        val level: Int,
        val max_level_granted_by_subscription: Int?, // TODO: this will be deprecated. Use Subscription.
        @Embedded
        val preferences: Preferences,
        val profile_url: String,
        val started_at: Date,
        val subscribed: Boolean?, // TODO: this will be deprecated. Use Subscription.
        @Embedded
        val subscription: Subscription,
        val username: String
    ) : WKData()

    data class SummaryData(
        val lessons: List<Lesson>,
        val next_reviews_at: Date?,
        val reviews: List<ReviewRef>
    ) : WKData()
}
