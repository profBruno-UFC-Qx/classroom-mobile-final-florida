package com.example.florida.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.domain.model.BudgetListItem
import com.example.florida.domain.model.BudgetStatus
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.Item
import com.example.florida.domain.model.UserSetup

@Composable
fun BudgetScreen(
    budgets: List<BudgetListItem>,
    clients: List<Client>,
    currentUser: UserSetup?,
    showCreateDialog: Boolean,
    initialClientId: Long? = null,
    onDismissCreateDialog: () -> Unit,
    onCreateBudget: (clientId: Long?, notes: String?, validade: String?, entrega: String?, items: List<Item>) -> Unit,
    onDeleteBudget: (BudgetListItem) -> Unit,
    onUpdateStatus: (BudgetListItem, BudgetStatus) -> Unit,
    onCreateReceiptFromBudget: (BudgetListItem) -> Unit,
    onOpenBudget: (BudgetListItem) -> Unit,
    onOpenReceipt: (Long) -> Unit,
    onShareBudgetPdf: (BudgetListItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (budgets.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_budgets),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        lazyItems(budgets, key = { it.id }) { budget ->
            BudgetCard(
                budget = budget,
                onDelete = { onDeleteBudget(budget) },
                onUpdateStatus = { status -> onUpdateStatus(budget, status) },
                onCreateReceipt = { onCreateReceiptFromBudget(budget) },
                onOpen = { onOpenBudget(budget) },
                onOpenLinkedReceipt = {
                    budget.linkedReceiptId?.let(onOpenReceipt)
                },
                onSharePdf = { if (currentUser != null) onShareBudgetPdf(budget) }
            )
        }
    }

    if (showCreateDialog) {
        BudgetFormDialog(
            clients = clients,
            budget = null,
            initialClientId = initialClientId,
            onDismiss = onDismissCreateDialog,
            onConfirm = onCreateBudget
        )
    }
}
