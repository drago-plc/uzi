package com.lomolo.uzi.compose.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation

object HomeScreenDestination: Navigation {
    override val route = "home"
    override val title = null
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(),
) {
    val deviceDetails by mainViewModel.deviceDetailsUiState.collectAsState()
    val isValidGps = {
        val gps = deviceDetails.gps
        gps.latitude != 0.0 && gps.longitude != 0.0
    }

    Box(modifier.fillMaxSize()) {
        if (isValidGps()) {
            val uiSettings by remember {
                mutableStateOf(MapUiSettings())
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
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            )
        } else {
            Loader(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}