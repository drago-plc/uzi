package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lomolo.uzi.R

@Composable
fun StartTrip(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.trip_details),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.my_location),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "pickup icon",
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            },
            value = "",
            placeholder = {
                Text(stringResource(R.string.pickup_location))
            },
            onValueChange = {},
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.next_location),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "pickup icon",
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            },
            value = "",
            placeholder = {
                Text(stringResource(R.string.drop_off_location))
            },
            onValueChange = {},
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {}
            )
        )
    }
}