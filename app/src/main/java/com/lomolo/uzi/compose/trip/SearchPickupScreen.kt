package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation

object SearchPickupLocationScreenDestination: Navigation {
    override val route = "trip/search"
    override val title = null
}

private data class place(
    val name: String
)
private val plcs = listOf<place>(
    place("Vihiga"),
    place("Ngong"),
    place("Kakamega"),
    place("Kona Baridi")
)

@Composable
fun SearchPickup(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    tripViewModel: TripViewModel
) {
    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp,
                tripViewModel = tripViewModel
            )
        }
    ) {
        Surface(
            modifier
                .fillMaxSize()
                .padding(it)
        ) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    tripViewModel: TripViewModel
) {
    var active by rememberSaveable {
        mutableStateOf(false)
    }
    val locationLoading = when(tripViewModel.searchingLocationState) {
        is LocationPredicateState.Loading -> {
            true
        }
        else -> {
            false
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .semantics { isTraversalGroup = true }
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                active = active,
                onActiveChange = { active = it },
                placeholder = {
                    Text(stringResource(R.string.enter_location))
                },
                leadingIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.TwoTone.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                trailingIcon = { if (locationLoading) Loader() },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .semantics { traversalIndex = -1f }
            ) {
                LazyColumn {
                    items(plcs) {
                        ListItem(
                            headlineContent = {
                                Text(it.name)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {}
                        )
                    }
                }
            }
            Icon(
                Icons.TwoTone.LocationOn,
                modifier = Modifier.size(40.dp),
                contentDescription = null
            )
        }
    }
}