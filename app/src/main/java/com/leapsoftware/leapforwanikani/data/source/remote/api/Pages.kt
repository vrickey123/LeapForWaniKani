package com.leapsoftware.leapforwanikani.data.source.remote.api

/*
* This class does not extend from WKCollection, WKResource, or WKData.
*/
data class Pages(
    val next_url: String?,
    val previous_url: String?,
    val per_page: Int
)