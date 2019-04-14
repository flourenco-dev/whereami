package com.fabiolourenco.whereami.data.model.api

import com.squareup.moshi.Json

data class Location(@field:Json(name = "LocationId")        val locationId: String?,
                    @field:Json(name = "DisplayPosition")   val displayPosition: DisplayPosition?,
                    @field:Json(name = "address")           val address: Address?)