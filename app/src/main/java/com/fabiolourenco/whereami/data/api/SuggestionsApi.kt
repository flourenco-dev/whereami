package com.fabiolourenco.whereami.data.api

import com.fabiolourenco.whereami.data.model.api.SuggestionsResponse
import com.fabiolourenco.whereami.utils.NetworkUtils
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SuggestionsApi {

    @GET("suggest.json?app_id=${NetworkUtils.appId}&app_code=${NetworkUtils.appCode}&maxresults=20")
    fun getNearbyPlaces(@Query("query") query: String, @Query("prox") location: String)
            : Call<SuggestionsResponse>

    @GET("suggest.json?app_id=${NetworkUtils.appId}&app_code=${NetworkUtils.appCode}&maxresults=5")
    fun getSuggestions(@Query("query") query: String)
            : Call<SuggestionsResponse>
}