package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    modifier: Modifier = Modifier,
    onEnterPickupClick: () -> Unit = {},
    onEnterDropoffClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.start_trip),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            enabled = false,
            value = "",
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier.size(36.dp),
                    contentDescription = "pickup icon",
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            },
            placeholder = {
                Text(
                    stringResource(R.string.pickup_location),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            onValueChange = {},
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    MaterialTheme.shapes.small
                )
                .clickable { onEnterPickupClick() },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(
            enabled = false,
            value = "",
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.next_location),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "pickup icon",
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            },
            placeholder = {
                Text(
                    stringResource(R.string.drop_off_location),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            onValueChange = {},
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    MaterialTheme.shapes.small
                )
                .clickable { onEnterDropoffClick() },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
    }
}