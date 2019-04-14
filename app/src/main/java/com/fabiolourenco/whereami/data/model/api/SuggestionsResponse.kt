package com.fabiolourenco.whereami.data.model.api

import com.squareup.moshi.Json

data class SuggestionsResponse(@field:Json(name = "suggestions") val suggestions: List<Suggestion>?)