package com.lomolo.uzi

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lomolo.uzi.compose.navigation.UziNavHost
import com.lomolo.uzi.ui.theme.UziTheme

@Composable
fun UziApplication(navController: NavHostController) {
    UziNavHost(navController = navController)
}

@Preview(showBackground = true)
@Composable
fun UziApplicationPreview() {
    UziTheme {
        UziApplication(rememberNavController())
    }
}