package com.lomolo.uzi.compose.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.lomolo.uzi.compose.map.GoogleMapScreen
import com.lomolo.uzi.compose.navigation.Navigation

object HomeScreenDestination: Navigation {
    override val route = "home"
    override val title = null
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    deviceLocation: LatLng
) {
    GoogleMapScreen(
        deviceLocation = deviceLocation
    )
}