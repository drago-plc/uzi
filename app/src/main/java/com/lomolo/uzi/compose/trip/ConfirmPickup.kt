package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.home.HomeScreenDestination
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation

object ConfirmTripPickupDestination: Navigation {
    override val route = "trip/confirm/pickup"
    override val title = null
}

@Composable
fun ConfirmTripPickup(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    tripViewModel: TripViewModel,
    onNavigateTo: (String) -> Unit = {},
) {
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }
    var isMapLoaded by rememberSaveable {
        mutableStateOf(false)
    }
    var pickupPoint: LatLng? = null
    when(val s = tripViewModel.makeTripRouteState) {
        is ComputeTripRouteState.Success -> {
            if (s.success?.polyline != null) {
                val polyline = PolyUtil.decode(s.success.polyline)
                pickupPoint = polyline[0]
            }
        }
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(pickupPoint ?: LatLng(0.0, 0.0), 18f)
    }

    LaunchedEffect((cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE)) {
        if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            tripViewModel.startMapDrag()
        } else {
            if (tripViewModel.mapDragState == DragState.DRAG) {
                tripViewModel.reverseGeocodeConfirmedPickup(cameraPositionState.position.target) {
                    tripViewModel.setConfirmedPickup(it)
                }
            }
        }
    }

    Box(modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            uiSettings = uiSettings,
            properties = mapProperties,
            onMapLoaded = { isMapLoaded = true },
            cameraPositionState = cameraPositionState
        )
        Box(Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceTint,
                            CircleShape
                        ),
                    onClick = {
                        onNavigateUp()
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.TwoTone.ArrowBack,
                        tint = MaterialTheme.colorScheme.background,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Confirm pickup location",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        Image(
            painterResource(R.drawable.icons8_location_pin_90___),
            modifier = Modifier
                .align(Alignment.Center),
            contentDescription = null
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    tripViewModel.createTrip {
                        onNavigateTo(HomeScreenDestination.route)
                    }
                }
            ) {
               if (tripViewModel.createTripState is CreateTripState.Loading) {
                  Loader()
               } else {
                   Text(
                       text = "Confirm",
                       style = MaterialTheme.typography.labelMedium
                   )
               }
            }
        }
    }
}