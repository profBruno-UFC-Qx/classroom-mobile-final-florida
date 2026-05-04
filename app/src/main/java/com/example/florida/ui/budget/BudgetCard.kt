package com.example.florida.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.extencions.formatForBrl
import com.example.florida.model.Budget
import com.example.florida.model.BudgetStatus
import java.time.format.DateTimeFormatter

@Composable
fun BudgetCard(
    budget: Budget,
    linkedReceiptId: Long?,
    onDelete: () -> Unit,
    onUpdateStatus: (BudgetStatus) -> Unit,
    onCreateReceipt: () -> Unit,
    onOpen: () -> Unit,
    onOpenLinkedReceipt: () -> Unit,
    onSharePdf: () -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onOpen
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = budget.client?.name ?: stringResource(R.string.customer_not_informed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.short_id, budget.id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = budget.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AssistChip(
                onClick = {},
                label = { Text(stringResource(budget.status.labelRes)) }
            )
            Text(text = stringResource(R.string.items_count, budget.items.size))
            Text(
                text = budget.total.formatForBrl(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (budget.status != BudgetStatus.APPROVED) {
                    TextButton(onClick = { onUpdateStatus(BudgetStatus.APPROVED) }) {
                        Text(stringResource(R.string.approve))
                    }
                }
                if (budget.status != BudgetStatus.REJECTED) {
                    TextButton(onClick = { onUpdateStatus(BudgetStatus.REJECTED) }) {
                        Text(stringResource(R.string.reject))
                    }
                }
                if (budget.status == BudgetStatus.APPROVED && linkedReceiptId == null) {
                    TextButton(onClick = onCreateReceipt) {
                        Text(stringResource(R.string.receipt))
                    }
                }
                if (linkedReceiptId != null) {
                    AssistChip(
                        onClick = onOpenLinkedReceipt,
                        label = { Text(stringResource(R.string.linked_receipt, linkedReceiptId)) }
                    )
                }
                TextButton(onClick = onSharePdf) {
                    Text(stringResource(R.string.pdf))
                }
                TextButton(onClick = { showDeleteConfirm = true }) {
                    Text(stringResource(R.string.delete))
                }
            }
        }
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