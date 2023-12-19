package com.lomolo.uzi.compose.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.DeviceDetailsUiState
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

    Box(modifier.fillMaxSize()) {
        when(val s = mainViewModel.deviceDetailsState) {
            is DeviceDetailsUiState.Loading -> Loader(
                modifier = Modifier.matchParentSize()
            )
            is DeviceDetailsUiState.Error -> {
                Text(
                    text = "Something went wrong and it's not your fault",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
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
                    uiSettings = uiSettings,
                    cameraPositionState = cameraPositionState
                )
            }
        }
    }
}