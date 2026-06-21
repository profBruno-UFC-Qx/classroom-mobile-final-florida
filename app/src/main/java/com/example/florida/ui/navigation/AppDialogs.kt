package com.example.florida.ui.navigation

import androidx.compose.runtime.Composable
import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.Receipt
import com.example.florida.ui.budget.BudgetViewModel
import com.example.florida.ui.budget.BudgetFormDialog
import com.example.florida.ui.client.ClientViewModel
import com.example.florida.ui.client.ClientFormDialog
import com.example.florida.ui.receipt.ReceiptFormDialog
import com.example.florida.ui.receipt.ReceiptViewModel

@Composable
fun AppDialogs(
    showCreateClientDialog: Boolean,
    clientToEdit: Client?,
    budgetToEdit: Budget?,
    receiptToEdit: Receipt?,
    clientOptions: List<Client>,
    clientViewModel: ClientViewModel,
    budgetViewModel: BudgetViewModel,
    receiptViewModel: ReceiptViewModel,
    onDismissClientDialog: () -> Unit,
    onDismissBudgetDialog: () -> Unit,
    onDismissReceiptDialog: () -> Unit,
) {
    if (showCreateClientDialog || clientToEdit != null) {
        ClientFormDialog(
            client = clientToEdit,
            onDismiss = onDismissClientDialog,
            onConfirm = { name, document, phone, address, imagePath ->
                val editing = clientToEdit
                if (editing == null) {
                    clientViewModel.saveClient(
                        Client(
                            name = name,
                            document = document,
                            phone = phone,
                            address = address,
                            imagePath = imagePath
                        )
                    )
                } else {
                    clientViewModel.saveClient(
                        editing.copy(
                            name = name,
                            document = document,
                            phone = phone,
                            address = address,
                            imagePath = imagePath
                        )
                    )
                }
                onDismissClientDialog()
            }
        )
    }

    budgetToEdit?.let { editingBudget ->
        BudgetFormDialog(
            clients = clientOptions,
            budget = editingBudget,
            initialClientId = editingBudget.clientId,
            onDismiss = onDismissBudgetDialog,
            onConfirm = { clientId, notes, validade, entrega, items ->
                budgetViewModel.updateBudget(
                    budget = editingBudget,
                    clientId = clientId,
                    notes = notes,
                    validade = validade,
                    entrega = entrega,
                    items = items,
                    onSaved = onDismissBudgetDialog
                )
            }
        )
    }

    receiptToEdit?.let { editingReceipt ->
        ReceiptFormDialog(
            clients = clientOptions,
            receipt = editingReceipt,
            initialClientId = editingReceipt.clientId,
            onDismiss = onDismissReceiptDialog,
            onConfirm = { clientId, items ->
                receiptViewModel.updateReceipt(
                    receipt = editingReceipt,
                    clientId = clientId,
                    items = items,
                    onSaved = onDismissReceiptDialog
                )
            }
        )
    }
}
