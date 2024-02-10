package com.lomolo.uzi.compose.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.compose.home.HomeScreenDestination
import com.lomolo.uzi.compose.navigation.Navigation
import com.lomolo.uzi.compose.signin.SessionViewModel
import com.lomolo.uzi.compose.trip.ConfirmTripDetailsDestination
import com.lomolo.uzi.compose.trip.DropoffMap
import com.lomolo.uzi.compose.trip.DropoffMapScreenDestination
import com.lomolo.uzi.compose.trip.PickupMap
import com.lomolo.uzi.compose.trip.PickupMapScreenDestination
import com.lomolo.uzi.compose.trip.SearchDropoff
import com.lomolo.uzi.compose.trip.SearchDropoffLocationScreenDestination
import com.lomolo.uzi.compose.trip.SearchPickup
import com.lomolo.uzi.compose.trip.SearchPickupLocationScreenDestination
import com.lomolo.uzi.compose.trip.ConfirmTripDetails
import com.lomolo.uzi.compose.trip.ConfirmTripPickup
import com.lomolo.uzi.compose.trip.ConfirmTripPickupDestination
import com.lomolo.uzi.compose.trip.TripProducts
import com.lomolo.uzi.compose.trip.TripProductsScreenDestination
import com.lomolo.uzi.compose.trip.TripViewModel
import com.lomolo.uzi.model.Session

object TripGraphDestination: Navigation {
    override val route = "trip/graph"
    override val title = null
}

fun NavGraphBuilder.trip(
    navController: NavHostController,
    tripViewModel: TripViewModel,
    mainViewModel: MainViewModel,
    sessionViewModel: SessionViewModel
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
        composable(TripProductsScreenDestination.route) {
            TripProducts(
                tripViewModel = tripViewModel,
                navigateBack = { navController.popBackStack() },
                onConfirmTrip = {
                    navController.navigate(it)
                }
            )
        }
        composable(ConfirmTripDetailsDestination.route) {
            ConfirmTripDetails(
                onNavigateUp = {
                    navController.popBackStack()
                },
                tripViewModel = tripViewModel,
                onConfirm = {
                    navController.navigate(ConfirmTripPickupDestination.route)
                },
                mainViewModel = mainViewModel,
                sessionViewModel = sessionViewModel
            )
        }
        composable(ConfirmTripPickupDestination.route) {
            ConfirmTripPickup(
                tripViewModel = tripViewModel,
                onNavigateUp = {
                    navController.popBackStack()
                },
                onNavigateTo = {
                    navController.navigate(it) {
                        popUpTo(it) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}