package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.navigation.Navigation

object PickupMapScreenDestination: Navigation {
    override val route = "trip/pickup/map"
    override val title = R.string.pickup_location
}

@Composable
fun PickupMap(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    tripViewModel: TripViewModel,
    onNavigateBackToTrip: () -> Unit,
) {
    val pickupValue = when(val s = tripViewModel.pickupGeocodeState) {
        PickupGeocodeState.Loading -> {
            "Loading..."
        }
        is PickupGeocodeState.Error -> {
            "Unnamed street"
        }
        is PickupGeocodeState.Success -> {
            s.geocode?.formattedAddress
        }
        else -> {""}
    }

    Map(
        modifier = modifier,
        mainViewModel = mainViewModel,
        tripViewModel = tripViewModel,
        onNavigateBackToTrip = onNavigateBackToTrip,
        address = pickupValue,
        onReverseGeocode = {
            tripViewModel.pickupReverseGeocode(it)
        },
        onLocationConfirmation = {
            tripViewModel.pickupReverseGeocode(it) {
                tripViewModel.stopMapDrag()
                tripViewModel.resetMapDrag()
            }
        },
        onConfirmationClick = {
            val geocode = when(val s = tripViewModel.pickupGeocodeState) {
                is PickupGeocodeState.Success -> {
                    s.geocode
                }
                else -> {null}
            }

            if (geocode != null) {
                tripViewModel.setPickup(geocode)
                onNavigateBackToTrip()
            }
        }
    )
}