package com.example.florida.ui.AppNavigation

import androidx.navigation.NavController

class NavigationActions(navController: NavController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(Route.Home.route) {
            popUpTo(Route.Home.route) { inclusive = true }
        }
    }

    val navigateTobudget: () -> Unit = {
        navController.navigate(Route.Budget.route){
            restoreState = true
        }
    }

    val navigateToSettings: () -> Unit = {
        navController.navigate(Route.Settings.route){
            restoreState = true
        }
    }

    val navigateToReceipt: () -> Unit = {
        navController.navigate(Route.Receipt.route){
            restoreState = true
        }
    }

    val navigateToClient: () -> Unit = {
        navController.navigate(Route.Client.route){
            restoreState = true
        }
    }

    val navigateUp: (NavController) -> Unit = { navController ->
        navController.navigateUp()
    }
}