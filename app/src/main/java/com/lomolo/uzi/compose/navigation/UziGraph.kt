package com.lomolo.uzi.compose.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lomolo.uzi.compose.home.HomeScreen
import com.lomolo.uzi.compose.home.HomeScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UziNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
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
                    HomeScreen()
                }
            }
        }
    }
}