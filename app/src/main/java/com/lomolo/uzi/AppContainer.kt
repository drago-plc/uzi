package com.lomolo.uzi

import com.lomolo.uzi.network.UziRestApiService
import com.lomolo.uzi.repository.DeviceIpinfoRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val uziRestApiService: UziRestApiService
}

class DefaultContainer: AppContainer {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.MINUTES)
        .callTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://e952-102-217-127-1.ngrok-free.app")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okhttpClient)
        .build()

    override val uziRestApiService: UziRestApiService by lazy {
        retrofit.create(UziRestApiService::class.java)
    }
}