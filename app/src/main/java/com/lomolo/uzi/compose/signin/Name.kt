package com.lomolo.uzi.compose.signin

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.home.HomeScreenDestination
import com.lomolo.uzi.compose.navigation.Navigation

object UserNameDestination: Navigation {
    override val route ="signin/name"
    override val title = "Enter your details"
}

@Composable
fun Name(
    modifier: Modifier = Modifier,
    onNextSubmit: () -> Unit = {},
    signInViewModel: SignInViewModel = viewModel()
) {
    val signInUiState by signInViewModel.signInInput.collectAsState()
    val isFirstnameValid = signInViewModel.isNameValid(signInUiState.firstName)
    val isLastnameValid = signInViewModel.isNameValid(signInUiState.lastName)
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
            onValueChange = { signInViewModel.setFirstname(it) },
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
            onValueChange = { signInViewModel.setLastname(it) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (isFirstnameValid &&
                        isLastnameValid) onNextSubmit()
                }
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = {
                if (isFirstnameValid && isLastnameValid)
                    onNextSubmit()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
        ) {
            Text(
                text = stringResource(R.string.proceed),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}