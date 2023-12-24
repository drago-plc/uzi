package com.lomolo.uzi.compose.loader

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Loader(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        strokeWidth = 2.dp,
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .wrapContentSize()
            .size(16.dp)
    )
}