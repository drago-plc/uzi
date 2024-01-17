package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.navigation.Navigation

object PickupMapScreenDestination: Navigation {
    override val route = "trip/pickup"
    override val title = R.string.pickup_location
}

@Composable
fun PickupMap(
    modifier: Modifier = Modifier
) {}