package com.lomolo.uzi.compose.navigation.graphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.uzi.DeviceDetails
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.compose.TopBar
import com.lomolo.uzi.compose.home.HomeScreenDestination
import com.lomolo.uzi.compose.navigation.Navigation
import com.lomolo.uzi.compose.trip.DropoffMap
import com.lomolo.uzi.compose.trip.DropoffMapScreenDestination
import com.lomolo.uzi.compose.trip.PickupMap
import com.lomolo.uzi.compose.trip.PickupMapScreenDestination
import com.lomolo.uzi.compose.trip.SearchDropoff
import com.lomolo.uzi.compose.trip.SearchDropoffLocationScreenDestination
import com.lomolo.uzi.compose.trip.SearchPickup
import com.lomolo.uzi.compose.trip.SearchPickupLocationScreenDestination
import com.lomolo.uzi.compose.trip.TripViewModel

object TripGraphDestination: Navigation {
    override val route = "trip/graph"
    override val title = null
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.trip(
    navController: NavHostController,
    tripViewModel: TripViewModel,
    mainViewModel: MainViewModel,
) {
    navigation(
        startDestination = PickupMapScreenDestination.route,
        route = TripGraphDestination.route
    ) {
        composable(PickupMapScreenDestination.route) {
            PickupMap(
                mainViewModel = mainViewModel,
                tripViewModel = tripViewModel,
                onNavigateBackToTrip = {
                    navController.navigate(HomeScreenDestination.route) {
                        popUpTo(HomeScreenDestination.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateUp = {
                    navController.popBackStack()
                }
            )
        }
        composable(DropoffMapScreenDestination.route) {
            Scaffold(
                topBar = {
                    TopBar(
                        title = stringResource(DropoffMapScreenDestination.title),
                        canNavigateBack = true,
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            ) { innerPadding ->
                Surface(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {
                    DropoffMap()
                }
            }
        }
        composable(SearchPickupLocationScreenDestination.route) {
            SearchPickup(
                onNavigateUp = {
                    navController.popBackStack()
                },
                tripViewModel = tripViewModel,
                onPickupMapClick = {
                    navController.navigate(PickupMapScreenDestination.route)
                }
            )
        }
        composable(SearchDropoffLocationScreenDestination.route) {
            SearchDropoff()
        }
    }
}