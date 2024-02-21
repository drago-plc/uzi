package com.lomolo.uzi.container

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.SubscriptionWsProtocol
import com.lomolo.uzi.apollo.interceptors.AuthInterceptor
import com.lomolo.uzi.network.UziGqlApiInterface
import com.lomolo.uzi.network.UziRestApiServiceInterface
import com.lomolo.uzi.repository.SessionInterface
import com.lomolo.uzi.network.UziGqlApiRepository
import com.lomolo.uzi.repository.SessionRepository
import com.lomolo.uzi.repository.TripInterface
import com.lomolo.uzi.repository.TripRepository
import com.lomolo.uzi.sql.UziStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val uziRestApiService: UziRestApiServiceInterface
    val sessionRepository: SessionInterface
    val uziGqlApiRepository: UziGqlApiInterface
    val apolloClient: ApolloClient
    val tripRepository: TripInterface
}

class DefaultContainer(private val context: Context): AppContainer {
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
        .baseUrl(baseApi)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okhttpClient)
        .build()

    override val uziRestApiService: UziRestApiServiceInterface by lazy {
        retrofit.create(UziRestApiServiceInterface::class.java)
    }

    override val sessionRepository: SessionInterface by lazy {
        SessionRepository(
            sessionDao = UziStore.getStore(context).sessionDao(),
            uziRestApiService = uziRestApiService
        )
    }

    override val apolloClient = ApolloClient.Builder()
        .okHttpClient(okhttpClient)
        .httpServerUrl("${baseApi}/api")
        .webSocketServerUrl("${wss}/subscription") //TODO add auth token header
        /*
        .wsProtocol(
            SubscriptionWsProtocol.Factory(
                connectionPayload = {
                    mapOf(
                        "type" to "connection_init",
                        "payload" to mapOf(
                            "headers" to mapOf(
                                "Authorization" to "Bearer ")
                            )
                        )
                }
            )
        )
         */
        .webSocketReopenWhen { _, attempt ->
            delay(attempt * 1000)
            true
        }
        .addHttpInterceptor(
            AuthInterceptor(
                UziStore.getStore(context).sessionDao(),
                sessionRepository
            )
        )
        .build()

    override val uziGqlApiRepository: UziGqlApiInterface by lazy {
        UziGqlApiRepository(apolloClient)
    }

    override val tripRepository: TripInterface by lazy {
        TripRepository(
            UziStore.getStore(context).tripDao(),
            uziGqlApiRepository
        )
    }
}