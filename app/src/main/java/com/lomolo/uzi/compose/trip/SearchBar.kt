package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
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
import com.lomolo.uzi.ReverseGeocodeQuery
import com.lomolo.uzi.compose.loader.Loader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchTopBar(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onConfirmLocation: (ReverseGeocodeQuery.ReverseGeocode) -> Unit = {},
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
            onSearch = { tripViewModel.searchPlace(tripViewModel.searchQuery) },
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
                    onClick = { tripViewModel.searchPlace(tripViewModel.searchQuery) }
                ) {
                    Icon(
                        Icons.TwoTone.Search,
                        modifier = Modifier
                            .size(24.dp),
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
                        supportingContent = {
                            Text(
                                it.secondaryText
                            )
                        },
                        headlineContent = {
                            Text(
                                it.mainText
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tripViewModel.updateSearchQuery(it.mainText)
                                onConfirmLocation(
                                    ReverseGeocodeQuery.ReverseGeocode(
                                        placeId = it.id,
                                        formattedAddress = it.mainText,
                                        location = ReverseGeocodeQuery.Location(0.0, 0.0)
                                    )
                                )
                            }
                    )
                }
                item {
                    ListItem(
                        headlineContent = {
                            Text(
                                "Search map"
                            )
                        },
                        leadingContent = {
                            Icon(
                                painterResource(id = R.drawable.icons8_location_pin_90___),
                                modifier = Modifier
                                    .size(20.dp),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .clickable { onPickupMapClick() }
                    )
                }
            }
        }
    }
}
