package com.lomolo.uzi.compose.navigation.graphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.compose.TopBar
import com.lomolo.uzi.compose.navigation.Navigation
import com.lomolo.uzi.compose.signin.Name
import com.lomolo.uzi.compose.signin.Phone
import com.lomolo.uzi.compose.signin.SignInViewModel
import com.lomolo.uzi.compose.signin.UserNameDestination
import com.lomolo.uzi.compose.signin.UserPhoneDestination

object UserGraphDestination: Navigation {
    override val route = "graph/user"
    override val title = null
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.user(
    navController: NavHostController,
    signInViewModel: SignInViewModel,
    mainViewModel: MainViewModel
) {
    navigation(
        startDestination = UserNameDestination.route,
        route = UserGraphDestination.route
    ) {
        composable(route = UserNameDestination.route) {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

            Scaffold(
                topBar = {
                    TopBar(
                        title = UserNameDestination.title,
                        scrollBehavior = scrollBehavior,
                        canNavigateBack = true,
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            ) {innerPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Name(
                        onNextSubmit = { navController.navigate(UserPhoneDestination.route) },
                        signInViewModel = signInViewModel
                    )
                }
            }
        }
        composable(route = UserPhoneDestination.route) {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

            Scaffold(
                topBar = {
                    TopBar(
                        title = UserPhoneDestination.title,
                        scrollBehavior = scrollBehavior,
                        canNavigateBack = true,
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            ) { innerPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Phone(
                        signInViewModel = signInViewModel,
                        mainViewModel = mainViewModel,
                        onNavigateTo = {
                            navController.navigate(it) {
                                popUpTo(it) {
                                    inclusive = true
                                    saveState = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}