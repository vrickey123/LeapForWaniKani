package com.leapsoftware.leapforwanikani.data.source.remote.api

import java.util.*

/*
* This class does not extend from WKCollection, WKResource, or WKData.
*/
data class Subscription(
    val active: Boolean,
    val max_level_granted: Int,
    val period_ends_at: Date?,
    val type: String
)