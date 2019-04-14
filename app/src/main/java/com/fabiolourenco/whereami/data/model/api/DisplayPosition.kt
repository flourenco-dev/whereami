package com.fabiolourenco.whereami.data.model.api

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class DisplayPosition(@field:Json(name = "Latitude") val latitude: Double?,
                           @field:Json(name = "Longitude") val longitude: Double?): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DisplayPosition> {
        override fun createFromParcel(parcel: Parcel): DisplayPosition {
            return DisplayPosition(parcel)
        }

        override fun newArray(size: Int): Array<DisplayPosition?> {
            return arrayOfNulls(size)
        }
    }
}