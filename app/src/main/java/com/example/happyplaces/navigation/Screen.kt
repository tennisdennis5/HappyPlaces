package com.example.happyplaces.navigation

sealed class Screen(val route: String) {

    object PlaceList : Screen("place_list")

    object AddPlace : Screen("add_place")

    object PlaceDetail : Screen("place_detail")
}