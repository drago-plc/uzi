package com.lomolo.uzi.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.uzi.TripUpdatesSubscription
import com.lomolo.uzi.model.Trip
import com.lomolo.uzi.network.UziGqlApiInterface
import com.lomolo.uzi.sql.dao.TripDao
import kotlinx.coroutines.flow.Flow

interface TripInterface {
    suspend fun createTrip(trip: Trip)
    fun getTrip(): Flow<Trip>
    fun getTripUpdates(id: String): Flow<ApolloResponse<TripUpdatesSubscription.Data>>
    suspend fun updateTrip(trip: Trip)
}

class TripRepository(
    private val tripDao: TripDao,
    private val uziGqlApi: UziGqlApiInterface
): TripInterface {
    override suspend fun createTrip(trip: Trip) = tripDao.createTrip(trip)

    override fun getTrip(): Flow<Trip> = tripDao.getTrip()
    override fun getTripUpdates(id: String): Flow<ApolloResponse<TripUpdatesSubscription.Data>> =
        uziGqlApi
            .getTripUpdates(id)

    override suspend fun updateTrip(trip: Trip) = tripDao.updateTrip(trip)
}