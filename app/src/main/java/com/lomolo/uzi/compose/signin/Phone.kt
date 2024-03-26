package com.lomolo.uzi.compose.signin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.uzi.DeviceDetails

@Composable
fun Phone(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel,
    deviceDetails: DeviceDetails
) {
    val signInUiState by sessionViewModel.signInInput.collectAsState()
    val isPhoneValid = sessionViewModel.isPhoneValid(signInUiState)

    OutlinedTextField(
        isError = signInUiState.phone.isNotBlank() && !isPhoneValid,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = if (signInUiState.phone.isNotBlank() && !isPhoneValid) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
            unfocusedContainerColor = if (signInUiState.phone.isNotBlank() && !isPhoneValid) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
            errorTextColor = MaterialTheme.colorScheme.error
        ),
        value = signInUiState.phone,
        placeholder = {
            Text("Phone number")
        },
        onValueChange = { sessionViewModel.setPhone(it) },
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
        prefix = {
            Text(
                text = "+${deviceDetails.countryPhoneCode}"
            )
        },
        singleLine = true,
        modifier = modifier.fillMaxWidth().padding(16.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
    )

}