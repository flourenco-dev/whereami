package com.fabiolourenco.whereami.data.model.api

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class Address(@field:Json(name = "country")    val country: String?,
                   @field:Json(name = "state")      val state: String?,
                   @field:Json(name = "county")     val county: String?,
                   @field:Json(name = "city")       val city: String?,
                   @field:Json(name = "district")   val district: String?,
                   @field:Json(name = "street")     val street: String?,
                   @field:Json(name = "postalCode") val postalCode: String?,
                   @field:Json(name = "unit")       val unit: String?,
                   @field:Json(name = "distance")   val distance: Int?): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(country)
        parcel.writeString(state)
        parcel.writeString(county)
        parcel.writeString(city)
        parcel.writeString(district)
        parcel.writeString(street)
        parcel.writeString(postalCode)
        parcel.writeString(unit)
        parcel.writeValue(distance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}