package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lomolo.uzi.R

@Composable
fun StartTrip(
    modifier: Modifier = Modifier,
    onEnterPickupClick: () -> Unit = {},
    onEnterDropoffClick: () -> Unit = {},
    tripViewModel: TripViewModel
) {
    val tripUiState by tripViewModel.tripUiInput.collectAsState()
    val pickupValue = when(val s = tripViewModel.pickupGeocodeState) {
        PickupGeocodeState.Loading -> {
            "Loading..."
        }
        is PickupGeocodeState.Error -> {
            "Unnamed street"
        }
        is PickupGeocodeState.Success -> {
            stringResource(R.string.pickup_location)
        }
        else -> {stringResource(R.string.pickup_location)}
    }
    val dropoffValue = when(val s = tripViewModel.dropoffGeocodeState) {
        DropoffGeocodeState.Loading -> {
            "Loading..."
        }
        is DropoffGeocodeState.Error -> {
            "Unnamed street"
        }
        is DropoffGeocodeState.Success -> {
            stringResource(R.string.drop_off_location)
        }
        else -> {stringResource(R.string.drop_off_location)}
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.start_trip),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            enabled = false,
            value = "",
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.icons8_next_location_96___),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "pickup icon",
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            },
            placeholder = {
                if (tripUiState.pickup.formattedAddress.isEmpty()) {
                    Text(
                        pickupValue,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                } else {
                    Text(
                        tripUiState.pickup.formattedAddress,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            },
            onValueChange = {},
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    MaterialTheme.shapes.small
                )
                .clickable { onEnterPickupClick() },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(
            enabled = false,
            value = "",
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.icons8_next_location_96___),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "pickup icon",
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            },
            placeholder = {
                if (tripUiState.dropoff.formattedAddress.isEmpty()) {
                    Text(
                        dropoffValue,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                } else {
                    Text(
                        tripUiState.dropoff.formattedAddress,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            },
            onValueChange = {},
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    MaterialTheme.shapes.small
                )
                .clickable { onEnterDropoffClick() },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
    }
}