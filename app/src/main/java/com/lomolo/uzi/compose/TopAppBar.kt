package com.lomolo.uzi.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lomolo.uzi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    canNavigateBack: Boolean = false,
    navigateBack: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        actions = actions,
        title = {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = navigateBack
                ) {
                    Icon(
                        Icons.AutoMirrored.TwoTone.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                }
            }
        }
    )
}