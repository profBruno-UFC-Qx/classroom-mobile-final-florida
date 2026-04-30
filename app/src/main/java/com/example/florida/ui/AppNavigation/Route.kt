package com.example.florida.ui.AppNavigation

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object Budget : Route("budget")
    data object Settings : Route("settings")
    data object Receipt : Route("receipt")
    data object Client : Route("clients")
}
