package com.lomolo.uzi.compose.trip

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.navigation.Navigation

object DropoffMapScreenDestination: Navigation {
    override val route = "trip/drop_off"
    override val title = R.string.drop_location
}

@Composable
fun DropoffMap(
    modifier: Modifier = Modifier
) {}