package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.lomolo.uzi.compose.navigation.Navigation

object SearchDropoffLocationScreenDestination: Navigation {
    override val route = "trip/search/drop_off"
    override val title = null
}

@Composable
fun SearchDropoff(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    tripViewModel: TripViewModel,
    onPickupMapClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                onPickupMapClick = onPickupMapClick,
                onNavigateUp = onNavigateUp,
                onConfirmLocation = {
                    tripViewModel.setDropoff(it)
                },
                tripViewModel = tripViewModel
            )
        }
    ) {
        Surface(
            modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LaunchedEffect(Unit) { tripViewModel.updateSearchQuery("")}
        }
    }
}

