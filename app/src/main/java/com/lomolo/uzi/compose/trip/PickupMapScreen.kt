package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.navigation.Navigation
import kotlinx.coroutines.launch

object PickupMapScreenDestination: Navigation {
    override val route = "trip/pickup/map"
    override val title = R.string.pickup_location
}

@Composable
fun PickupMap(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    tripViewModel: TripViewModel,
    onNavigateUp: () -> Unit,
    onNavigateBackToTrip: () -> Unit
) {
    val deviceDetails by mainViewModel.deviceDetailsUiState.collectAsState()
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }
    var isMapLoaded by rememberSaveable {
        mutableStateOf(false)
    }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }
    val cP = CameraPosition(deviceDetails.gps, 17f, 0f, 0f)
    val cameraPositionState = rememberCameraPositionState {
        position = cP
    }
    val scope = rememberCoroutineScope()

    if (cameraPositionState.isMoving) {
        tripViewModel.setPickup(cameraPositionState.position.target)
    }

    Scaffold {
        Surface(
            modifier
                .fillMaxSize()
                .padding(it)
        ) {
            GoogleMap(
                properties = mapProperties,
                onMapLoaded = { isMapLoaded = true },
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            )
            if (isMapLoaded) {
                Box(
                    Modifier.padding(8.dp)
                ) {
                    IconButton(
                        onClick = { onNavigateUp() },
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceTint,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.TwoTone.ArrowBack,
                            tint = MaterialTheme.colorScheme.background,
                            contentDescription = null
                        )
                    }
                    Image(
                        painterResource(R.drawable.icons8_pin_100),
                        modifier = Modifier
                            .align(Alignment.Center),
                        contentDescription = null
                    )
                    Column(
                        Modifier
                            .align(Alignment.BottomCenter)
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(deviceDetails.gps, 17f), 1000)
                                }

                            },
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                                .align(Alignment.End)
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_launcher_foreground),
                                modifier = Modifier
                                    .size(36.dp),
                                tint = MaterialTheme.colorScheme.background,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.size(28.dp))
                        Button(
                            onClick = { onNavigateBackToTrip() },
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}