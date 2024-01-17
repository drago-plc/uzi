package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lomolo.uzi.compose.navigation.Navigation

object SearchPickupLocationScreenDestination: Navigation {
    override val route = "trip/search"
    override val title = null
}

@Composable
fun SearchPickup(
    modifier: Modifier = Modifier
) {}