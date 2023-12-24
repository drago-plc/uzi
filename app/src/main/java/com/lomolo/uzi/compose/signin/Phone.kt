package com.lomolo.uzi.compose.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.home.HomeScreenDestination
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation

object UserPhoneDestination: Navigation {
    override val route = "signin/phone"
    override val title = "Enter your phone"
}

@Composable
fun Phone(
    modifier: Modifier = Modifier,
    signInViewModel: SignInViewModel,
    onNavigateTo: (String) -> Unit = {},
    mainViewModel: MainViewModel
) {
    val signInUiState by signInViewModel.signInInput.collectAsState()
    val validPhone = signInViewModel.validPhone(signInUiState)
    val deviceUiState by mainViewModel.deviceDetailsUiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextField(
            isError = !validPhone,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (!validPhone) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
                unfocusedContainerColor = if (!validPhone) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                errorTextColor = MaterialTheme.colorScheme.error
            ),
            value = signInUiState.phone,
            placeholder = {
                Text("Phone number")
            },
            onValueChange = { signInViewModel.setPhone(it) },
            leadingIcon = {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(deviceUiState.countryFlag)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp),
                    contentDescription = "country flag"
                )
            },
            prefix = {
                Text(
                    text = "+${deviceUiState.countryPhoneCode}"
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            when (val s = signInViewModel.signInUiState) {
                is SignInUiState.Success -> {
                    Button(
                        onClick = { signInViewModel.signIn { onNavigateTo(HomeScreenDestination.route)} },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.sign_in),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                is SignInUiState.Loading -> {
                    Loader()
                }
                is SignInUiState.Error -> {
                    Text(
                        text = s.message!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}