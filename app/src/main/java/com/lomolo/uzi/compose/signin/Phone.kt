package com.lomolo.uzi.compose.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.lomolo.uzi.compose.navigation.Navigation

object UserPhoneDestination: Navigation {
    override val route = "signin/phone"
    override val title = null
}

@Composable
fun Phone(
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "",
            placeholder = {
                Text("Phone number")
            },
            onValueChange = {},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = { /*TODO*/ },
            shape = MaterialTheme.shapes.small
        ) {
           Text(
               text = "Sign In",
               style = MaterialTheme.typography.labelMedium
           )
        }
    }
}