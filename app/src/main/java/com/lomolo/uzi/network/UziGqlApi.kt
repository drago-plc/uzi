package com.lomolo.uzi.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.uzi.ReverseGeocodeQuery
import com.lomolo.uzi.SearchPlaceQuery

interface UziGqlApiInterface {
    suspend fun searchPlace(place: String): ApolloResponse<SearchPlaceQuery.Data>
    suspend fun reverseGeocode(place: LatLng): ApolloResponse<ReverseGeocodeQuery.Data>
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
}