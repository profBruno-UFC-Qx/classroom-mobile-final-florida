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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.florida.R
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
    val context = LocalContext.current
    val appViewModel: AppNavigatorViewModel = viewModel(
        factory = AppNavigatorViewModel.factory(context)
    )
    val navActions = NavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showCreateClientDialog by remember { mutableStateOf(false) }
    var showCreateBudgetDialog by remember { mutableStateOf(false) }
    var showCreateReceiptDialog by remember { mutableStateOf(false) }
    val clients by appViewModel.clients.collectAsState()
    val budgets by appViewModel.budgets.collectAsState()
    val receipts by appViewModel.receipts.collectAsState()

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
                            Route.Budget.route -> showCreateBudgetDialog = true
                            Route.Receipt.route -> showCreateReceiptDialog = true
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
                HomeScreen(
                    clients = clients,
                    budgets = budgets,
                    receipts = receipts,
                    onOpenClients = { navActions.navigateToClient() },
                    onOpenBudgets = { navActions.navigateTobudget() },
                    onOpenReceipts = { navActions.navigateToReceipt() },
                    onCreateBudget = {
                        showCreateBudgetDialog = true
                        navActions.navigateTobudget()
                    },
                    onCreateReceipt = {
                        showCreateReceiptDialog = true
                        navActions.navigateToReceipt()
                    }
                )
            }
            composable(Route.Budget.route) {
                BudgetScreen(
                    budgets = budgets,
                    clients = clients,
                    showCreateDialog = showCreateBudgetDialog,
                    onDismissCreateDialog = { showCreateBudgetDialog = false },
                    onCreateBudget = { clientId, notes, validade, entrega, items ->
                        appViewModel.createBudget(
                            clientId = clientId,
                            notes = notes,
                            validade = validade,
                            entrega = entrega,
                            items = items,
                            onSaved = {
                            showCreateBudgetDialog = false
                            }
                        )
                    },
                    onDeleteBudget = { budget ->
                        appViewModel.deleteBudget(budget.id)
                    }
                )
            }
            composable(Route.Settings.route) {
                SettingsScreen()
            }
            composable(Route.Client.route) {
                ClientScreen(
                    clients = clients,
                    onDeleteClick = { client ->
                        appViewModel.deleteClient(client)
                    }
                )
            }
            composable(Route.Receipt.route) {
                ReceiptScreen(
                    receipts = receipts,
                    clients = clients,
                    showCreateDialog = showCreateReceiptDialog,
                    onDismissCreateDialog = { showCreateReceiptDialog = false },
                    onCreateReceipt = { clientId, items ->
                        appViewModel.createReceipt(
                            clientId = clientId,
                            items = items,
                            onSaved = {
                            showCreateReceiptDialog = false
                            }
                        )
                    },
                    onDeleteReceipt = { receipt ->
                        appViewModel.deleteReceipt(receipt.id)
                    }
                )
            }
        }
    }

    if (showCreateClientDialog) {
        CreateClientDialog(
            onDismiss = { showCreateClientDialog = false },
            onConfirm = { name, document, phone, address, imagePath ->
                appViewModel.createClient(
                    name = name,
                    document = document,
                    phone = phone,
                    address = address,
                    imagePath = imagePath
                )
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
