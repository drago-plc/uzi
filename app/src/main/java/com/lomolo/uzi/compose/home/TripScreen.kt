package com.lomolo.uzi.compose.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.trip.CancelTripState
import com.lomolo.uzi.compose.trip.GetTripDetailsState
import com.lomolo.uzi.compose.trip.TripViewModel
import com.lomolo.uzi.model.Trip
import com.lomolo.uzi.model.TripStatus

@Composable
internal fun TripScreen(
    modifier: Modifier = Modifier,
    tripViewModel: TripViewModel,
    mapLoaded: Boolean,
    tripUpdates: Trip,
    onNavigateBackHome: () -> Unit = {}
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = mapLoaded,
        enter = EnterTransition.None,
        exit = fadeOut()
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.align(Alignment.BottomCenter)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (tripUpdates.status) {
                            TripStatus.CREATE.toString() -> {
                                Text(
                                    modifier = Modifier
                                        .padding(start = 8.dp),
                                    text = "Finding courier",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Loader()
                            }
                            TripStatus.COURIER_ASSIGNED.toString() -> {
                                Text(
                                    modifier = Modifier
                                        .padding(start = 8.dp),
                                    text = "Getting courier details",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Loader()
                            }
                            TripStatus.COURIER_ARRIVING.toString(), TripStatus.COURIER_EN_ROUTE.toString() -> {
                                Column {
                                    Text(
                                        text = "Your courier is arriving for pickup",
                                        textAlign = TextAlign.Start,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    when (val s = tripViewModel.getTripDetailsUiState) {
                                        is GetTripDetailsState.Success -> {
                                            Courier(
                                                courier = s.success!!,
                                                tripViewModel = tripViewModel
                                            )
                                        }

                                        GetTripDetailsState.Loading -> Loader(
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }
                                }
                            }
                            TripStatus.COURIER_NOT_FOUND.toString() -> {
                                tripViewModel.clearTrips {
                                    onNavigateBackHome()
                                }
                            }
                            TripStatus.CANCELLED.toString(), TripStatus.COMPLETE.toString() -> {
                                tripViewModel.clearTrips {
                                    onNavigateBackHome()
                                }
                            }
                        }
                    }
                    if (tripUpdates.status == TripStatus.CREATE.toString()) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = MaterialTheme.shapes.small,
                            onClick = {
                                tripViewModel.cancelTrip {
                                    onNavigateBackHome()
                                }
                            }
                        ) {
                            if (tripViewModel.cancelTripUiState !is CancelTripState.Loading) {
                                Text(
                                    "Cancel",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            } else {
                                Loader()
                            }
                        }
                    }
                }
            }
        }
    }
}
