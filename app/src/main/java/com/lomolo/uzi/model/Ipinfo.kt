package com.lomolo.uzi.model

import com.squareup.moshi.Json

data class Ipinfo(
    @Json(name = "loc") val location: String,
    @Json(name = "country") val country: String
)
