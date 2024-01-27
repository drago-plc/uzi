package com.lomolo.uzi

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.lomolo.uzi.network.UziGqlApiInterface
import com.lomolo.uzi.network.UziGqlApiRepository
import com.lomolo.uzi.network.UziRestApiServiceInterface
import com.lomolo.uzi.repository.SessionInterface
import com.lomolo.uzi.repository.SessionRepository
import com.lomolo.uzi.sql.UziStore
import com.lomolo.uzi.sql.dao.SessionDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val uziRestApiService: UziRestApiServiceInterface
    val sessionRepository: SessionInterface
    val uziGqlApiRepository: UziGqlApiInterface
    val apolloClient: ApolloClient
}

private const val baseApi = "https://16c1-102-217-124-1.ngrok-free.app"

class AuthInterceptor(
    private val sessionDao: SessionDao,
    private val sessionRepository: SessionInterface
): HttpInterceptor {
    private val mutex = Mutex()

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {
        var session = mutex.withLock {
            sessionDao
                .getSession()
                .firstOrNull()
        }

        val response = chain.proceed(
            request.newBuilder().addHeader("Authorization", "Bearer ${session!!.first().token}").build()
        )

        return if (response.statusCode == 401) {
            session = mutex.withLock {
                sessionRepository.refreshSession(session!!.first())
                sessionDao
                    .getSession()
                    .firstOrNull()
            }

            chain.proceed(
                request.newBuilder().addHeader("Authorization", "Bearer ${session!!.first().token}").build()
            )
        } else {
            return response
        }
    }
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
        .serverUrl("${baseApi}/api")
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
}