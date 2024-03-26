package com.lomolo.uzi.compose.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Composable
fun Name(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel,
) {
    val signInUiState by sessionViewModel.signInInput.collectAsState()
    val isFirstnameValid = sessionViewModel.isNameValid(signInUiState.firstName)
    val isLastnameValid = sessionViewModel.isNameValid(signInUiState.lastName)

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
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
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
        )
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(
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
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (isFirstnameValid && isLastnameValid) sessionViewModel.onboardUser()
                }
            )
        )
    }
}