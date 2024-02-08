package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.gms.maps.model.LatLng
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.google.maps.android.compose.DragState
import com.lomolo.uzi.GetCourierNearPickupPointQuery
import com.lomolo.uzi.ComputeTripRouteQuery
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.ReverseGeocodeQuery
import com.lomolo.uzi.SearchPlaceQuery
import com.lomolo.uzi.model.SignIn
import com.lomolo.uzi.network.UziGqlApiInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

class TripViewModel(
    private val uziGqlApiRepository: UziGqlApiInterface,
    private val mainViewModel: MainViewModel
): ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    var tripProductId by mutableStateOf("")
        private set

    var searchingLocationState: LocationPredicateState by mutableStateOf(LocationPredicateState.Success(
        listOf()
    ))
        private set

    var pickupGeocodeState: PickupGeocodeState by mutableStateOf(PickupGeocodeState.Success(null))
        private set

    var dropoffGeocodeState: DropoffGeocodeState by mutableStateOf(DropoffGeocodeState.Success(null))
        private set

    var makeTripRouteState: ComputeTripRouteState by mutableStateOf(ComputeTripRouteState.Success(null))
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

    val phoneUtil = PhoneNumberUtil.getInstance()
    private fun isPhoneNumberValid(number: String): Boolean {
        return try {
            if (number.isEmpty()) return false
            val p = Phonenumber.PhoneNumber()
            p.countryCode = mainViewModel.deviceDetailsUiState.value.countryPhoneCode.toInt()
            p.nationalNumber = number.toLong()
            return phoneUtil.isValidNumber(p)
        } catch(e: Exception) {
            false
        }
    }
    private fun parsePhoneNumber(number: String): String {
        val p = phoneUtil.parse(number, mainViewModel.deviceDetailsUiState.value.country)
        return p.countryCode.toString()+p.nationalNumber.toString()
    }
    fun isPhoneValid(uiState: TripDetails): Boolean {
        return with(uiState) {
            isPhoneNumberValid(phone)
        }
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

    fun setTripProduct(id: String) {
        tripProductId = id
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
        if (makeTripRouteState !is ComputeTripRouteState.Loading) {
            makeTripRouteState = ComputeTripRouteState.Loading
            viewModelScope.launch {
                makeTripRouteState = try {
                    val res = uziGqlApiRepository.makeTripRoute(
                        pickup = _trip.value.pickup,
                        dropoff = _trip.value.dropoff
                    ).dataOrThrow()
                    ComputeTripRouteState.Success(res.computeTripRoute)
                } catch(e: IOException) {
                    ComputeTripRouteState.Error(e.message)
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

    fun tripDetailsValid(uiState: TripDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && isPhoneValid(uiState)
        }
    }

    fun isNameValid(name: String): Boolean {
        return name.trim().isNotBlank() && name.trim().matches(Regex("^[a-zA-Z ]*$"))
    }

    fun setTripDetailsName(name: String) {
        _trip.update {
            val tripDetails = it.details.copy(name = name)
            it.copy(details = tripDetails)
        }
    }

    fun setTripDetailsBuilding(building: String) {
        _trip.update {
            val tripDetails = it.details.copy(buildName = building)
            it.copy(details = tripDetails)
        }
    }

    fun setTripDetailsUnit(name: String) {
        _trip.update {
            val tripDetails = it.details.copy(flatOrOffice = name)
            it.copy(details = tripDetails)
        }
    }

    fun setTripDetailsPhone(phone: String) {
        _trip.update {
            val tripDetails = it.details.copy(phone = phone)
            it.copy(details = tripDetails)
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
    val details: TripDetails = TripDetails(),
    val dropoff: ReverseGeocodeQuery.ReverseGeocode = ReverseGeocodeQuery.ReverseGeocode("", "", ReverseGeocodeQuery.Location(0.0, 0.0))
)
 data class TripDetails(
     val name: String = "",
     var buildName: String = "",
     val flatOrOffice: String = "",
     val phone: String = ""
 )

interface ComputeTripRouteState {
    data class Success(val success: ComputeTripRouteQuery.ComputeTripRoute?): ComputeTripRouteState
    data object Loading: ComputeTripRouteState
    data class Error(val message: String?): ComputeTripRouteState
}

interface GetCourierNearPickupState {
    data class Success(val success: List<GetCourierNearPickupPointQuery.GetCourierNearPickupPoint>): GetCourierNearPickupState
    data object Loading: GetCourierNearPickupState
    data class Error(val message: String?): GetCourierNearPickupState
}