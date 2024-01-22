package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.DragState
import com.lomolo.uzi.ReverseGeocodeQuery
import com.lomolo.uzi.SearchPlaceQuery
import com.lomolo.uzi.network.UziGqlApiInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        searchingLocationState = LocationPredicateState.Loading
        viewModelScope.launch {
            searchingLocationState = try {
                val res = uziGqlApiRepository.searchPlace(query).dataOrThrow()
                LocationPredicateState.Success(res.searchPlace)
            } catch(e: ApolloException) {
                LocationPredicateState.Error(e.message)
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