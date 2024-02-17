package com.lomolo.uzi.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.uzi.CancelTripMutation
import com.lomolo.uzi.GetTripDetailsQuery
import com.lomolo.uzi.TripUpdatesSubscription
import com.lomolo.uzi.model.Trip
import com.lomolo.uzi.network.UziGqlApiInterface
import com.lomolo.uzi.sql.dao.TripDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach

interface TripInterface {
    suspend fun createTrip(trip: Trip)
    fun getTrip(): Flow<List<Trip>>
    fun getTripUpdates(id: String): Flow<ApolloResponse<TripUpdatesSubscription.Data>>
    suspend fun updateTrip(trip: Trip)
    suspend fun getTripDetails(tripId: String): ApolloResponse<GetTripDetailsQuery.Data>
    suspend fun clearTrips()
    suspend fun cancelTrip(tripId: String): ApolloResponse<CancelTripMutation.Data>
}

class TripRepository(
    private val tripDao: TripDao,
    private val uziGqlApi: UziGqlApiInterface
): TripInterface {
    override suspend fun createTrip(trip: Trip) = tripDao.createTrip(trip)

    override fun getTrip() = tripDao.getTrip()
    override fun getTripUpdates(id: String) = uziGqlApi
        .getTripUpdates(id)
        .onEach {
            tripDao.updateTrip(
                Trip(
                    id = it.data?.tripUpdates?.id.toString(),
                    status = it.data?.tripUpdates?.status.toString(),
                    lat = it.data?.tripUpdates?.location?.lat ?: 0.0,
                    lng = it.data?.tripUpdates?.location?.lng ?: 0.0
                )
            )
        }

    override suspend fun updateTrip(trip: Trip) = tripDao.updateTrip(trip)

    override suspend fun getTripDetails(tripId: String): ApolloResponse<GetTripDetailsQuery.Data> {
        val res = uziGqlApi.getTripDetails(tripId)
        tripDao.updateTrip(
            Trip(
                id = tripId,
                status = res.data?.getTripDetails?.status.toString(),
                lat = res.data?.getTripDetails?.courier?.location?.lat ?: 0.0,
                lng = res.data?.getTripDetails?.courier?.location?.lng ?: 0.0,
            )
        )
        return res
    }

    override suspend fun clearTrips() = tripDao.clearTrips()
    override suspend fun cancelTrip(tripId: String) = uziGqlApi.cancelTrip(tripId)
}