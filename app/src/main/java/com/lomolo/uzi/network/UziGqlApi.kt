package com.lomolo.uzi.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.uzi.CancelTripMutation
import com.lomolo.uzi.GetCourierNearPickupPointQuery
import com.lomolo.uzi.ComputeTripRouteQuery
import com.lomolo.uzi.CreateTripMutation
import com.lomolo.uzi.GetTripDetailsQuery
import com.lomolo.uzi.ReverseGeocodeQuery
import com.lomolo.uzi.SearchPlaceQuery
import com.lomolo.uzi.TripUpdatesSubscription
import com.lomolo.uzi.type.CreateTripInput
import com.lomolo.uzi.type.GpsInput
import com.lomolo.uzi.type.TripInput
import kotlinx.coroutines.flow.Flow

interface UziGqlApiInterface {
    suspend fun searchPlace(place: String): ApolloResponse<SearchPlaceQuery.Data>
    suspend fun reverseGeocode(place: LatLng): ApolloResponse<ReverseGeocodeQuery.Data>
    suspend fun makeTripRoute(pickup: ReverseGeocodeQuery.ReverseGeocode, dropoff: ReverseGeocodeQuery.ReverseGeocode): ApolloResponse<ComputeTripRouteQuery.Data>
    suspend fun getCourierNearPickupPoint(pickup: LatLng): ApolloResponse<GetCourierNearPickupPointQuery.Data>
    suspend fun createTrip(input: CreateTripInput): ApolloResponse<CreateTripMutation.Data>
    fun getTripUpdates(id: String): Flow<ApolloResponse<TripUpdatesSubscription.Data>>
    suspend fun getTripDetails(tripId: String): ApolloResponse<GetTripDetailsQuery.Data>
    suspend fun cancelTrip(tripId: String): ApolloResponse<CancelTripMutation.Data>
}

class UziGqlApiRepository(
    private val apolloClient: ApolloClient
): UziGqlApiInterface {
    override suspend fun searchPlace(place: String) = apolloClient.query(SearchPlaceQuery(place)).execute()

    override suspend fun reverseGeocode(place: LatLng) = apolloClient.query(ReverseGeocodeQuery(place.latitude, place.longitude)).execute()

    override suspend fun makeTripRoute(
        pickup: ReverseGeocodeQuery.ReverseGeocode,
        dropoff: ReverseGeocodeQuery.ReverseGeocode
    ) = apolloClient.query(
            ComputeTripRouteQuery(
                pickup = TripInput(
                    placeId = pickup.placeId,
                    formattedAddress = pickup.formattedAddress,
                    location = GpsInput(
                        lat = pickup.location.lat,
                        lng = pickup.location.lng
                    )
                ),
                dropoff = TripInput(
                    placeId = dropoff.placeId,
                    formattedAddress = dropoff.formattedAddress,
                    location = GpsInput(
                        lat = dropoff.location.lat,
                        lng = dropoff.location.lng
                    )
                )
            )
        ).execute()

    override suspend fun getCourierNearPickupPoint(pickup: LatLng) = apolloClient.query(GetCourierNearPickupPointQuery(GpsInput(pickup.latitude, pickup.longitude))).execute()

    override suspend fun createTrip(input: CreateTripInput) = apolloClient.mutation(CreateTripMutation(input)).execute()

    override fun getTripUpdates(id: String) = apolloClient.subscription(TripUpdatesSubscription(id)).toFlow()

    override suspend fun getTripDetails(tripId: String) = apolloClient.query(GetTripDetailsQuery(tripId)).execute()
    override suspend fun cancelTrip(tripId: String) = apolloClient.mutation(CancelTripMutation(tripId)).execute()
}