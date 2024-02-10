package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.navigation.Navigation

object DropoffMapScreenDestination: Navigation {
    override val route = "trip/drop_off/map"
    override val title = R.string.drop_location
}

@Composable
fun DropoffMap(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    tripViewModel: TripViewModel,
    onNavigateBackToTrip: () -> Unit,
) {
    val dropoffValue = when(val s = tripViewModel.dropoffGeocodeState) {
        DropoffGeocodeState.Loading -> {
            "Loading..."
        }
        is DropoffGeocodeState.Error -> {
            "Unnamed street"
        }
        is DropoffGeocodeState.Success -> {
            s.geocode?.formattedAddress
        }
        else -> {""}
    }

    Map(
        modifier = modifier,
        mainViewModel = mainViewModel,
        tripViewModel = tripViewModel,
        onNavigateBackToTrip = onNavigateBackToTrip,
        address = dropoffValue,
        onReverseGeocode = {
            tripViewModel.dropoffReverseGeocode(it)
        },
        onLocationConfirmation = {
            tripViewModel.dropoffReverseGeocode(it) {
                tripViewModel.stopMapDrag()
                tripViewModel.resetMapDrag()
            }
        },
        onConfirmationClick = {
            val geocode = when(val s = tripViewModel.dropoffGeocodeState) {
                is DropoffGeocodeState.Success -> {
                    s.geocode
                }
                else -> {null}
            }

            if (geocode != null) {
                tripViewModel.setDropoff(geocode)
                onNavigateBackToTrip()
            }
        }
    )
}