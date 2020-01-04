package com.leapsoftware.leapforwanikani.data.source.remote.api

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.leapsoftware.leapforwanikani.data.source.remote.WANIKANI_JSON_KEY_OBJECT
import com.leapsoftware.leapforwanikani.data.source.remote.api.types.WKReportType
import com.leapsoftware.leapforwanikani.data.source.remote.api.types.WKResourceType
import com.squareup.moshi.Json
import java.util.*

/**
 * Reports are a generic response for an item of a given type. For example, a User or Summary.
 */
sealed class WKReport(@Json(name = WANIKANI_JSON_KEY_OBJECT) var wanikani_object_type: String) {
    abstract val url: String
    abstract val data_updated_at: Date
    abstract val data: WKData

    @Entity(primaryKeys = arrayOf("wanikani_object_type", "data_updated_at"))
    data class Summary(
        override val url: String,
        override val data_updated_at: Date,
        @Embedded
        override val data: WKData.SummaryData
    ) : WKReport(WKReportType.report.name)

    @Entity(primaryKeys = arrayOf("wanikani_object_type", "data_updated_at"))
    data class User(
        override val url: String,
        override val data_updated_at: Date,
        @Embedded
        override val data: WKData.UserData
    ) : WKReport(WKReportType.user.name)

    /**
     * Resources return information on a singular item with an id. For example,
     * there can be a List<Assignment> with an id of 1, 2, 3, etc.
     */
    sealed class WKResource(wanikani_object_type: String): WKReport(wanikani_object_type) {
        abstract val id: Int

        @Entity
        data class Assignment(
            @PrimaryKey
            override val id: Int,
            override val url: String,
            override val data_updated_at: Date,
            @Embedded
            override val data: WKData.AssignmentData
        ) : WKResource(WKResourceType.assignment.name)
    }
}