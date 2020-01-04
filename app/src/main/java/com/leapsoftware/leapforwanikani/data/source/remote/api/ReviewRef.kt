package com.leapsoftware.leapforwanikani.data

import java.util.*

data class ReviewRef(
    val available_at: Date,
    val subject_ids: List<Int>
)