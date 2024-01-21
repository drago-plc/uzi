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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripViewModel(
    private val uziGqlApiRepository: UziGqlApiInterface
): ViewModel() {
    var searchingLocationState: LocationPredicateState by mutableStateOf(LocationPredicateState.Success(
        listOf()
    ))
        private set
    var reverseGeocodeState: LocationGeocodeState by mutableStateOf(LocationGeocodeState.Success(null))
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

    fun setPickup(pickup: String) {
        _trip.update {
            it.copy(pickup = pickup)
        }
    }

    fun searchPlace(query: String) {
        searchingLocationState = LocationPredicateState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            delay(2_000L)
            searchingLocationState = try {
                val res = uziGqlApiRepository.searchPlace(query).dataOrThrow()
                LocationPredicateState.Success(res.searchPlace)
            } catch(e: ApolloException) {
                LocationPredicateState.Error(e.message)
            }
        }
    }

    fun reverseGeocode(cords: LatLng, cb: (String) -> Unit = {}) {
        reverseGeocodeState = LocationGeocodeState.Loading
        viewModelScope.launch {
            reverseGeocodeState = try {
                val res = uziGqlApiRepository.reverseGeocode(cords).dataOrThrow()
                LocationGeocodeState.Success(res.reverseGeocode).also { cb(res.reverseGeocode!!.formattedAddress) }
            } catch(e: ApolloException) {
                LocationGeocodeState.Error(e.message)
            }
        }
    }
}

interface LocationPredicateState {
    data class Success(val places: List<SearchPlaceQuery.SearchPlace>): LocationPredicateState
    data object Loading: LocationPredicateState
    data class Error(val message: String?): LocationPredicateState
}

interface LocationGeocodeState {
    data class Success(val geocode: ReverseGeocodeQuery.ReverseGeocode?): LocationGeocodeState
    data object Loading: LocationGeocodeState
    data class Error(val message: String?): LocationGeocodeState
}

data class Trip(
    val pickup: String = "",
    val dropoff: String = ""
)