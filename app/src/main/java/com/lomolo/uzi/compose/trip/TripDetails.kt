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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.compose.navigation.Navigation
import com.lomolo.uzi.compose.signin.SessionViewModel
import com.lomolo.uzi.model.Session

object ConfirmTripDetailsDestination: Navigation {
    override val route = "trip/confirm/details"
    override val title = null
}

@Composable
fun ConfirmTripDetails(
    modifier: Modifier = Modifier,
    tripViewModel: TripViewModel,
    mainViewModel: MainViewModel,
    session: Session,
    onConfirm: () -> Unit,
    onNavigateUp: () -> Unit
) {
    var chipSelected by rememberSaveable {
        mutableIntStateOf(0)
    }
    val deviceDetails by mainViewModel.deviceDetailsUiState.collectAsState()
    val tripDetailsUi by tripViewModel.tripUiInput.collectAsState()
    val isPhoneValid = tripViewModel.isPhoneValid(tripDetailsUi.details)
    val tripDetailsValid = tripViewModel.tripDetailsValid(tripDetailsUi.details)
    val isNameValid = tripViewModel.isNameValid(tripDetailsUi.details.name)
    val keyboardController = LocalSoftwareKeyboardController.current

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
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    "Recipient details",
                    style = MaterialTheme.typography.labelMedium
                )
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
                            onClick = {
                                chipSelected = it
                                if (it == 1) {
                                    tripViewModel.setTripDetailsName("${session.firstname} ${session.lastname}")
                                    tripViewModel.setTripDetailsPhone(session.phone)
                                } else {
                                    tripViewModel.setTripDetailsName("")
                                    tripViewModel.setTripDetailsPhone("")
                                }
                            },
                            label = {
                                when(it) {
                                    0 -> {
                                        Text(
                                            "Sending",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    1 -> {
                                        Text(
                                            "Receiving",
                                            style = MaterialTheme.typography.labelSmall
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
                    isError = tripDetailsUi.details.name.isNotBlank() && !isNameValid,
                    placeholder = {
                        Text(
                            text = "Name",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    singleLine = true,
                    value = tripDetailsUi.details.name,
                    onValueChange = {
                        tripViewModel.setTripDetailsName(it)
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Building name or Flat name",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    singleLine = true,
                    supportingText = {
                        Text("Can be blank but it's an aid for the courier")
                    },
                    value = tripDetailsUi.details.buildName,
                    onValueChange = {
                        tripViewModel.setTripDetailsBuilding(it)
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Office/House number",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    singleLine = true,
                    supportingText = {
                        Text("Can be blank but it's an aid for the courier")
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    value = tripDetailsUi.details.flatOrOffice,
                    onValueChange = {
                        tripViewModel.setTripDetailsUnit(it)
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = tripDetailsUi.details.phone.isNotBlank() && !isPhoneValid,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = if (tripDetailsUi.details.phone.isNotBlank() && !isPhoneValid) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = if (tripDetailsUi.details.phone.isNotBlank() && !isPhoneValid) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        errorTextColor = MaterialTheme.colorScheme.error
                    ),
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
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    ),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Phone number",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    prefix = {
                        Text("+${deviceDetails.countryPhoneCode}")
                    },
                    value = tripDetailsUi.details.phone,
                    onValueChange = {
                        tripViewModel.setTripDetailsPhone(it)
                    }
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
                onClick = { if (tripDetailsValid) onConfirm() }
            ) {
                Text(
                    text = "Confirm",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}