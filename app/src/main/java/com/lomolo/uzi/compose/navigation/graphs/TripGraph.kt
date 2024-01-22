package com.lomolo.uzi.compose.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.uzi.MainViewModel
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
                }
            )
        }
        composable(DropoffMapScreenDestination.route) {
            DropoffMap(
                mainViewModel = mainViewModel,
                tripViewModel = tripViewModel,
                onNavigateBackToTrip = {
                    navController.navigate(HomeScreenDestination.route) {
                        popUpTo(HomeScreenDestination.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
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
            SearchDropoff(
                onNavigateUp = {
                    navController.popBackStack()
                },
                tripViewModel = tripViewModel,
                onPickupMapClick = {
                    navController.navigate(DropoffMapScreenDestination.route)
                }
            )
        }
    }
}