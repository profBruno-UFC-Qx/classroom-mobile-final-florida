package com.example.florida.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.florida.R
import com.example.florida.document.pdf.BudgetPdfCreator
import com.example.florida.document.pdf.ReceiptPdfCreate
import com.example.florida.document.pdf.sharePdf
import com.example.florida.domain.model.ClientListItem
import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.Receipt
import com.example.florida.ui.budget.BudgetViewModel
import com.example.florida.ui.client.ClientViewModel
import com.example.florida.ui.home.DashboardViewModel
import com.example.florida.ui.receipt.ReceiptViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AppNavigator(
    navController: NavHostController,
    onLogout: () -> Unit,
) {
    val context = LocalContext.current
    val appViewModel: AppNavigatorViewModel = viewModel(
        factory = AppNavigatorViewModel.factory(context)
    )
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.factory(context)
    )
    val clientViewModel: ClientViewModel = viewModel(
        factory = ClientViewModel.factory(context)
    )
    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModel.factory(context)
    )
    val receiptViewModel: ReceiptViewModel = viewModel(
        factory = ReceiptViewModel.factory(context)
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
    var pendingBudgetPdfId by remember { mutableStateOf<Long?>(null) }
    var pendingReceiptPdfId by remember { mutableStateOf<Long?>(null) }

    val dashboardSummary by dashboardViewModel.summary.collectAsState()
    val clients by clientViewModel.clients.collectAsState()
    val budgetList by budgetViewModel.budgets.collectAsState()
    val receiptList by receiptViewModel.receipts.collectAsState()
    val selectedClient by clientViewModel.selectedClient.collectAsState()
    val selectedClientDocuments by clientViewModel.selectedClientDocuments.collectAsState()
    val selectedBudget by budgetViewModel.selectedBudget.collectAsState()
    val selectedReceipt by receiptViewModel.selectedReceipt.collectAsState()
    val currentUser by appViewModel.currentUser.collectAsState()
    val clientOptions = clients.map { it.toClient() }

    LaunchedEffect(pendingBudgetPdfId, selectedBudget, currentUser) {
        val budgetId = pendingBudgetPdfId ?: return@LaunchedEffect
        val budget = selectedBudget?.takeIf { it.id == budgetId } ?: return@LaunchedEffect
        val user = currentUser ?: return@LaunchedEffect
        val file = BudgetPdfCreator(
            user = user,
            client = budget.client,
            itens = budget.items,
            observasion = budget.notes.orEmpty(),
            date = budget.createdAt.atZone(ZoneId.systemDefault()).toOffsetDateTime(),
            budgetNumber = budget.id.toString(),
            prazo = budget.entrega,
            validade = budget.validade,
            context = context
        )
        sharePdf(context, file, context.getString(R.string.share_budget))
        pendingBudgetPdfId = null
    }

    LaunchedEffect(pendingReceiptPdfId, selectedReceipt, currentUser) {
        val receiptId = pendingReceiptPdfId ?: return@LaunchedEffect
        val receipt = selectedReceipt?.takeIf { it.id == receiptId } ?: return@LaunchedEffect
        val user = currentUser ?: return@LaunchedEffect
        val file = ReceiptPdfCreate(
            context = context,
            user = user,
            cliente = receipt.client,
            itens = receipt.items,
            budgetNumber = receipt.id.toInt(),
            dateStr = receipt.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        )
        sharePdf(context, file, context.getString(R.string.share_receipt))
        pendingReceiptPdfId = null
    }

    AppScaffold(
        navController = navController,
        navActions = navActions,
        currentRoute = currentRoute,
        onActionClick = {
            when (currentRoute) {
                Route.Client.route -> showCreateClientDialog = true
                Route.Budget.route -> showCreateBudgetDialog = true
                Route.Receipt.route -> showCreateReceiptDialog = true
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding,
            navActions = navActions,
            appViewModel = appViewModel,
            clientViewModel = clientViewModel,
            budgetViewModel = budgetViewModel,
            receiptViewModel = receiptViewModel,
            dashboardSummary = dashboardSummary,
            clients = clients,
            clientOptions = clientOptions,
            budgetList = budgetList,
            receiptList = receiptList,
            selectedClient = selectedClient,
            selectedClientDocuments = selectedClientDocuments,
            selectedBudget = selectedBudget,
            selectedReceipt = selectedReceipt,
            currentUser = currentUser,
            showCreateBudgetDialog = showCreateBudgetDialog,
            showCreateReceiptDialog = showCreateReceiptDialog,
            selectedClientIdForNewDocument = selectedClientIdForNewDocument,
            onShowCreateBudgetDialogChange = { showCreateBudgetDialog = it },
            onShowCreateReceiptDialogChange = { showCreateReceiptDialog = it },
            onSelectedClientIdForNewDocumentChange = { selectedClientIdForNewDocument = it },
            onClientToEditChange = { clientToEdit = it },
            onBudgetToEditChange = { budgetToEdit = it },
            onReceiptToEditChange = { receiptToEdit = it },
            onPendingBudgetPdfIdChange = { pendingBudgetPdfId = it },
            onPendingReceiptPdfIdChange = { pendingReceiptPdfId = it },
            onLogout = onLogout,
        )
    }

    AppDialogs(
        showCreateClientDialog = showCreateClientDialog,
        clientToEdit = clientToEdit,
        budgetToEdit = budgetToEdit,
        receiptToEdit = receiptToEdit,
        clientOptions = clientOptions,
        clientViewModel = clientViewModel,
        budgetViewModel = budgetViewModel,
        receiptViewModel = receiptViewModel,
        onDismissClientDialog = {
            showCreateClientDialog = false
            clientToEdit = null
        },
        onDismissBudgetDialog = { budgetToEdit = null },
        onDismissReceiptDialog = { receiptToEdit = null },
    )

    if (!showCreateBudgetDialog && !showCreateReceiptDialog) {
        selectedClientIdForNewDocument = null
    }
}

private fun ClientListItem.toClient(): Client {
    return Client(
        id = id,
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = imagePath,
        deleted = false
    )
}
