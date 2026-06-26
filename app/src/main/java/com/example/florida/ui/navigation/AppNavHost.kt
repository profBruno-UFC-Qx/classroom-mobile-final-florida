package com.example.florida.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.florida.domain.model.BudgetListItem
import com.example.florida.domain.model.ClientDocumentSummary
import com.example.florida.domain.model.ClientListItem
import com.example.florida.domain.model.DashboardSummary
import com.example.florida.domain.model.ReceiptListItem
import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.Receipt
import com.example.florida.domain.model.UserSetup
import com.example.florida.ui.budget.BudgetDetailScreen
import com.example.florida.ui.budget.BudgetScreen
import com.example.florida.ui.budget.BudgetViewModel
import com.example.florida.ui.client.ClientDetailScreen
import com.example.florida.ui.client.ClientScreen
import com.example.florida.ui.client.ClientViewModel
import com.example.florida.ui.home.HomeScreen
import com.example.florida.ui.receipt.ReceiptDetailScreen
import com.example.florida.ui.receipt.ReceiptScreen
import com.example.florida.ui.receipt.ReceiptViewModel
import com.example.florida.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    navActions: NavigationActions,
    appViewModel: AppNavigatorViewModel,
    clientViewModel: ClientViewModel,
    budgetViewModel: BudgetViewModel,
    receiptViewModel: ReceiptViewModel,
    dashboardSummary: DashboardSummary,
    clients: List<ClientListItem>,
    clientOptions: List<Client>,
    budgetList: List<BudgetListItem>,
    receiptList: List<ReceiptListItem>,
    selectedClient: Client?,
    selectedClientDocuments: List<ClientDocumentSummary>,
    selectedBudget: Budget?,
    selectedReceipt: Receipt?,
    currentUser: UserSetup?,
    isSyncingBackup: Boolean,
    backupSyncMessage: String?,
    showCreateBudgetDialog: Boolean,
    showCreateReceiptDialog: Boolean,
    selectedClientIdForNewDocument: Long?,
    onShowCreateBudgetDialogChange: (Boolean) -> Unit,
    onShowCreateReceiptDialogChange: (Boolean) -> Unit,
    onSelectedClientIdForNewDocumentChange: (Long?) -> Unit,
    onClientToEditChange: (Client?) -> Unit,
    onBudgetToEditChange: (Budget?) -> Unit,
    onReceiptToEditChange: (Receipt?) -> Unit,
    onSyncBackupNow: () -> Unit,
    onLogout: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
        modifier = Modifier.padding(innerPadding).padding(top = 12.dp)
    ) {
        composable(Route.Home.route) {
            HomeScreen(
                summary = dashboardSummary,
                onOpenClients = { navActions.navigateToClient() },
                onOpenBudgets = { navActions.navigateTobudget() },
                onOpenReceipts = { navActions.navigateToReceipt() },
                onCreateBudget = {
                    onSelectedClientIdForNewDocumentChange(null)
                    onShowCreateBudgetDialogChange(true)
                    navActions.navigateTobudget()
                },
                onCreateReceipt = {
                    onSelectedClientIdForNewDocumentChange(null)
                    onShowCreateReceiptDialogChange(true)
                    navActions.navigateToReceipt()
                }
            )
        }
        composable(Route.Budget.route) {
            BudgetScreen(
                budgets = budgetList,
                clients = clientOptions,
                currentUser = currentUser,
                showCreateDialog = showCreateBudgetDialog,
                initialClientId = selectedClientIdForNewDocument,
                onDismissCreateDialog = { onShowCreateBudgetDialogChange(false) },
                onCreateBudget = { clientId, notes, validade, entrega, items ->
                    budgetViewModel.createBudget(
                        clientId = clientId,
                        notes = notes,
                        validade = validade,
                        entrega = entrega,
                        items = items,
                        onSaved = {
                            onShowCreateBudgetDialogChange(false)
                            onSelectedClientIdForNewDocumentChange(null)
                        }
                    )
                },
                onDeleteBudget = { budget ->
                    budgetViewModel.deleteBudget(budget.id)
                },
                onUpdateStatus = { budget, status ->
                    budgetViewModel.updateStatus(budget.id, status)
                },
                onCreateReceiptFromBudget = { budget ->
                    budgetViewModel.createReceiptFromBudget(budget.id) {
                        navActions.navigateToReceipt()
                    }
                },
                onOpenBudget = { budget ->
                    navActions.navigateToBudgetDetail(budget.id)
                },
                onOpenReceipt = { receiptId ->
                    navActions.navigateToReceiptDetail(receiptId)
                },
                onShareBudgetPdf = { budget ->
                    budgetViewModel.shareBudgetPdf(budget.id)
                }
            )
        }
        composable(
            route = Route.BudgetDetail.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.LongType })
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getLong("budgetId") ?: 0L
            LaunchedEffect(budgetId) {
                budgetViewModel.selectBudget(budgetId)
            }
            LaunchedEffect(budgetId, receiptList) {
                receiptList.firstOrNull { it.budgetId == budgetId }?.let {
                    receiptViewModel.selectReceipt(it.id)
                }
            }
            val budget = selectedBudget?.takeIf { it.id == budgetId }
            val linkedReceipt = receiptList.firstOrNull { it.budgetId == budgetId }?.let { receiptSummary ->
                selectedReceipt?.takeIf { it.id == receiptSummary.id }
            }
            BudgetDetailScreen(
                budget = budget,
                linkedReceipt = linkedReceipt,
                onUpdateStatus = { status ->
                    budget?.let { budgetViewModel.updateStatus(it.id, status) }
                },
                onEdit = { editingBudget ->
                    onBudgetToEditChange(editingBudget)
                },
                onCreateReceipt = {
                    budget?.let {
                        budgetViewModel.createReceiptFromBudget(it.id) {
                            navActions.navigateToReceipt()
                        }
                    }
                },
                onOpenReceipt = { receipt ->
                    navActions.navigateToReceiptDetail(receipt.id)
                },
                onDelete = {
                    budget?.let { budgetViewModel.deleteBudget(it.id) }
                    navActions.navigateUp(navController)
                },
                onSharePdf = {
                    budget?.let { budgetViewModel.shareBudgetPdf(it.id) }
                }
            )
        }
        composable(Route.Settings.route) {
            SettingsScreen(
                currentUser = currentUser,
                onSaveUser = appViewModel::updateUser,
                onSyncBackupNow = onSyncBackupNow,
                isSyncingBackup = isSyncingBackup,
                backupSyncMessage = backupSyncMessage,
                onLogout = onLogout
            )
        }
        composable(Route.Client.route) {
            ClientScreen(
                clients = clients,
                onDeleteClick = { client ->
                    clientViewModel.deleteClient(client)
                },
                onEditClick = { client ->
                    onClientToEditChange(client)
                },
                onOpenClient = { client ->
                    navActions.navigateToClientDetail(client.id)
                },
                onClickBudget = { selectedClient ->
                    onSelectedClientIdForNewDocumentChange(selectedClient.id)
                    onShowCreateBudgetDialogChange(true)
                    navActions.navigateTobudget()
                },
                onClickReceipt = { selectedClient ->
                    onSelectedClientIdForNewDocumentChange(selectedClient.id)
                    onShowCreateReceiptDialogChange(true)
                    navActions.navigateToReceipt()
                }
            )
        }
        composable(
            route = Route.ClientDetail.route,
            arguments = listOf(navArgument("clientId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
            LaunchedEffect(clientId) {
                clientViewModel.selectClient(clientId)
            }
            ClientDetailScreen(
                client = selectedClient?.takeIf { it.id == clientId },
                documents = selectedClientDocuments,
                onEditClient = { editingClient ->
                    onClientToEditChange(editingClient)
                },
                onCreateBudget = { selectedClient ->
                    onSelectedClientIdForNewDocumentChange(selectedClient.id)
                    onShowCreateBudgetDialogChange(true)
                    navActions.navigateTobudget()
                },
                onCreateReceipt = { selectedClient ->
                    onSelectedClientIdForNewDocumentChange(selectedClient.id)
                    onShowCreateReceiptDialogChange(true)
                    navActions.navigateToReceipt()
                }
            )
        }
        composable(Route.Receipt.route) {
            ReceiptScreen(
                receipts = receiptList,
                clients = clientOptions,
                currentUser = currentUser,
                showCreateDialog = showCreateReceiptDialog,
                initialClientId = selectedClientIdForNewDocument,
                onDismissCreateDialog = { onShowCreateReceiptDialogChange(false) },
                onCreateReceipt = { clientId, items ->
                    receiptViewModel.createReceipt(
                        clientId = clientId,
                        items = items,
                        onSaved = {
                            onShowCreateReceiptDialogChange(false)
                            onSelectedClientIdForNewDocumentChange(null)
                        }
                    )
                },
                onDeleteReceipt = { receipt ->
                    receiptViewModel.deleteReceipt(receipt.id)
                },
                onOpenReceipt = { receipt ->
                    navActions.navigateToReceiptDetail(receipt.id)
                },
                onShareReceiptPdf = { receipt ->
                    receiptViewModel.shareReceiptPdf(receipt.id)
                }
            )
        }
        composable(
            route = Route.ReceiptDetail.route,
            arguments = listOf(navArgument("receiptId") { type = NavType.LongType })
        ) { backStackEntry ->
            val receiptId = backStackEntry.arguments?.getLong("receiptId") ?: 0L
            LaunchedEffect(receiptId) {
                receiptViewModel.selectReceipt(receiptId)
            }
            val receipt = selectedReceipt?.takeIf { it.id == receiptId }
            LaunchedEffect(receipt?.budgetId) {
                receipt?.budgetId?.let { budgetViewModel.selectBudget(it) }
            }
            val originBudget = receipt?.budgetId?.let { budgetId ->
                selectedBudget?.takeIf { it.id == budgetId }
            }
            ReceiptDetailScreen(
                receipt = receipt,
                originBudget = originBudget,
                onEdit = { editingReceipt ->
                    onReceiptToEditChange(editingReceipt)
                },
                onOpenBudget = { budget ->
                    navActions.navigateToBudgetDetail(budget.id)
                },
                onDelete = {
                    receipt?.let { receiptViewModel.deleteReceipt(it.id) }
                    navActions.navigateUp(navController)
                },
                onSharePdf = {
                    receipt?.let { receiptViewModel.shareReceiptPdf(it.id) }
                }
            )
        }
    }
}
