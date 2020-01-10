package com.leapsoftware.leapforwanikani.data.typeconverters

import androidx.room.TypeConverter
import com.leapsoftware.leapforwanikani.data.Lesson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.*

class ListLessonTypeConverter {
    val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()
    val listType = Types.newParameterizedType(List::class.java, Lesson::class.java)
    val adapter: JsonAdapter<List<Lesson>> = moshi.adapter(listType)

    @TypeConverter
    fun fromJson(value: String): List<Lesson>? {
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toJson(value: List<Lesson>?): String {
        return adapter.toJson(value)
    }
}