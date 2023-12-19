package com.lomolo.uzi.compose.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.lomolo.uzi.compose.navigation.Navigation

object UserNameDestination: Navigation {
    override val route ="signin/name"
    override val title = null
}

@Composable
fun Name(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "",
            placeholder = {
                Text("Firstname")
            },
            onValueChange = {},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
        )
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(
            value = "",
            placeholder = {
                Text("Lastname")
            },
            onValueChange = {},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {}
            )
        )
    }
}