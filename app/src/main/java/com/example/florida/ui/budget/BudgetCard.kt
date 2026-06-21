package com.example.florida.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import com.example.florida.domain.model.BudgetListItem
import com.example.florida.extensions.formatForBrl
import com.example.florida.domain.model.BudgetStatus
import java.time.format.DateTimeFormatter

@Composable
fun BudgetCard(
    budget: BudgetListItem,
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
                    text = budget.clientName ?: stringResource(R.string.customer_not_informed),
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
                label = { Text(stringResource(budget.status.labelRes)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = budget.status.containerColor(),
                    labelColor = budget.status.contentColor()
                )
            )
            Text(text = stringResource(R.string.items_count, budget.itemCount))
            Text(
                text = budget.total.formatForBrl(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (budget.status != BudgetStatus.APPROVED) {
                    TextButton(onClick = { onUpdateStatus(BudgetStatus.APPROVED) }) {
                        Text(
                            text = stringResource(R.string.approve),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                if (budget.status != BudgetStatus.REJECTED) {
                    TextButton(onClick = { onUpdateStatus(BudgetStatus.REJECTED) }) {
                        Text(
                            text = stringResource(R.string.reject),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                if (budget.status == BudgetStatus.APPROVED && budget.linkedReceiptId == null) {
                    TextButton(onClick = onCreateReceipt) {
                        Text(stringResource(R.string.receipt))
                    }
                }
                if (budget.linkedReceiptId != null) {
                    AssistChip(
                        onClick = onOpenLinkedReceipt,
                        label = { Text(stringResource(R.string.linked_receipt, budget.linkedReceiptId)) }
                    )
                }
                TextButton(onClick = onSharePdf) {
                    Text(stringResource(R.string.pdf))
                }
                TextButton(onClick = { showDeleteConfirm = true }) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
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

@Composable
private fun BudgetStatus.containerColor() = when (this) {
    BudgetStatus.APPROVED -> MaterialTheme.colorScheme.secondaryContainer
    BudgetStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
    BudgetStatus.EXPIRED -> MaterialTheme.colorScheme.surfaceVariant
    BudgetStatus.SENT -> MaterialTheme.colorScheme.primaryContainer
    BudgetStatus.DRAFT -> MaterialTheme.colorScheme.tertiaryContainer
}

@Composable
private fun BudgetStatus.contentColor() = when (this) {
    BudgetStatus.APPROVED -> MaterialTheme.colorScheme.onSecondaryContainer
    BudgetStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
    BudgetStatus.EXPIRED -> MaterialTheme.colorScheme.onSurfaceVariant
    BudgetStatus.SENT -> MaterialTheme.colorScheme.onPrimaryContainer
    BudgetStatus.DRAFT -> MaterialTheme.colorScheme.onTertiaryContainer
}
