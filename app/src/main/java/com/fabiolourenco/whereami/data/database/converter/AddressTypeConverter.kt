package com.fabiolourenco.whereami.data.database.converter

import androidx.room.TypeConverter
import com.fabiolourenco.whereami.data.model.api.Address
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class AddressTypeConverter {

    private val moshi = Moshi.Builder().build()
    private val type = Types.newParameterizedType(Address::class.java, String::class.java)
    private val adapter = moshi.adapter<Address>(type)

    @TypeConverter fun addressToString(address: Address?) = adapter.toJson(address)

    @TypeConverter fun stringToAddress(string: String) = adapter.fromJson(string)
}