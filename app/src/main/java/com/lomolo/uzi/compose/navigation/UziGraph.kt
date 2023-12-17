package com.lomolo.uzi.compose.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lomolo.uzi.MainViewModel
import com.lomolo.uzi.UziViewModelProvider
import com.lomolo.uzi.compose.home.HomeScreen
import com.lomolo.uzi.compose.home.HomeScreenDestination

@Composable
fun UziNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel = viewModel(factory = UziViewModelProvider.Factory)
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeScreenDestination.route
    ) {
        composable(route = HomeScreenDestination.route) {
            Scaffold { innerPadding ->
                Surface(
                    modifier = modifier
                        .padding(innerPadding)
                ) {
                    HomeScreen(
                        mainViewModel = mainViewModel
                    )
                }
            }
        }
    }
}