package com.lomolo.uzi.compose.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lomolo.uzi.DeviceDetails
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.signin.GetStarted
import com.lomolo.uzi.compose.signin.SessionViewModel
import com.lomolo.uzi.compose.signin.User
import com.lomolo.uzi.compose.trip.ComputeTripRouteState
import com.lomolo.uzi.compose.trip.SearchDropoffLocationScreenDestination
import com.lomolo.uzi.compose.trip.SearchPickupLocationScreenDestination
import com.lomolo.uzi.compose.trip.StartTrip
import com.lomolo.uzi.compose.trip.TripProductsScreenDestination
import com.lomolo.uzi.compose.trip.TripViewModel

@Composable
internal fun DefaultHomeScreen(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel,
    tripViewModel: TripViewModel,
    deviceDetails: DeviceDetails,
    onGetStartedClick: () -> Unit,
    onEnterTripClick: (String) -> Unit,
    onTripProceed: (String) -> Unit,
    isAuthed: Boolean
) {
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


    AnimatedVisibility(
        modifier = modifier,
        visible = deviceDetails.mapLoaded,
        exit = fadeOut(),
        enter = EnterTransition.None
    ) {
        Box(modifier = modifier) {
            if (!isAuthed && !sessionViewModel.signingIn) {
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
            } else if (sessionViewModel.signingIn) {
                User(sessionViewModel = sessionViewModel, deviceDetails = deviceDetails)
            } else {
                StartTrip(
                    Modifier.background(MaterialTheme.colorScheme.background),
                    onEnterPickupClick = { onEnterTripClick(SearchPickupLocationScreenDestination.route) },
                    onEnterDropoffClick = { onEnterTripClick(SearchDropoffLocationScreenDestination.route) },
                    tripViewModel = tripViewModel
                )
                Box(
                    Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 28.dp)
                        .align(Alignment.BottomCenter)
                ) {
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
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
