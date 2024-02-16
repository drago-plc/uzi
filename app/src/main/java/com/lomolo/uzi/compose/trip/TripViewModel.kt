package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.gms.maps.model.LatLng
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.google.maps.android.compose.DragState
import com.lomolo.uzi.GetCourierNearPickupPointQuery
import com.lomolo.uzi.ComputeTripRouteQuery
import com.lomolo.uzi.CreateTripMutation
import com.lomolo.uzi.GetTripDetailsQuery
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.ReverseGeocodeQuery
import com.lomolo.uzi.SearchPlaceQuery
import com.lomolo.uzi.model.TripStatus
import com.lomolo.uzi.network.UziGqlApiInterface
import com.lomolo.uzi.repository.TripInterface
import com.lomolo.uzi.type.CreateTripInput
import com.lomolo.uzi.type.GpsInput
import com.lomolo.uzi.type.TripInput
import com.lomolo.uzi.type.TripRecipientInput
import com.lomolo.uzi.type.TripRouteInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

class TripViewModel(
    private val uziGqlApiRepository: UziGqlApiInterface,
    private val mainViewModel: MainViewModel,
    private val tripRepository: TripInterface
): ViewModel() {
    val tripUpdatesUiState: StateFlow<com.lomolo.uzi.model.Trip> = tripRepository
        .getTrip()
        .filterNotNull()
        .map {
            com.lomolo.uzi.model.Trip(
                id = it.id,
                status = it.status,
                lat = it.lat,
                lng = it.lng
            )
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = com.lomolo.uzi.model.Trip(),
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
        )

    companion object {
        private const val TIMEOUT_MILLIS = 2_000L
    }
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

    var confirmedPickup: ReverseGeocodeConfirmedPickup by mutableStateOf(ReverseGeocodeConfirmedPickup.Success(null))
        private set

    var createTripState: CreateTripState by mutableStateOf(CreateTripState.Success(null))
        private set

    var getTripDetailsUiState: GetTripDetailsState by mutableStateOf(GetTripDetailsState.Success(null))
        private set

    private var _trip = MutableStateFlow(Trip())
    val tripUiInput: StateFlow<Trip> = _trip.asStateFlow()

    var mapDragState: DragState by mutableStateOf(DragState.START)
        private set
    fun startMapDrag() {
        mapDragState = DragState.DRAG
    }
    fun stopMapDrag() {
        mapDragState = DragState.END
    }
    fun resetMapDrag() {
        mapDragState = DragState.START
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

    fun setConfirmedPickup(confirmed: ReverseGeocodeQuery.ReverseGeocode) {
        _trip.update {
            it.copy(confirmedPickup = confirmed)
        }
    }

    fun reverseGeocodeConfirmedPickup(cords: LatLng, cb: (ReverseGeocodeQuery.ReverseGeocode) -> Unit = {}) {
        confirmedPickup = ReverseGeocodeConfirmedPickup.Loading
        viewModelScope.launch {
            confirmedPickup = try {
                val res = reverseGeocode(cords)
                ReverseGeocodeConfirmedPickup.Success(res.reverseGeocode).also { cb(res.reverseGeocode!!) }
            } catch (e: ApolloException) {
                ReverseGeocodeConfirmedPickup.Error(e.message)
            }
        }
    }

    private fun createTripInput(): CreateTripInput {
        val _confirmedPickup: TripInput = when(val s = confirmedPickup) {
            is ReverseGeocodeConfirmedPickup.Success -> {
                val place = s.success
                if (place != null) {
                    TripInput(place.placeId, place.formattedAddress, GpsInput(place.location.lat, place.location.lng))
                } else {
                    TripInput("", "", GpsInput(0.0, 0.0))
                }
            }
            else -> {
                TripInput("", "", GpsInput(0.0, 0.0))
            }
        }

        return CreateTripInput(
            tripInput = TripRouteInput(
                pickup = TripInput(
                    placeId = _trip.value.pickup.placeId,
                    formattedAddress = _trip.value.pickup.formattedAddress,
                    location = GpsInput(_trip.value.pickup.location.lat, _trip.value.pickup.location.lng)
                ),
                dropoff = TripInput(
                    placeId = _trip.value.dropoff.placeId,
                    formattedAddress = _trip.value.dropoff.formattedAddress,
                    location = GpsInput(_trip.value.dropoff.location.lat, _trip.value.dropoff.location.lng)
                )
            ),
            confirmedPickup = _confirmedPickup,
            recipient = TripRecipientInput(
                name = _trip.value.details.name,
                building_name = Optional.presentIfNotNull(_trip.value.details.buildName),
                unit_name = Optional.presentIfNotNull(_trip.value.details.flatOrOffice),
                phone = _trip.value.details.phone
            ),
            tripProductId = tripProductId
        )
    }
    fun createTrip(cb: () -> Unit = {}) {
        if (createTripState !is CreateTripState.Loading) {
            createTripState = CreateTripState.Loading
            viewModelScope.launch {
                createTripState = try {
                    val res = uziGqlApiRepository.createTrip(createTripInput()).dataOrThrow()
                    tripRepository.createTrip(
                        com.lomolo.uzi.model.Trip(
                            res.createTrip.id.toString(),
                            TripStatus.CREATE.toString(),
                            com.lomolo.uzi.model.Trip().lat,
                            com.lomolo.uzi.model.Trip().lng
                        )
                    )
                    CreateTripState.Success(res.createTrip).also { cb() }
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    CreateTripState.Error(e.message)
                }
            }
        }
    }

    fun getTripDetails() {
        if (tripUpdatesUiState.value.id.isNotBlank()) {
            getTripDetailsUiState = GetTripDetailsState.Loading
            viewModelScope.launch {
                getTripDetailsUiState = try {
                    delay(4_000L)
                    val res = uziGqlApiRepository.getTripDetails(tripUpdatesUiState.value.id)
                        .dataOrThrow()
                    GetTripDetailsState.Success(res.getTripDetails)
                } catch (e: ApolloException) {
                    GetTripDetailsState.Error(e.message)
                }
            }
        }
    }

    fun clearTrips() = viewModelScope.launch {
        try {
            tripRepository.clearTrips()
        } catch(e: Throwable) {
            e.printStackTrace()
        }
    }

    fun resetTrip() {
        //_trip.value = Trip() TODO just for testing(revert once ready)
    }

    fun getTripUpdates() = tripRepository.getTripUpdates(tripUpdatesUiState.value.id)
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
    val dropoff: ReverseGeocodeQuery.ReverseGeocode = ReverseGeocodeQuery.ReverseGeocode("", "", ReverseGeocodeQuery.Location(0.0, 0.0)),
    val confirmedPickup: ReverseGeocodeQuery.ReverseGeocode = ReverseGeocodeQuery.ReverseGeocode("", "", ReverseGeocodeQuery.Location(0.0, 0.0))
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

interface ReverseGeocodeConfirmedPickup {
    data class Success(val success: ReverseGeocodeQuery.ReverseGeocode?): ReverseGeocodeConfirmedPickup
    data object Loading: ReverseGeocodeConfirmedPickup
    data class Error(val message: String?): ReverseGeocodeConfirmedPickup
}

interface CreateTripState {
    data class Success(val success: CreateTripMutation.CreateTrip?): CreateTripState
    data object Loading: CreateTripState
    data class Error(val message: String?): CreateTripState
}

interface GetTripDetailsState {
    data class Success(val success: GetTripDetailsQuery.GetTripDetails?): GetTripDetailsState
    data object Loading: GetTripDetailsState
    data class Error(val message: String?): GetTripDetailsState
}