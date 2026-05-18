package com.example.florida.ui.navigation

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

    val navigateToBudgetDetail: (Long) -> Unit = { budgetId ->
        navController.navigate(Route.BudgetDetail.create(budgetId))
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

    val navigateToReceiptDetail: (Long) -> Unit = { receiptId ->
        navController.navigate(Route.ReceiptDetail.create(receiptId))
    }

    val navigateToClient: () -> Unit = {
        navController.navigate(Route.Client.route){
            restoreState = true
        }
    }

    val navigateToClientDetail: (Long) -> Unit = { clientId ->
        navController.navigate(Route.ClientDetail.create(clientId))
    }

    val navigateUp: (NavController) -> Unit = { navController ->
        navController.navigateUp()
    }
}
