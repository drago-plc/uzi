package com.lomolo.uzi.compose.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lomolo.uzi.DeviceDetails
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.loader.Loader

@Composable
fun User(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel,
    deviceDetails: DeviceDetails
) {
    val signInUiState by sessionViewModel.signInInput.collectAsState()
    val isFirstnameValid = sessionViewModel.isNameValid(signInUiState.firstName)
    val isLastnameValid = sessionViewModel.isNameValid(signInUiState.lastName)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Get started",
            modifier = Modifier
                .padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Phone(sessionViewModel = sessionViewModel, deviceDetails = deviceDetails)
        Name(sessionViewModel = sessionViewModel)
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            when (sessionViewModel.signInUiState) {
                is SignInUiState.Success -> {
                    Button(
                        onClick = {
                            if (isFirstnameValid && isLastnameValid) sessionViewModel.onboardUser()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = stringResource(R.string.proceed),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                is SignInUiState.Loading -> {
                    Loader()
                }
                is SignInUiState.Error -> {
                    Column {
                        Text(
                            text = stringResource(R.string.not_your_fault_err),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Button(
                            onClick = {
                                if (isFirstnameValid && isLastnameValid) sessionViewModel.onboardUser()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = stringResource(R.string.retry),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}