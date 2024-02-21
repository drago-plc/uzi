package com.lomolo.uzi.compose.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.ktx.utils.simplify
import com.lomolo.uzi.DeviceDetails
import com.lomolo.uzi.DeviceDetailsUiState
import com.lomolo.uzi.GetTripDetailsQuery
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation
import com.lomolo.uzi.compose.signin.GetStarted
import com.lomolo.uzi.compose.signin.UserNameDestination
import com.lomolo.uzi.compose.trip.CancelTripState
import com.lomolo.uzi.compose.trip.ComputeTripRouteState
import com.lomolo.uzi.compose.trip.GetTripDetailsState
import com.lomolo.uzi.compose.trip.SearchDropoffLocationScreenDestination
import com.lomolo.uzi.compose.trip.SearchPickupLocationScreenDestination
import com.lomolo.uzi.compose.trip.StartTrip
import com.lomolo.uzi.compose.trip.TripProductsScreenDestination
import com.lomolo.uzi.compose.trip.TripViewModel
import com.lomolo.uzi.model.Session
import com.lomolo.uzi.model.Trip
import com.lomolo.uzi.model.TripStatus
import java.text.NumberFormat

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

    when {
        isAuthed && isOnboarding -> {
            onNavigateTo(UserNameDestination.route)
        }
        else -> {
            if (tripInProgress) {
                TripScreen(
                    tripViewModel = tripViewModel,
                    tripUpdates = tripUpdates,
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

@Composable
private fun DefaultHomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    tripViewModel: TripViewModel,
    deviceDetails: DeviceDetails,
    onGetStartedClick: () -> Unit,
    onEnterTripClick: (String) -> Unit,
    onTripProceed: (String) -> Unit,
    isAuthed: Boolean
) {
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }

    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }

    val cP = CameraPosition(deviceDetails.gps, 17f, 0f, 0f)
    val cameraPositionState = rememberCameraPositionState {
        position = cP
    }
    var errorString: String? = null
    var isMakingTrip = false
    when(val s = tripViewModel.makeTripRouteState) {
        ComputeTripRouteState.Loading -> {isMakingTrip = true}
        is ComputeTripRouteState.Error -> {errorString = s.message}
        else -> {
            isMakingTrip = false
            errorString = null
        }
    }
    val trip by tripViewModel.tripUiInput.collectAsState()

    GoogleMap(
        modifier = modifier,
        properties = mapProperties,
        onMapLoaded = { mainViewModel.setMapLoaded(true) },
        uiSettings = uiSettings,
        cameraPositionState = cameraPositionState
    )
    AnimatedVisibility(
        modifier = modifier,
        visible = deviceDetails.mapLoaded,
        exit = fadeOut(),
        enter = EnterTransition.None
    ) {
        Box(modifier = modifier) {
            if (!isAuthed) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 28.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .wrapContentHeight()
                ) {
                    GetStarted(
                        onGetStartedClick = onGetStartedClick
                    )
                }
            } else {
                StartTrip(
                    Modifier.background(MaterialTheme.colorScheme.background),
                    onEnterPickupClick = { onEnterTripClick(SearchPickupLocationScreenDestination.route) },
                    onEnterDropoffClick = { onEnterTripClick(SearchDropoffLocationScreenDestination.route) },
                    tripViewModel = tripViewModel
                )
                Box(Modifier.align(Alignment.BottomCenter)) {
                    LaunchedEffect(key1 = trip.pickup, key2 = trip.dropoff) {
                        if (tripViewModel.callTripEndpoint() && deviceDetails.mapLoaded) tripViewModel.makeTripRoute()
                    }

                    Column {
                        if (errorString != null) {
                            Text(
                                text = errorString,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                                .height(48.dp),
                            shape = MaterialTheme.shapes.small,
                            onClick = {
                                if (tripViewModel.callTripEndpoint() && !isMakingTrip)
                                    onTripProceed(TripProductsScreenDestination.route)
                            } // TODO proceed with valid trip input details
                        ) {
                            if (isMakingTrip) {
                                Loader()
                            } else {
                                Text(
                                    stringResource(id = R.string.proceed),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeErrorScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.not_your_fault_err),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
        )
        Button(
            onClick = { mainViewModel.getIpinfo() },
        ) {
            Text(
                text = stringResource(R.string.retry),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun TripScreen(
    modifier: Modifier = Modifier,
    tripViewModel: TripViewModel,
    tripUpdates: Trip,
    onNavigateBackHome: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        tripViewModel.getTripDetails()
    }
    val u = tripViewModel.getTripUpdates().collectAsState(initial = null)
    var markerState = LatLng(0.0, 0.0)
    var done by rememberSaveable {
        mutableStateOf(false)
    }
    var polyline: String? = null
    when(val s = tripViewModel.getTripDetailsUiState) {
        is GetTripDetailsState.Success -> {
            if(s.success != null) {
                println(s.success)
                if (s.success.start_location != null)
                    markerState = LatLng(s.success.start_location.lat, s.success.start_location.lng)
                polyline = s.success.route?.polyline
                done = true
            }
        }
    }
    var mapLoaded by rememberSaveable {
        mutableStateOf(false)
    }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }
    var courierHeading by remember {
        mutableFloatStateOf(0.0f)
    }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }
    var route by remember {
        mutableStateOf(PolyUtil.decode(polyline ?: "") ?: listOf<LatLng>())
    }
    val courierPosition = rememberMarkerState(
        position = LatLng(tripUpdates.lat, tripUpdates.lng)
    )

    if (done) {
        val cameraPosition = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(markerState, 17f)
        }
        LaunchedEffect(key1 = tripUpdates) {
            if (tripUpdates.status == TripStatus.COURIER_ARRIVING.toString()) tripViewModel.getTripDetails()
            if (route.isEmpty()) route = PolyUtil.decode(polyline ?: "")
            if (route.isNotEmpty()) {
                courierPosition.position = LatLng(tripUpdates.lat, tripUpdates.lng)
                courierHeading = when (route.size-1) {
                    0 -> SphericalUtil.computeHeading(
                        courierPosition.position,
                        route[0],
                    ).toFloat() - 45

                    else -> {
                        SphericalUtil.computeHeading(
                            courierPosition.position,
                            route[route.size-1],
                        ).toFloat() - 45
                    }
                }
                if (PolyUtil.isLocationOnPath(courierPosition.position, route, true)) {
                    val newRoute = route.subList(
                        0,
                        PolyUtil.locationIndexOnPath(
                            courierPosition.position,
                            route.toMutableList(),
                            true
                        )+1
                    ).toMutableList()
                    newRoute.add(courierPosition.position)
                    route = newRoute.toList()
                }
            }
        }

        GoogleMap(
            uiSettings = uiSettings,
            properties = mapProperties,
            cameraPositionState = cameraPosition,
            modifier = modifier,
            onMapLoaded = {
                mapLoaded = true
            }
        ) {
            if (polyline != null && route.isNotEmpty()) {
                Marker(
                    state = MarkerState(route[0]),
                    zIndex = 1.0f,
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.icons8_filled_circle_30)
                )
                Polyline(
                    zIndex = 1.0f,
                    width = 12f,
                    points = route,
                    geodesic = true,
                )
                Marker(
                    anchor = Offset(0.5f, 0.5f),
                    state = courierPosition,
                    zIndex = 1.0f,
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.icons8_navigation_90___),
                    flat = true,
                    rotation = courierHeading
                )
            }
        }
    } else {
        Loader(
            modifier = Modifier
                .fillMaxSize()
        )
    }
    AnimatedVisibility(
        visible = mapLoaded && done,
        enter = EnterTransition.None,
        exit = fadeOut()
    ) {
       Box(Modifier.fillMaxSize()) {
           Box(Modifier.align(Alignment.BottomCenter)) {
               Column(
                   modifier = Modifier
                       .fillMaxWidth()
                       .background(MaterialTheme.colorScheme.background)
                       .padding(16.dp)
               ) {
                   when (tripUpdates.status) {
                       TripStatus.CREATE.toString() -> {
                           Row(
                               modifier = Modifier
                                   .fillMaxWidth()
                                   .height(64.dp),
                               verticalAlignment = Alignment.CenterVertically
                           ) {
                               Text(
                                   modifier = Modifier
                                       .padding(start = 8.dp),
                                   text = "Matching courier",
                                   style = MaterialTheme.typography.labelMedium
                               )
                               Spacer(modifier = Modifier.weight(1f))
                               Loader()
                           }
                       }
                       TripStatus.COURIER_ARRIVING.toString() -> {
                           val s = tripViewModel.getTripDetailsUiState
                           Text(
                               text = "Your courier is arriving for pickup",
                               style = MaterialTheme.typography.labelMedium
                           )
                           Spacer(modifier = Modifier.size(20.dp))
                           if (s is GetTripDetailsState.Success) {
                               Courier(
                                   courier = s.success!!
                               )
                           } else if (s is GetTripDetailsState.Loading) {
                               Row(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .height(64.dp),
                                   verticalAlignment = Alignment.CenterVertically,
                                   horizontalArrangement = Arrangement.Center
                               ) {
                                   Loader()
                               }
                           }
                       }
                       TripStatus.COURIER_NOT_FOUND.toString() -> {
                           tripViewModel.clearTrips {
                               onNavigateBackHome()
                           }
                       }
                       TripStatus.COURIER_FOUND.toString() -> {
                           Row(
                               modifier = Modifier
                                   .fillMaxWidth()
                                   .height(64.dp),
                               verticalAlignment = Alignment.CenterVertically
                           ) {
                               Text(
                                   modifier = Modifier
                                       .padding(start = 8.dp),
                                   text = "Getting courier details",
                                   style = MaterialTheme.typography.labelMedium
                               )
                               Spacer(modifier = Modifier.weight(1f))
                               Loader()
                           }
                       }
                       TripStatus.CANCELLED.toString() -> {
                           tripViewModel.clearTrips {
                               onNavigateBackHome()
                           }
                       }
                   }
                   if (
                       tripUpdates.status == TripStatus.CREATE.toString() ||
                       tripUpdates.status == TripStatus.COURIER_FOUND.toString()
                       ) {
                       Button(
                           modifier = Modifier
                               .fillMaxWidth()
                               .height(54.dp),
                           shape = MaterialTheme.shapes.small,
                           onClick = {
                               tripViewModel.cancelTrip {
                                   onNavigateBackHome()
                               }
                           }
                       ) {
                          if (tripViewModel.cancelTripUiState !is CancelTripState.Loading) {
                              Text(
                                  "Cancel",
                                  style = MaterialTheme.typography.labelMedium,
                              )
                          } else {
                              Loader()
                          }
                       }
                   }
               }
           }
       }
    }
}

@Composable
private fun Courier(
    modifier: Modifier = Modifier,
    courier: GetTripDetailsQuery.GetTripDetails
) {
    val context = LocalContext.current

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (courier.courier != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(courier.courier.avatar?.uri)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(68.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentDescription = null
                )
            }
            if (courier.courier?.user != null) {
                Column(
                    modifier = Modifier
                        .padding(start=8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${courier.courier.user.first_name} ${courier.courier.user.last_name}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            if (courier.courier?.product != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(courier.courier.product.icon_url)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(42.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.size(32.dp))
        Text(
            "Trip cost KES ${NumberFormat.getNumberInstance().format(courier.cost)}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(start=4.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        if (courier.courier?.user != null) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${courier.courier.user.phone}")
                    }.also { context.startActivity(it) }
                }
            ) {
                Text(
                    "Call",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}