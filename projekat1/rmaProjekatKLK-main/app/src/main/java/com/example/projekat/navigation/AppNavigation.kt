package com.example.projekat.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projekat.cats.details.catsDetails
import com.example.projekat.cats.list.catsListScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "cats",
    ) {

        catsListScreen(
            route = "cats",
            navController = navController
        )

        catsDetails(
            route = "cats/{id}",
            navController = navController,
        )

    }
}
