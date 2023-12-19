package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
            "Start trip",
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
        )
        OutlinedTextField(
            value = "",
            placeholder = {
                Text("Pickup")
            },
            onValueChange = {},
            singleLine = true,
            modifier = Modifier.wrapContentHeight(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        Row(
            modifier = Modifier
                .padding(top = 2.dp, bottom = 2.dp)
        ) {
            Icon(
                Icons.AutoMirrored.TwoTone.ArrowForward,
                contentDescription = "",
                modifier = Modifier.rotate(90f)
            )
            Icon(
                Icons.AutoMirrored.TwoTone.ArrowForward,
                contentDescription = "",
                modifier = Modifier.rotate(-90f)
            )
        }
        OutlinedTextField(
            value = "",
            placeholder = {
                Text("Drop-off")
            },
            onValueChange = {},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {}
            )
        )
    }
}