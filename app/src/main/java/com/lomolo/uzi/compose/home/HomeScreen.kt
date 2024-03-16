package com.lomolo.uzi.compose.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.DeviceDetails
import com.lomolo.uzi.DeviceDetailsUiState
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation
import com.lomolo.uzi.compose.signin.UserNameDestination
import com.lomolo.uzi.compose.trip.GetTripDetailsState
import com.lomolo.uzi.compose.trip.TripViewModel
import com.lomolo.uzi.model.Session
import com.lomolo.uzi.model.Trip

object HomeScreenDestination: Navigation {
    override val route = "home"
    override val title = null
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(),
    tripViewModel: TripViewModel,
    deviceDetails: DeviceDetails,
    onGetStartedClick: () -> Unit = {},
    onNavigateTo: (String) -> Unit = {},
    onNavigateToTrip: (String) -> Unit = {},
    session: Session,
    tripUpdates: Trip
) {
    Box(modifier.fillMaxSize()) {
        when(mainViewModel.deviceDetailsState) {
            is DeviceDetailsUiState.Loading -> Loader(
                modifier = Modifier.matchParentSize()
            )
            is DeviceDetailsUiState.Error -> {
                HomeErrorScreen(
                    mainViewModel = mainViewModel,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is DeviceDetailsUiState.Success -> {
                HomeSuccessScreen(
                    modifier = Modifier.matchParentSize(),
                    mainViewModel = mainViewModel,
                    tripViewModel = tripViewModel,
                    deviceDetails = deviceDetails,
                    onGetStartedClick = onGetStartedClick,
                    onNavigateTo = onNavigateTo,
                    onNavigateToTrip = onNavigateToTrip,
                    session = session,
                    tripUpdates = tripUpdates
                )
            }
        }
    }
}

@Composable
private fun HomeSuccessScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    tripViewModel: TripViewModel,
    deviceDetails: DeviceDetails,
    onGetStartedClick: () -> Unit,
    onNavigateToTrip: (String) -> Unit,
    session: Session,
    onNavigateTo: (String) -> Unit = {},
    tripUpdates: Trip
) {
    val isAuthed = session.token.isNotBlank()
    val isOnboarding = session.onboarding
    val tripInProgress = tripUpdates.id.isNotBlank()
    val context = LocalContext.current


    when {
        isAuthed && isOnboarding -> {
            onNavigateTo(UserNameDestination.route)
        }
        else -> {
            val uiSettings by remember {
                mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
            }
            val mapProperties by remember {
                mutableStateOf(
                    MapProperties(
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.style_json
                        )
                    )
                )
            }
            var deviceCameraPosition by remember {
                mutableStateOf(deviceDetails.gps)
            }
            val cP = CameraPosition(deviceCameraPosition, 17f, 0f, 0f)
            val cameraPositionState = rememberCameraPositionState {
                position = cP
            }
            var polyline by remember {
                mutableStateOf(listOf<LatLng>())
            }
            var computeHeading by remember {
                mutableFloatStateOf(0.0f)
            }

            GoogleMap(
                modifier = modifier,
                properties = mapProperties,
                onMapLoaded = { mainViewModel.setMapLoaded(true) },
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            ) {
                if (tripInProgress) {
                    Polyline(
                        width = 12f,
                        geodesic = true,
                        points = polyline.toList(),
                        zIndex = 1f,
                        endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.icons8_filled_circle_30))
                    )
                    if (polyline.isNotEmpty()) {
                        Marker(
                            zIndex = 1f,
                            state = MarkerState(polyline.first()),
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.icons8_navigation_100__2_),
                            flat = true,
                            rotation = computeHeading,
                            anchor = Offset(0.5f, 0.5f)
                        )
                    }
                }
            }
            if (tripInProgress) {
                tripViewModel.getTripUpdates().collectAsState(initial = null)
                LaunchedEffect(Unit) {
                    tripViewModel.getTripDetails()
                }
                LaunchedEffect(key1 = tripViewModel.getTripDetailsUiState, key3 = deviceCameraPosition, key2 = tripUpdates) {
                    val s = tripViewModel.getTripDetailsUiState
                    // Repopulates data for genesis notification
                    if (s is GetTripDetailsState.Success && s.success == null) tripViewModel.getTripDetails()
                    var courierIndex = 0
                    if (s is GetTripDetailsState.Success) {
                        if (tripUpdates.lat != 0.0 && tripUpdates.lng != 0.0) {
                            val courierGps = LatLng(tripUpdates.lat, tripUpdates.lng)
                            deviceCameraPosition = courierGps
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    deviceCameraPosition,
                                    17f
                                ), 2500
                            )
                        }
                        if (polyline.isEmpty()) polyline = PolyUtil.decode(s.success?.route?.polyline ?: "")
                    }
                    if (PolyUtil.isLocationOnPath(deviceCameraPosition, polyline, true)) {
                        courierIndex = PolyUtil.locationIndexOnPath(deviceCameraPosition, polyline, true)
                        val newRoute = polyline.subList(
                            courierIndex+1,
                            polyline.size
                        ).toMutableList()
                        polyline = newRoute
                    }
                    computeHeading = when(polyline.size) {
                        0 -> 0f - 60
                        1 -> SphericalUtil.computeHeading(deviceCameraPosition, polyline[0]).toFloat()-45
                        else -> SphericalUtil.computeHeading(deviceCameraPosition, polyline[courierIndex+1]).toFloat()-45
                    }
                }

                TripScreen(
                    tripViewModel = tripViewModel,
                    tripUpdates = tripUpdates,
                    mapLoaded = deviceDetails.mapLoaded,
                    courierArrived = polyline.size == 1,
                    onNavigateBackHome = {
                        onNavigateTo(HomeScreenDestination.route)
                    }
                )
            } else {
                DefaultHomeScreen(
                    modifier = modifier,
                    mainViewModel = mainViewModel,
                    tripViewModel = tripViewModel,
                    deviceDetails = deviceDetails,
                    onGetStartedClick = onGetStartedClick,
                    onEnterTripClick = onNavigateToTrip,
                    onTripProceed = onNavigateTo,
                    isAuthed = isAuthed
                )
            }
        }
    }
}

