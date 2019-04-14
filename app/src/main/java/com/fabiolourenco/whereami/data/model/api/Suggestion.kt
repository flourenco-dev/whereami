package com.fabiolourenco.whereami.data.model.api

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.squareup.moshi.Json

data class Suggestion(@field:Json(name = "label")       val label: String?,
                      @field:Json(name = "locationId")  val locationId: String?,
                      @field:Json(name = "address")     val address: Address?,
                      @field:Json(name = "distance")    val distance: Int?): SearchSuggestion {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun getBody() = label

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(label)
        parcel.writeString(locationId)
        parcel.writeParcelable(address, flags)
        parcel.writeValue(distance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Suggestion> {
        override fun createFromParcel(parcel: Parcel): Suggestion {
            return Suggestion(parcel)
        }

        override fun newArray(size: Int): Array<Suggestion?> {
            return arrayOfNulls(size)
        }
    }

    fun getFormattedStreet() =
        if (!TextUtils.isEmpty(address?.street)
            && !TextUtils.isEmpty(address?.city)
            && !TextUtils.isEmpty(address?.country)) {

            // Should use a String format here
            address?.street + ", " + address?.city + ", " + address?.country

        } else {
            label
        }

    fun getFormattedDistance() = "$distance m"
}