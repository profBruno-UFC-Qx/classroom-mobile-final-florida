package com.example.florida.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.extencions.currencyDigitsToCents
import com.example.florida.extencions.formatForBrl
import com.example.florida.model.Budget
import com.example.florida.model.BudgetStatus
import com.example.florida.model.Client
import com.example.florida.model.Item
import com.example.florida.model.Receipt
import com.example.florida.model.SessionManager
import com.example.florida.ui.utils.BudgetPdfCreator
import com.example.florida.ui.utils.CurrencyVisualTransformation
import com.example.florida.ui.utils.sharePdf
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BudgetScreen(
    budgets: List<Budget>,
    receipts: List<Receipt>,
    clients: List<Client>,
    showCreateDialog: Boolean,
    initialClientId: Long? = null,
    onDismissCreateDialog: () -> Unit,
    onCreateBudget: (clientId: Long?, notes: String?, validade: String?, entrega: String?, items: List<Item>) -> Unit,
    onDeleteBudget: (Budget) -> Unit,
    onUpdateStatus: (Budget, BudgetStatus) -> Unit,
    onCreateReceiptFromBudget: (Budget) -> Unit,
    onOpenBudget: (Budget) -> Unit,
    onOpenReceipt: (Receipt) -> Unit,
) {
    val context = LocalContext.current
    val user = SessionManager.getCurrentUser()

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
            val linkedReceipt = receipts.firstOrNull { it.budgetId == budget.id }
            BudgetCard(
                budget = budget,
                linkedReceiptId = linkedReceipt?.id,
                onDelete = { onDeleteBudget(budget) },
                onUpdateStatus = { status -> onUpdateStatus(budget, status) },
                onCreateReceipt = { onCreateReceiptFromBudget(budget) },
                onOpen = { onOpenBudget(budget) },
                onOpenLinkedReceipt = {
                    linkedReceipt?.let(onOpenReceipt)
                },
                onSharePdf = {
                    user?.let {
                        val file = BudgetPdfCreator(
                            user = it,
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
                    }
                }
            )
        }
    }

    if (showCreateDialog) {
        CreateBudgetDialog(
            clients = clients,
            budget = null,
            initialClientId = initialClientId,
            onDismiss = onDismissCreateDialog,
            onConfirm = onCreateBudget
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBudgetDialog(
    clients: List<Client>,
    budget: Budget? = null,
    initialClientId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (clientId: Long?, notes: String?, validade: String?, entrega: String?, items: List<Item>) -> Unit,
) {
    val selectedClientId = budget?.clientId ?: initialClientId
    var selectedClient by remember(selectedClientId, clients) {
        mutableStateOf(clients.firstOrNull { it.id == selectedClientId } ?: clients.firstOrNull())
    }
    var expanded by remember { mutableStateOf(false) }
    var notes by remember(budget?.id) { mutableStateOf(budget?.notes.orEmpty()) }
    var validade by remember(budget?.id) { mutableStateOf(budget?.validade.orEmpty()) }
    var entrega by remember(budget?.id) { mutableStateOf(budget?.entrega.orEmpty()) }
    var description by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    val items = remember(budget?.id) {
        mutableStateListOf<Item>().also { it.addAll(budget?.items.orEmpty()) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if (budget == null) R.string.new_budget_title else R.string.edit_budget)) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedClient?.name ?: stringResource(R.string.no_client),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.client)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.no_client)) },
                                onClick = {
                                    selectedClient = null
                                    expanded = false
                                }
                            )
                            clients.forEach { client ->
                                DropdownMenuItem(
                                    text = { Text(client.name) },
                                    onClick = {
                                        selectedClient = client
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(notes, { notes = it }, label = { Text(stringResource(R.string.notes)) }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(validade, { validade = it }, label = { Text(stringResource(R.string.validity)) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(entrega, { entrega = it }, label = { Text(stringResource(R.string.delivery)) }, modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Text(stringResource(R.string.items), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                }
                lazyItems(items) { item ->
                    AssistChip(
                        onClick = { items.remove(item) },
                        label = { Text(stringResource(R.string.item_summary, item.qty, item.description, item.total.formatForBrl())) }
                    )
                }
                item {
                    OutlinedTextField(description, { description = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            qty,
                            { qty = it.filter(Char::isDigit).take(4) },
                            label = { Text(stringResource(R.string.quantity)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            price,
                            { price = it.filter(Char::isDigit).take(12) },
                            label = { Text(stringResource(R.string.value)) },
                            visualTransformation = CurrencyVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Button(
                        onClick = {
                            val parsedQty = qty.toIntOrNull() ?: 1
                            val parsedPrice = price.currencyDigitsToCents()
                            if (description.isNotBlank() && parsedPrice > 0) {
                                items.add(Item(description = description, qty = parsedQty, price = parsedPrice))
                                description = ""
                                qty = "1"
                                price = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text(stringResource(R.string.add_item))
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.total_value, items.sumOf { it.total }.formatForBrl()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = items.isNotEmpty(),
                onClick = {
                    onConfirm(
                        selectedClient?.id,
                        notes.ifBlank { null },
                        validade.ifBlank { null },
                        entrega.ifBlank { null },
                        items.toList()
                    )
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
