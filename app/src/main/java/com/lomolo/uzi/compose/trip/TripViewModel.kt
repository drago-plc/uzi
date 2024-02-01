package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.DragState
import com.lomolo.uzi.GetCourierNearPickupPointQuery
import com.lomolo.uzi.MakeTripRouteQuery
import com.lomolo.uzi.ReverseGeocodeQuery
import com.lomolo.uzi.SearchPlaceQuery
import com.lomolo.uzi.network.UziGqlApiInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class TripViewModel(
    private val uziGqlApiRepository: UziGqlApiInterface
): ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    var searchingLocationState: LocationPredicateState by mutableStateOf(LocationPredicateState.Success(
        listOf()
    ))
        private set

    var pickupGeocodeState: PickupGeocodeState by mutableStateOf(PickupGeocodeState.Success(null))
        private set

    var dropoffGeocodeState: DropoffGeocodeState by mutableStateOf(DropoffGeocodeState.Success(null))
        private set

    var makeTripRouteState: MakeTripRouteState by mutableStateOf(MakeTripRouteState.Success(null))
        private set

    var getCourierNearPickupState: GetCourierNearPickupState by mutableStateOf(GetCourierNearPickupState.Success(listOf()))
        private set

    private var _trip = MutableStateFlow(Trip())
    val tripUiInput: StateFlow<Trip> = _trip.asStateFlow()

    var pickupMapDragState: DragState by mutableStateOf(DragState.START)
        private set
    fun startPickupMapDrag() {
        pickupMapDragState = DragState.DRAG
    }
    fun stopPickupMapDrag() {
        pickupMapDragState = DragState.END
    }
    fun resetPickupMapDrag() {
        pickupMapDragState = DragState.START
    }

    fun setPickup(pickup: ReverseGeocodeQuery.ReverseGeocode) {
        _trip.update {
            it.copy(pickup = pickup)
        }
    }

    fun setDropoff(dropoff: ReverseGeocodeQuery.ReverseGeocode) {
        _trip.update {
            it.copy(dropoff = dropoff)
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun searchPlace(query: String) {
        if (query.isNotBlank()) {
            searchingLocationState = LocationPredicateState.Loading
            viewModelScope.launch {
                searchingLocationState = try {
                    val res = uziGqlApiRepository.searchPlace(query).dataOrThrow()
                    LocationPredicateState.Success(res.searchPlace)
                } catch (e: ApolloException) {
                    LocationPredicateState.Error(e.message)
                }
            }
        }
    }

    suspend fun reverseGeocode(cords: LatLng) =
        uziGqlApiRepository.reverseGeocode(cords).dataOrThrow()

    fun pickupReverseGeocode(cords: LatLng, cb: (ReverseGeocodeQuery.ReverseGeocode) -> Unit = {}) {
        pickupGeocodeState = PickupGeocodeState.Loading
        viewModelScope.launch {
            pickupGeocodeState = try {
                val res = reverseGeocode(cords)
                PickupGeocodeState.Success(res.reverseGeocode).also { cb(res.reverseGeocode!!) }
            } catch (e: ApolloException) {
                PickupGeocodeState.Error(e.message)
            }
        }
    }

    fun dropoffReverseGeocode(cords: LatLng, cb: (ReverseGeocodeQuery.ReverseGeocode) -> Unit = {}) {
        dropoffGeocodeState = DropoffGeocodeState.Loading
        viewModelScope.launch {
            dropoffGeocodeState = try {
                val res = reverseGeocode(cords)
                DropoffGeocodeState.Success(res.reverseGeocode).also { cb(res.reverseGeocode!!) }
            } catch (e: ApolloException) {
                DropoffGeocodeState.Error(e.message)
            }
        }
    }

    fun callTripEndpoint(): Boolean {
        return _trip.value.pickup.placeId.isNotBlank() && _trip.value.dropoff.placeId.isNotBlank()
    }

    fun makeTripRoute() {
        if (makeTripRouteState !is MakeTripRouteState.Loading) {
            makeTripRouteState = MakeTripRouteState.Loading
            viewModelScope.launch {
                makeTripRouteState = try {
                    val res = uziGqlApiRepository.makeTripRoute(
                        pickup = _trip.value.pickup,
                        dropoff = _trip.value.dropoff
                    ).dataOrThrow()
                    MakeTripRouteState.Success(res.makeTripRoute)
                } catch(e: IOException) {
                    MakeTripRouteState.Error(e.message)
                }
            }
        }
    }

    fun getCourierNearPickup(pickup: LatLng) {
        getCourierNearPickupState = GetCourierNearPickupState.Loading
        viewModelScope.launch {
            getCourierNearPickupState = try {
                val res = uziGqlApiRepository.getCourierNearPickupPoint(pickup).dataOrThrow()
                GetCourierNearPickupState.Success(res.getCourierNearPickupPoint)
            } catch(e: ApolloException) {
                GetCourierNearPickupState.Error(e.message)
            }
        }
    }

    fun resetTrip() {
        //_trip.value = Trip() TODO just for testing(revert once ready)
    }
}

interface LocationPredicateState {
    data class Success(val places: List<SearchPlaceQuery.SearchPlace>): LocationPredicateState
    data object Loading: LocationPredicateState
    data class Error(val message: String?): LocationPredicateState
}

interface DropoffGeocodeState {
    data class Success(val geocode: ReverseGeocodeQuery.ReverseGeocode?): DropoffGeocodeState
    data object Loading: DropoffGeocodeState
    data class Error(val message: String?): DropoffGeocodeState
}

interface PickupGeocodeState {
    data class Success(val geocode: ReverseGeocodeQuery.ReverseGeocode?): PickupGeocodeState
    data object Loading: PickupGeocodeState
    data class Error(val message: String?): PickupGeocodeState
}

data class Trip(
    val pickup: ReverseGeocodeQuery.ReverseGeocode = ReverseGeocodeQuery.ReverseGeocode("", "", ReverseGeocodeQuery.Location(0.0, 0.0)),
    val dropoff: ReverseGeocodeQuery.ReverseGeocode = ReverseGeocodeQuery.ReverseGeocode("", "", ReverseGeocodeQuery.Location(0.0, 0.0))
)

interface MakeTripRouteState {
    data class Success(val success: MakeTripRouteQuery.MakeTripRoute?): MakeTripRouteState
    data object Loading: MakeTripRouteState
    data class Error(val message: String?): MakeTripRouteState
}

interface GetCourierNearPickupState {
    data class Success(val success: List<GetCourierNearPickupPointQuery.GetCourierNearPickupPoint>): GetCourierNearPickupState
    data object Loading: GetCourierNearPickupState
    data class Error(val message: String?): GetCourierNearPickupState
}