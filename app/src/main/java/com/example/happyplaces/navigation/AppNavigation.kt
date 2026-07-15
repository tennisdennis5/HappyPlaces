package com.example.happyplaces.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.happyplaces.ui.screens.PlaceListScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.PlaceList.route
    ) {

        composable(Screen.PlaceList.route) {

            PlaceListScreen(
                places = emptyList(),
                onAddClick = {
                    navController.navigate(Screen.AddPlace.route)
                }
            )

        }

        composable(Screen.AddPlace.route) {

            // Kommt im nächsten Schritt
        }

        composable(Screen.PlaceDetail.route) {

            // Kommt später
        }
    }
}