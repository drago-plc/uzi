package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.uzi.DeviceDetails
import com.lomolo.uzi.compose.navigation.Navigation

object ConfirmTripDetailsDestination: Navigation {
    override val route = "trip/confirm/details"
    override val title = null
}

@Composable
fun ConfirmTripDetails(
    modifier: Modifier = Modifier,
    tripViewModel: TripViewModel,
    deviceDetails: DeviceDetails,
    onConfirm: () -> Unit,
    onNavigateUp: () -> Unit
) {
    var chipSelected by rememberSaveable {
        mutableIntStateOf(0)
    }

    Box(
        modifier
            .fillMaxSize()
    ) {
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
            }
            Column(
                Modifier
                    .padding(top = 72.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth()
                ) {
                    repeat(2) {
                        FilterChip(
                            modifier = Modifier
                                .fillMaxWidth(if (it == 0) .5f else 1f)
                                .padding(
                                    start = if (it == 1) 4.dp else 0.dp,
                                    end = if (it == 0) 4.dp else 0.dp
                                ),
                            selected = chipSelected == it,
                            onClick = { chipSelected = it },
                            label = {
                                when(it) {
                                    0 -> {
                                        Text(
                                            "Sending",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    1 -> {
                                        Text(
                                            "Receiving",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Building name or Flat name",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    value = "",
                    onValueChange = {}
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Office/House number",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    value = "",
                    onValueChange = {}
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(deviceDetails.countryFlag)
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp),
                            contentDescription = null
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Phone number",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    value = "",
                    onValueChange = {}
                )
            }
        }
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
                onClick = { onConfirm() }
            ) {
                Text(
                    text = "Confirm",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}