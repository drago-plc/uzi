package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lomolo.uzi.compose.navigation.Navigation

object SearchDropoffLocationScreenDestination: Navigation {
    override val route = "trip/search/dropoff"
    override val title = null
}

@Composable
fun SearchDropoff(
    modifier: Modifier = Modifier
) {}