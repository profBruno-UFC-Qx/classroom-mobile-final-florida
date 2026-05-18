package com.example.florida.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.florida.R

@Composable
fun AppScaffold(
    navController: NavHostController,
    navActions: NavigationActions,
    currentRoute: String?,
    onActionClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val showBottomBar = currentRoute in bottomBarRoutes
    val showActionButton = currentRoute in actionButtonRoutes
    val showTopBar = !showBottomBar && currentRoute != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showTopBar) {
                AppTopBar(
                    title = getTitleForRoute(currentRoute),
                    onBackClick = { navActions.navigateUp(navController) }
                )
            } else if (showBottomBar) {
                AppTopBar(
                    title = getTitleForRoute(currentRoute),
                    showBackButton = false
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    navActions = navActions
                )
            }
        },
        floatingActionButton = {
            if (showActionButton) {
                ActionButton(
                    currentRoute = currentRoute,
                    onClick = onActionClick
                )
            }
        },
        content = content
    )
}

@Composable
fun getTitleForRoute(route: String?): String {
    if (route?.startsWith("clients/") == true) {
        return stringResource(R.string.detail_client)
    }
    if (route?.startsWith("budget/") == true) {
        return stringResource(R.string.detail_budget)
    }
    if (route?.startsWith("receipt/") == true) {
        return stringResource(R.string.detail_receipt)
    }

    return when (route) {
        Route.Home.route -> stringResource(R.string.home)
        Route.Budget.route -> stringResource(R.string.budget)
        Route.Settings.route -> stringResource(R.string.settings)
        Route.Client.route -> stringResource(R.string.client)
        Route.Receipt.route -> stringResource(R.string.receipt)
        else -> stringResource(R.string.app_name)
    }
}

private val bottomBarRoutes = listOf(
    Route.Home.route,
    Route.Budget.route,
    Route.Settings.route,
    Route.Receipt.route,
    Route.Client.route,
)

private val actionButtonRoutes = listOf(
    Route.Budget.route,
    Route.Receipt.route,
    Route.Client.route,
)
