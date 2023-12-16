package com.lomolo.uzi.compose.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.compose.loader.Loader

@Composable
fun GoogleMapScreen(
    modifier: Modifier = Modifier,
    deviceLocation: LatLng
) {
    val gps = LatLng(deviceLocation.longitude, deviceLocation.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(gps, 15f)
    }
    var isMapLoaded by remember { mutableStateOf(false) }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(compassEnabled = false))
    }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            properties = mapProperties,
            onMapLoaded = {
                isMapLoaded = true
            },
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState
        )
        if (!isMapLoaded) {
            AnimatedVisibility(
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut(),
                modifier = Modifier
                    .matchParentSize()
            ) {
                Loader()
            }
        }
    }
}