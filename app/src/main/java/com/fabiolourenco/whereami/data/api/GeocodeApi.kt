package com.fabiolourenco.whereami.data.api

import com.fabiolourenco.whereami.data.model.api.GeocodeResponse
import com.fabiolourenco.whereami.utils.NetworkUtils
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeApi {
    @GET("geocode.json?app_id=${NetworkUtils.appId}&app_code=${NetworkUtils.appCode}")
    fun getCoordinates(@Query("locationid") locationId: String): Call<GeocodeResponse>
}