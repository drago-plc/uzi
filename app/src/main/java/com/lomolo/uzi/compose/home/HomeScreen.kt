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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Call
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.DeviceDetails
import com.lomolo.uzi.DeviceDetailsUiState
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation
import com.lomolo.uzi.compose.signin.GetStarted
import com.lomolo.uzi.compose.signin.UserNameDestination
import com.lomolo.uzi.compose.trip.ComputeTripRouteState
import com.lomolo.uzi.compose.trip.GetTripDetailsState
import com.lomolo.uzi.compose.trip.SearchDropoffLocationScreenDestination
import com.lomolo.uzi.compose.trip.SearchPickupLocationScreenDestination
import com.lomolo.uzi.compose.trip.StartTrip
import com.lomolo.uzi.compose.trip.TripProductsScreenDestination
import com.lomolo.uzi.compose.trip.TripViewModel
import com.lomolo.uzi.model.Session

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
    session: Session
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
                    session = session
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
) {
    val isAuthed = session.token.isNotBlank()
    val isOnboarding = session.onboarding

    when {
        isAuthed && isOnboarding -> {
            onNavigateTo(UserNameDestination.route)
        }
        else -> {
            TripScreen(
                tripViewModel = tripViewModel
            )
            /*
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
             */
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
    tripViewModel: TripViewModel
) {
    var mapLoaded by rememberSaveable {
        mutableStateOf(false)
    }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }

    GoogleMap(
        uiSettings = uiSettings,
        properties = mapProperties,
        modifier = modifier,
        onMapLoaded = {
            mapLoaded = true
        }
    )
    AnimatedVisibility(
        visible = mapLoaded,
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
                   Row(
                       verticalAlignment = Alignment.CenterVertically
                   ) {
                       Text(
                           modifier = Modifier
                               .padding(start = 8.dp),
                           text = "Matching courier",
                           style = MaterialTheme.typography.labelMedium
                       )
                       Spacer(modifier = Modifier.size(16.dp))
                       Loader()
                   }
                   Courier(
                       tripViewModel = tripViewModel
                   )
               }
           }
       }
    }
}

@Composable
private fun Courier(
    modifier: Modifier = Modifier,
    tripViewModel: TripViewModel
) {
    val context = LocalContext.current
    val trip = when(val s = tripViewModel.getTripDetailsUiState) {
        is GetTripDetailsState.Success -> {
            s.success
        }
        else -> {null}
    }

    LaunchedEffect(Unit) {
        tripViewModel.getTripDetails()
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://uzi-images.s3.eu-west-2.amazonaws.com/FEEdsVaWQAQ76vr.jpeg")
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
        Column(
            modifier = Modifier
                .padding(start=8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "John Doe",
                    style = MaterialTheme.typography.labelMedium
                )
                IconButton(
                    onClick = {
                        Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:0849828440")
                        }.also { context.startActivity(it) }
                    }
                ) {
                    Icon(Icons.TwoTone.Call, contentDescription = null)
                }
            }
            // TODO show based on product courier product(bike/boda)
            Text(
                "KDJ 425T",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://uzi-images.s3.eu-west-2.amazonaws.com/icons8-bike-50.png")
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