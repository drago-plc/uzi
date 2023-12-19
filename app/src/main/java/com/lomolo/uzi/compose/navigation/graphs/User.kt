package com.lomolo.uzi.compose.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.uzi.compose.navigation.Navigation
import com.lomolo.uzi.compose.signin.Name
import com.lomolo.uzi.compose.signin.Phone
import com.lomolo.uzi.compose.signin.UserNameDestination
import com.lomolo.uzi.compose.signin.UserPhoneDestination

object UserGraphDestination: Navigation {
    override val route = "graph/user"
    override val title = null
}

fun NavGraphBuilder.user(
    navController: NavHostController
) {
    navigation(
        startDestination = UserNameDestination.route,
        route = UserGraphDestination.route
    ) {
        composable(route = UserNameDestination.route) {
            Name()
        }
        composable(route = UserPhoneDestination.route) {
            Phone()
        }
    }
}