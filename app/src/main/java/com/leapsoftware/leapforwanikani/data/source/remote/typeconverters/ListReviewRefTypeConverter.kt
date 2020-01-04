package com.leapsoftware.leapforwanikani.data.typeconverters

import androidx.room.TypeConverter
import com.leapsoftware.leapforwanikani.data.ReviewRef
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.*

class ListReviewRefTypeConverter {
    val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()
    val listType = Types.newParameterizedType(List::class.java, ReviewRef::class.java)
    val adapter: JsonAdapter<List<ReviewRef>> = moshi.adapter(listType)

    @TypeConverter
    fun fromJson(value: String): List<ReviewRef>? {
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toJson(value: List<ReviewRef>?): String {
        return adapter.toJson(value)
    }
}