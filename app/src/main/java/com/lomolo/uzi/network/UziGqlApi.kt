package com.lomolo.uzi.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.uzi.GetCourierNearPickupPointQuery
import com.lomolo.uzi.MakeTripRouteQuery
import com.lomolo.uzi.ReverseGeocodeQuery
import com.lomolo.uzi.SearchPlaceQuery
import com.lomolo.uzi.type.GpsInput
import com.lomolo.uzi.type.TripInput

interface UziGqlApiInterface {
    suspend fun searchPlace(place: String): ApolloResponse<SearchPlaceQuery.Data>
    suspend fun reverseGeocode(place: LatLng): ApolloResponse<ReverseGeocodeQuery.Data>
    suspend fun makeTripRoute(pickup: ReverseGeocodeQuery.ReverseGeocode, dropoff: ReverseGeocodeQuery.ReverseGeocode): ApolloResponse<MakeTripRouteQuery.Data>
    suspend fun getCourierNearPickupPoint(pickup: LatLng): ApolloResponse<GetCourierNearPickupPointQuery.Data>
}

class UziGqlApiRepository(
    private val apolloClient: ApolloClient
): UziGqlApiInterface {
    override suspend fun searchPlace(place: String): ApolloResponse<SearchPlaceQuery.Data> {
        return apolloClient.query(SearchPlaceQuery(place)).execute()
    }

    override suspend fun reverseGeocode(place: LatLng): ApolloResponse<ReverseGeocodeQuery.Data> {
        return apolloClient.query(ReverseGeocodeQuery(place.latitude, place.longitude)).execute()
    }

    override suspend fun makeTripRoute(
        pickup: ReverseGeocodeQuery.ReverseGeocode,
        dropoff: ReverseGeocodeQuery.ReverseGeocode
    ): ApolloResponse<MakeTripRouteQuery.Data> {
        return apolloClient.query(
            MakeTripRouteQuery(
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
    }

    override suspend fun getCourierNearPickupPoint(point: LatLng): ApolloResponse<GetCourierNearPickupPointQuery.Data> {
        return apolloClient.query(
            GetCourierNearPickupPointQuery(
                GpsInput(point.latitude, point.longitude)
            )
        ).execute()
    }
}