package com.leapsoftware.leapforwanikani.data.source.remote.api

import com.leapsoftware.leapforwanikani.data.source.remote.WANIKANI_JSON_KEY_OBJECT
import com.squareup.moshi.Json
import java.util.*

data class WKCollection<T>(@Json(name = WANIKANI_JSON_KEY_OBJECT) var wanikani_object_type: String,
                           val url: String,
                           val pages: Pages,
                           val total_count: Int,
                           val data_updated_at: Date?, // added ? for ResetCollection
                           val data: List<T>)