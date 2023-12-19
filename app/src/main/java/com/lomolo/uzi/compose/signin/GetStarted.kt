package com.lomolo.uzi.compose.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GetStarted(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Button(
            onClick = onGetStartedClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(8.dp)
        ) {
            Text(
                text = "Get started",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
