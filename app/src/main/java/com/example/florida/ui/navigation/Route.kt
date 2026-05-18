package com.example.florida.ui.navigation

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object Budget : Route("budget")
    data object BudgetDetail : Route("budget/{budgetId}") {
        fun create(budgetId: Long): String = "budget/$budgetId"
    }
    data object Settings : Route("settings")
    data object Receipt : Route("receipt")
    data object ReceiptDetail : Route("receipt/{receiptId}") {
        fun create(receiptId: Long): String = "receipt/$receiptId"
    }
    data object Client : Route("clients")
    data object ClientDetail : Route("clients/{clientId}") {
        fun create(clientId: Long): String = "clients/$clientId"
    }
}
