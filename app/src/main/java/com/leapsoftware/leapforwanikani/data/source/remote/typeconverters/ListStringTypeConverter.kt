package com.leapsoftware.leapforwanikani.data.typeconverters

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class ListStringTypeConverter {
    val moshi = Moshi.Builder().build()
    val listType = Types.newParameterizedType(List::class.java, String::class.java)
    val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)

    @TypeConverter
    fun fromJson(value: String): List<String>? {
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toJson(value: List<String>?): String {
        return adapter.toJson(value)
    }
}