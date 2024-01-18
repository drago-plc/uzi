package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TripViewModel: ViewModel() {
    var searchingLocationState: LocationPredicateState by mutableStateOf(LocationPredicateState.Success(false))
        private set
}

interface LocationPredicateState {
    data class Success(val success: Boolean): LocationPredicateState
    data object Loading: LocationPredicateState
    data class Error(val message: String?): LocationPredicateState
}