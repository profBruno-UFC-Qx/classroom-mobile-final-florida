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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.florida.R
import com.example.florida.model.Budget
import com.example.florida.model.Client
import com.example.florida.model.Receipt
import com.example.florida.ui.budget.CreateBudgetDialog
import com.example.florida.ui.budget.BudgetDetailScreen
import com.example.florida.ui.budget.BudgetScreen
import com.example.florida.ui.client.ClientDetailScreen
import com.example.florida.ui.client.ClientScreen
import com.example.florida.ui.client.CreateClientDialog
import com.example.florida.ui.home.HomeScreen
import com.example.florida.ui.receipt.CreateReceiptDialog
import com.example.florida.ui.receipt.ReceiptDetailScreen
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
    var clientToEdit by remember { mutableStateOf<Client?>(null) }
    var showCreateBudgetDialog by remember { mutableStateOf(false) }
    var showCreateReceiptDialog by remember { mutableStateOf(false) }
    var budgetToEdit by remember { mutableStateOf<Budget?>(null) }
    var receiptToEdit by remember { mutableStateOf<Receipt?>(null) }
    var selectedClientIdForNewDocument by remember { mutableStateOf<Long?>(null) }
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
            modifier = Modifier.padding(innerPadding).padding(top = 12.dp)
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
                        selectedClientIdForNewDocument = null
                        showCreateBudgetDialog = true
                        navActions.navigateTobudget()
                    },
                    onCreateReceipt = {
                        selectedClientIdForNewDocument = null
                        showCreateReceiptDialog = true
                        navActions.navigateToReceipt()
                    }
                )
            }
            composable(Route.Budget.route) {
                BudgetScreen(
                    budgets = budgets,
                    receipts = receipts,
                    clients = clients,
                    showCreateDialog = showCreateBudgetDialog,
                    initialClientId = selectedClientIdForNewDocument,
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
                            selectedClientIdForNewDocument = null
                            }
                        )
                    },
                    onDeleteBudget = { budget ->
                        appViewModel.deleteBudget(budget.id)
                    },
                    onUpdateStatus = { budget, status ->
                        appViewModel.updateBudgetStatus(budget.id, status)
                    },
                    onCreateReceiptFromBudget = { budget ->
                        appViewModel.createReceipt(
                            clientId = budget.clientId,
                            items = budget.items,
                            budgetId = budget.id,
                            onSaved = { navActions.navigateToReceipt() }
                        )
                    },
                    onOpenBudget = { budget ->
                        navActions.navigateToBudgetDetail(budget.id)
                    },
                    onOpenReceipt = { receipt ->
                        navActions.navigateToReceiptDetail(receipt.id)
                    }
                )
            }
            composable(
                route = Route.BudgetDetail.route,
                arguments = listOf(navArgument("budgetId") { type = NavType.LongType })
            ) { backStackEntry ->
                val budgetId = backStackEntry.arguments?.getLong("budgetId") ?: 0L
                val budget = budgets.firstOrNull { it.id == budgetId }
                val linkedReceipt = receipts.firstOrNull { it.budgetId == budgetId }
                BudgetDetailScreen(
                    budget = budget,
                    linkedReceipt = linkedReceipt,
                    onUpdateStatus = { status ->
                        budget?.let { appViewModel.updateBudgetStatus(it.id, status) }
                    },
                    onEdit = { selectedBudget ->
                        budgetToEdit = selectedBudget
                    },
                    onCreateReceipt = {
                        budget?.let {
                            appViewModel.createReceipt(
                                clientId = it.clientId,
                                items = it.items,
                                budgetId = it.id,
                                onSaved = { navActions.navigateToReceipt() }
                            )
                        }
                    },
                    onOpenReceipt = { receipt ->
                        navActions.navigateToReceiptDetail(receipt.id)
                    },
                    onDelete = {
                        budget?.let { appViewModel.deleteBudget(it.id) }
                        navActions.navigateUp(navController)
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
                    },
                    onEditClick = { client ->
                        clientToEdit = client
                    },
                    onOpenClient = { client ->
                        navActions.navigateToClientDetail(client.id)
                    }
                )
            }
            composable(
                route = Route.ClientDetail.route,
                arguments = listOf(navArgument("clientId") { type = NavType.LongType })
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
                val client = clients.firstOrNull { it.id == clientId }
                ClientDetailScreen(
                    client = client,
                    budgets = budgets.filter { it.clientId == clientId },
                    receipts = receipts.filter { it.clientId == clientId },
                    onEditClient = { selectedClient ->
                        clientToEdit = selectedClient
                    },
                    onCreateBudget = { selectedClient ->
                        selectedClientIdForNewDocument = selectedClient.id
                        showCreateBudgetDialog = true
                        navActions.navigateTobudget()
                    },
                    onCreateReceipt = { selectedClient ->
                        selectedClientIdForNewDocument = selectedClient.id
                        showCreateReceiptDialog = true
                        navActions.navigateToReceipt()
                    }
                )
            }
            composable(Route.Receipt.route) {
                ReceiptScreen(
                    receipts = receipts,
                    clients = clients,
                    showCreateDialog = showCreateReceiptDialog,
                    initialClientId = selectedClientIdForNewDocument,
                    onDismissCreateDialog = { showCreateReceiptDialog = false },
                    onCreateReceipt = { clientId, items ->
                        appViewModel.createReceipt(
                            clientId = clientId,
                            items = items,
                            onSaved = {
                            showCreateReceiptDialog = false
                            selectedClientIdForNewDocument = null
                            }
                        )
                    },
                    onDeleteReceipt = { receipt ->
                        appViewModel.deleteReceipt(receipt.id)
                    },
                    onOpenReceipt = { receipt ->
                        navActions.navigateToReceiptDetail(receipt.id)
                    }
                )
            }
            composable(
                route = Route.ReceiptDetail.route,
                arguments = listOf(navArgument("receiptId") { type = NavType.LongType })
            ) { backStackEntry ->
                val receiptId = backStackEntry.arguments?.getLong("receiptId") ?: 0L
                val receipt = receipts.firstOrNull { it.id == receiptId }
                val originBudget = receipt?.budgetId?.let { budgetId ->
                    budgets.firstOrNull { it.id == budgetId }
                }
                ReceiptDetailScreen(
                    receipt = receipt,
                    originBudget = originBudget,
                    onEdit = { selectedReceipt ->
                        receiptToEdit = selectedReceipt
                    },
                    onOpenBudget = { budget ->
                        navActions.navigateToBudgetDetail(budget.id)
                    },
                    onDelete = {
                        receipt?.let { appViewModel.deleteReceipt(it.id) }
                        navActions.navigateUp(navController)
                    }
                )
            }
        }
    }

    if (showCreateClientDialog || clientToEdit != null) {
        CreateClientDialog(
            client = clientToEdit,
            onDismiss = {
                showCreateClientDialog = false
                clientToEdit = null
            },
            onConfirm = { name, document, phone, address, imagePath ->
                val editing = clientToEdit
                if (editing == null) {
                    appViewModel.createClient(
                        name = name,
                        document = document,
                        phone = phone,
                        address = address,
                        imagePath = imagePath
                    )
                } else {
                    appViewModel.updateClient(
                        client = editing,
                        name = name,
                        document = document,
                        phone = phone,
                        address = address,
                        imagePath = imagePath
                    )
                }
                showCreateClientDialog = false
                clientToEdit = null
            }
        )
    }

    budgetToEdit?.let { editingBudget ->
        CreateBudgetDialog(
            clients = clients,
            budget = editingBudget,
            initialClientId = editingBudget.clientId,
            onDismiss = {
                budgetToEdit = null
            },
            onConfirm = { clientId, notes, validade, entrega, items ->
                appViewModel.updateBudget(
                    budget = editingBudget,
                    clientId = clientId,
                    notes = notes,
                    validade = validade,
                    entrega = entrega,
                    items = items,
                    onSaved = {
                        budgetToEdit = null
                    }
                )
            }
        )
    }

    receiptToEdit?.let { editingReceipt ->
        CreateReceiptDialog(
            clients = clients,
            receipt = editingReceipt,
            initialClientId = editingReceipt.clientId,
            onDismiss = {
                receiptToEdit = null
            },
            onConfirm = { clientId, items ->
                appViewModel.updateReceipt(
                    receipt = editingReceipt,
                    clientId = clientId,
                    items = items,
                    onSaved = {
                        receiptToEdit = null
                    }
                )
            }
        )
    }

    if (!showCreateBudgetDialog && !showCreateReceiptDialog) {
        selectedClientIdForNewDocument = null
    }
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
