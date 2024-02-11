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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.home.HomeScreenDestination
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation

object UserNameDestination: Navigation {
    override val route ="signin/name"
    override val title = R.string.enter_your_details
}

@Composable
fun Name(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {}
) {
    val signInUiState by sessionViewModel.signInInput.collectAsState()
    val isFirstnameValid = sessionViewModel.isNameValid(signInUiState.firstName)
    val isLastnameValid = sessionViewModel.isNameValid(signInUiState.lastName)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextField(
            isError = signInUiState.firstName.isNotBlank() && !isFirstnameValid,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (signInUiState.firstName.isNotBlank() && !isFirstnameValid) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
                unfocusedContainerColor = if (signInUiState.firstName.isNotBlank() && !isFirstnameValid) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                errorTextColor = MaterialTheme.colorScheme.error
            ),
            value = signInUiState.firstName,
            placeholder = {
                Text("Firstname")
            },
            onValueChange = { sessionViewModel.setFirstname(it) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
        )
        Spacer(modifier = Modifier.size(16.dp))
        TextField(
            isError = signInUiState.lastName.isNotBlank() && !isLastnameValid,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (signInUiState.lastName.isNotBlank() && !isLastnameValid) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
                unfocusedContainerColor = if (signInUiState.lastName.isNotBlank() && !isLastnameValid) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                errorTextColor = MaterialTheme.colorScheme.error
            ),
            value = signInUiState.lastName,
            placeholder = {
                Text("Lastname")
            },
            onValueChange = { sessionViewModel.setLastname(it) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (isFirstnameValid &&
                        isLastnameValid) sessionViewModel.onboardUser {
                        onNavigateTo(HomeScreenDestination.route)
                    }
                }
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            when (val s = sessionViewModel.signInUiState) {
                is SignInUiState.Success -> {
                    Button(
                        onClick = {
                            if (isFirstnameValid && isLastnameValid)
                                sessionViewModel.onboardUser {
                                    onNavigateTo(HomeScreenDestination.route)
                                    sessionViewModel.resetSignIn()
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.proceed),
                            style = MaterialTheme.typography.labelMedium
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
                                if (isFirstnameValid && isLastnameValid)
                                    sessionViewModel.onboardUser {
                                        onNavigateTo(HomeScreenDestination.route)
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.retry),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}