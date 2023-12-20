package com.lomolo.uzi.compose.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lomolo.uzi.compose.navigation.Navigation

object UserPhoneDestination: Navigation {
    override val route = "signin/phone"
    override val title = "Enter your phone"
}

@Composable
fun Phone(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        OutlinedTextField(
            value = "",
            placeholder = {
                Text("Phone number")
            },
            onValueChange = {},
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
        )
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth().height(60.dp),
        ) {
           Text(
               text = "Sign In",
               style = MaterialTheme.typography.labelMedium
           )
        }
    }
}