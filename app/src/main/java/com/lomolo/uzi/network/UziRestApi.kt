package com.lomolo.uzi.network

import com.lomolo.uzi.model.Ipinfo
import com.lomolo.uzi.model.Session
import com.lomolo.uzi.model.SignIn
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface UziRestApiService {
    @GET("ipinfo")
    suspend fun getIpinfo(): Ipinfo
    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun signIn(@Body input: SignIn): Session
}