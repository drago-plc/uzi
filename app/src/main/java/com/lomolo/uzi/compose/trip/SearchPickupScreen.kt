package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.loader.Loader
import com.lomolo.uzi.compose.navigation.Navigation

object SearchPickupLocationScreenDestination: Navigation {
    override val route = "trip/search"
    override val title = null
}

@Composable
fun SearchPickup(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    tripViewModel: TripViewModel,
    onPickupMapClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                onPickupMapClick = onPickupMapClick,
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
    tripViewModel: TripViewModel,
    onPickupMapClick: () -> Unit
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

    val places = when(val s = tripViewModel.searchingLocationState) {
        is LocationPredicateState.Success -> {
            s.places
        }
        else -> {listOf()}
    }

    Box(modifier = modifier
        .fillMaxSize()
        .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            query = tripViewModel.searchQuery,
            onQueryChange = { tripViewModel.updateSearchQuery(it) },
            onSearch = {},
            active = true,
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
            trailingIcon = {
                if (locationLoading) Loader()
                else  IconButton(
                    onClick = { onPickupMapClick() }
                ) {
                    Icon(
                        painterResource(id = R.drawable.icons8_map_marker_100),
                        modifier = Modifier
                            .size(28.dp),
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = -1f }
        ) {
            LazyColumn {
                items(places) {
                    ListItem(
                        leadingContent = {
                            Icon(
                                Icons.TwoTone.LocationOn,
                                contentDescription = null
                            )
                        },
                        headlineContent = {
                            Text(
                                it.mainText
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {}
                    )
                }
            }
        }
    }
}