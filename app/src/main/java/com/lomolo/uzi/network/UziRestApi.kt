package com.lomolo.uzi.network

import com.lomolo.uzi.model.Ipinfo
import retrofit2.http.GET

interface UziRestApiService {
    @GET("ipinfo")
    suspend fun getIpinfo(): Ipinfo
}