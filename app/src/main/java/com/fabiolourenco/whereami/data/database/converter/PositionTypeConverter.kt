package com.fabiolourenco.whereami.data.database.converter

import androidx.room.TypeConverter
import com.fabiolourenco.whereami.data.model.api.DisplayPosition
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class PositionTypeConverter {

    private val moshi = Moshi.Builder().build()
    private val type = Types.newParameterizedType(DisplayPosition::class.java, String::class.java)
    private val adapter = moshi.adapter<DisplayPosition>(type)

    @TypeConverter fun displayPositionToString(position: DisplayPosition?) = adapter.toJson(position)

    @TypeConverter fun stringToDisplayPosition(string: String) = adapter.fromJson(string)
}