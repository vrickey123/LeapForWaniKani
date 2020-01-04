package com.leapsoftware.leapforwanikani.data.typeconverters

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class ListIntTypeConverter {
    val moshi = Moshi.Builder().build()
    val listType = Types.newParameterizedType(List::class.java, Integer::class.java)
    val adapter: JsonAdapter<List<Int>> = moshi.adapter(listType)

    @TypeConverter
    fun fromJson(value: String): List<Int>? {
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toJson(value: List<Int>?): String {
        return adapter.toJson(value)
    }
}