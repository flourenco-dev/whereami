package com.fabiolourenco.whereami.data.model.database

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fabiolourenco.whereami.data.database.converter.AddressTypeConverter
import com.fabiolourenco.whereami.data.database.converter.PositionTypeConverter
import com.fabiolourenco.whereami.data.model.api.Address
import com.fabiolourenco.whereami.data.model.api.DisplayPosition
import com.google.android.gms.maps.model.LatLng

@TypeConverters(AddressTypeConverter::class, PositionTypeConverter::class)
@Entity(tableName = "favorites")
data class Place(@PrimaryKey @ColumnInfo(name = "id") val locationId: String,
                 val label: String,
                 val address: Address?,
                 val distance: Int,
                 val position: DisplayPosition?): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readValue(Int::class.java.classLoader) as Int,
        parcel.readParcelable(DisplayPosition::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(locationId)
        parcel.writeString(label)
        parcel.writeParcelable(address, flags)
        parcel.writeValue(distance)
        parcel.writeParcelable(position, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place {
            return Place(parcel)
        }

        override fun newArray(size: Int): Array<Place?> {
            return arrayOfNulls(size)
        }
    }

    fun getFormattedLabel() =
        if (!TextUtils.isEmpty(address?.street)) {
            address?.street
        } else {
            label
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

    fun getFormattedPostalCode() : String {
        var formattedPostalCode = ""

        address?.postalCode?.let { postalCode ->
            formattedPostalCode = postalCode

            // Should use a String format here
            address.city?.let { city ->
                formattedPostalCode = "$postalCode, $city"
            }
        }

        return formattedPostalCode
    }

    fun getFormattedCoordinates() =
        if (position?.latitude != null && position.longitude != null) {
            "${position.latitude}, ${position.longitude}"
        } else {
            ""
        }

    fun getFormattedDistance() = "$distance m"

    fun getLatLng() = LatLng(position?.latitude ?: 0.0, position?.longitude  ?: 0.0)
}