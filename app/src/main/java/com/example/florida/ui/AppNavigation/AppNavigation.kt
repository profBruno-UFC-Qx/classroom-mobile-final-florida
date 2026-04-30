package com.example.florida.ui.AppNavigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.florida.R
import com.example.florida.ui.budget.BudgetScreen
import com.example.florida.ui.client.ClientScreen
import com.example.florida.ui.home.HomeScreen
import com.example.florida.ui.receipt.ReceiptScreen
import com.example.florida.ui.settings.SettingsScreen

@Composable
fun AppNavigator(
    navController: NavHostController
) {
    val navActions = NavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Route.Home.route,
        Route.Budget.route,
        Route.Settings.route,
        Route.Receipt.route,
        Route.Client.route,
        Route.Settings.route,
    )

    val showActionButton = currentRoute in listOf(
        Route.Budget.route,
        Route.Receipt.route,
        Route.Client.route,
    )

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
            if (showBottomBar) {

            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.route) {
                HomeScreen()
            }
            composable(Route.Budget.route) {
                BudgetScreen()
            }
            composable(Route.Settings.route) {
                SettingsScreen()
            }
            composable(Route.Client.route) {
                ClientScreen()
            }
            composable(Route.Receipt.route) {
                ReceiptScreen()
            }
            composable(Route.Receipt.route){
                ReceiptScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
fun AppBottomBar(
    currentRoute: String?,
    navActions: NavigationActions
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
    ) {
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    when (item) {
                        BottomNavItem.HOME -> navActions.navigateToHome()
                        BottomNavItem.BUDGET -> navActions.navigateTobudget()
                        BottomNavItem.CLIENT -> navActions.navigateToClient()
                        BottomNavItem.RECEIPT -> navActions.navigateToReceipt()
                        BottomNavItem.SETTINGS -> navActions.navigateToSettings()
                    }
                },
                icon = item.icon,
                label = { Text(stringResource(item.label)) },
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
fun ActionButton(
    currentRoute: String?,
    // criar um meio de o butão do scarfolld chamar a criação de um novo orçamento ou recibo ou cliente.

) {
}

@Composable
fun getTitleForRoute(route: String?): String {
    return when (route) {
        Route.Home.route -> stringResource(R.string.home)
        Route.Budget.route -> stringResource(R.string.budget)
        Route.Settings.route -> stringResource(R.string.settings)
        Route.Client.route -> stringResource(R.string.client)
        Route.Receipt.route -> stringResource(R.string.receipt)
        else -> stringResource(R.string.app_name)
    }
}
