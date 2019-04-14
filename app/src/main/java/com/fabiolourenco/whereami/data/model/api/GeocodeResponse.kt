package com.fabiolourenco.whereami.data.model.api

import com.squareup.moshi.Json

data class GeocodeResponse(@field:Json(name = "Response") val response: Response?) {

    // Has the api call is being made using the Location ID, all lists of the response will only contain one element
    fun getGeocodeLocation(): DisplayPosition? = response?.view?.get(0)?.result?.get(0)?.location?.displayPosition
}

data class Response(@field:Json(name = "View") val view: List<View>?)

data class View(@field:Json(name = "Result") val result: List<Result>?)

data class Result(@field:Json(name = "Location") val location: Location?)