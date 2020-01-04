package com.leapsoftware.leapforwanikani.data.typeconverters

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.*

/**
 * Use Moshi [Rfc3339DateJsonAdapter]
 */
class DateTypeConverter {
    var moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()

    val dateAdapter = moshi.adapter(Date::class.java)

    @TypeConverter
    fun toJson(value: Date?): String? {
        return dateAdapter.toJson(value)
    }

    @TypeConverter
    fun fromJson(value: String) : Date? {
        return dateAdapter.fromJson(value)
    }
}