package com.example.florida.ui.AppNavigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.florida.R
import com.example.florida.model.Client
import com.example.florida.ui.budget.BudgetScreen
import com.example.florida.ui.client.ClientScreen
import com.example.florida.ui.client.CreateClientDialog
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
    var showCreateClientDialog by remember { mutableStateOf(false) }
    val clients = remember {
        mutableStateListOf(
            Client("Francisco", "Rua dos Bobos", "06364254307", "123456789", null)
        )
    }

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
            if (showActionButton) {
                ActionButton(
                    currentRoute = currentRoute,
                    onClick = {
                        when (currentRoute) {
                            Route.Client.route -> {
                                showCreateClientDialog = true
                            }
                            Route.Budget.route -> { }
                            Route.Receipt.route -> { }
                            else -> {  }
                        }
                    }
                )
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
                ClientScreen(
                    clients = clients,
                    onDeleteClick = { client -> clients.remove(client) }
                )
            }
            composable(Route.Receipt.route) {
                ReceiptScreen()
            }
            composable(Route.Receipt.route){
                ReceiptScreen()
            }
        }
    }

    if (showCreateClientDialog) {
        CreateClientDialog(
            onDismiss = { showCreateClientDialog = false },
            onConfirm = { name, document, phone, address, imagePath ->
                clients.add(Client(name, address, document, phone, imagePath))
                showCreateClientDialog = false
            }
        )
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
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = stringResource(R.string.add)
        )
    }
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
