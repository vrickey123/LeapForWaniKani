package com.leapsoftware.leapforwanikani.data

import java.util.*

data class Lesson(
    val available_at: Date,
    val subject_ids: List<Int>
)