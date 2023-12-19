package com.lomolo.uzi.compose.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.DeviceDetailsUiState
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation
import com.lomolo.uzi.compose.signin.GetStarted
import com.lomolo.uzi.compose.trip.StartTrip

object HomeScreenDestination: Navigation {
    override val route = "home"
    override val title = null
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(),
    onGetStartedClick: () -> Unit = {}
) {
    val deviceDetails by mainViewModel.deviceDetailsUiState.collectAsState()

    Box(modifier.fillMaxSize()) {
        when(mainViewModel.deviceDetailsState) {
            is DeviceDetailsUiState.Loading -> Loader(
                modifier = Modifier.matchParentSize()
            )
            is DeviceDetailsUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
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
                        shape = MaterialTheme.shapes.small
                    ) {
                       Text(
                           text = stringResource(R.string.retry)
                       )
                    }
                }
            }
            is DeviceDetailsUiState.Success -> {
                val uiSettings by remember {
                    mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
                }
                val mapProperties by remember {
                    mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
                }
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        deviceDetails.gps,
                        16f
                    )
                }

                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    properties = mapProperties,
                    onMapLoaded = { mainViewModel.setMapLoaded(true) },
                    uiSettings = uiSettings,
                    cameraPositionState = cameraPositionState
                )
                AnimatedVisibility(
                    visible = deviceDetails.mapLoaded,
                    modifier = Modifier.matchParentSize(),
                    exit = fadeOut(),
                    enter = EnterTransition.None
                ) {
                    Box(Modifier.matchParentSize()) {
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .align(Alignment.TopCenter)
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    shape = MaterialTheme.shapes.small
                                )
                        ) {
                            StartTrip()
                        }
                        Box(
                            modifier = Modifier
                                .padding(bottom = 28.dp, start = 8.dp, end = 8.dp)
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .wrapContentHeight()
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    shape = MaterialTheme.shapes.small
                                )
                        ) {
                            GetStarted(
                                onGetStartedClick = onGetStartedClick
                            )
                        }
                    }
                }
            }
        }
    }
}