package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TripViewModel: ViewModel() {
    var searchingLocationState: LocationPredicateState by mutableStateOf(LocationPredicateState.Success(false))
        private set

    private var _trip = MutableStateFlow(Trip())
    val trip: StateFlow<Trip> = _trip.asStateFlow()

    fun setPickup(pickup: LatLng) {
        _trip.update {
            it.copy(pickup = pickup)
        }
    }
}

interface LocationPredicateState {
    data class Success(val success: Boolean): LocationPredicateState
    data object Loading: LocationPredicateState
    data class Error(val message: String?): LocationPredicateState
}

data class Trip(
    val pickup: LatLng = LatLng(0.0, 0.0),
    val dropoff: LatLng = LatLng(0.0, 0.0)
)