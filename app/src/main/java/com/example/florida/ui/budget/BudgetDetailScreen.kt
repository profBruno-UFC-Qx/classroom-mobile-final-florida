package com.example.florida.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.extencions.formatForBrl
import com.example.florida.model.Budget
import com.example.florida.model.BudgetStatus
import com.example.florida.model.Receipt
import com.example.florida.model.SessionManager
import com.example.florida.ui.utils.BudgetPdfCreator
import com.example.florida.ui.utils.sharePdf
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BudgetDetailScreen(
    budget: Budget?,
    linkedReceipt: Receipt?,
    onUpdateStatus: (BudgetStatus) -> Unit,
    onCreateReceipt: () -> Unit,
    onOpenReceipt: (Receipt) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (budget == null) {
        Text(
            text = stringResource(R.string.budget_not_found),
            modifier = modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val context = LocalContext.current
    val user = SessionManager.getCurrentUser()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.budget_number, budget.id),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = budget.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    AssistChip(onClick = {}, label = { Text(stringResource(budget.status.labelRes)) })
                }

                Text(
                    text = budget.client?.name ?: stringResource(R.string.customer_not_informed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.total_value, budget.total.formatForBrl()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                budget.entrega?.let { Text(stringResource(R.string.delivery_value, it)) }
                budget.validade?.let { Text(stringResource(R.string.validity_value, it)) }
                budget.notes?.let { Text(stringResource(R.string.notes_value, it)) }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(stringResource(R.string.items), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                budget.items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.description, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${item.qty} x ${item.price.formatForBrl()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(item.total.formatForBrl(), fontWeight = FontWeight.SemiBold)
                    }
                    if (index < budget.items.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }

        linkedReceipt?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.linked_receipt_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.linked_receipt_value, it.id, it.total.formatForBrl()))
                    Button(onClick = { onOpenReceipt(it) }) {
                        Text(stringResource(R.string.open_receipt))
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (budget.status != BudgetStatus.APPROVED) {
                Button(onClick = { onUpdateStatus(BudgetStatus.APPROVED) }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.approve))
                }
            }
            if (budget.status != BudgetStatus.REJECTED) {
                Button(onClick = { onUpdateStatus(BudgetStatus.REJECTED) }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.reject))
                }
            }
        }

        if (budget.status == BudgetStatus.APPROVED && linkedReceipt == null) {
            Button(onClick = onCreateReceipt, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.generate_receipt))
            }
        }

        Button(
            onClick = {
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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.share_pdf))
        }

        TextButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.delete_budget))
        }

        Spacer(Modifier.height(12.dp))
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete_budget)) },
            text = { Text(stringResource(R.string.delete_budget_question)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
