package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lomolo.uzi.compose.navigation.Navigation

object ConfirmTripDetailsDestination: Navigation {
    override val route = "trip/confirm/details"
    override val title = null
}

@Composable
fun ConfirmTripDetails(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    tripViewModel: TripViewModel
) {
    Box(Modifier.fillMaxSize()) {
        ConfirmPickup(tripViewModel = tripViewModel)
        Text(text = "Confirm trip details")
    }
}

@Composable
private fun ConfirmPickup(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    tripViewModel: TripViewModel
) {
    Text(text = "Confirm pickup")
}