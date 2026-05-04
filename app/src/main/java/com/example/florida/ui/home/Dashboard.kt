package com.example.florida.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.extencions.formatForBrl
import com.example.florida.model.Budget
import com.example.florida.model.Client
import com.example.florida.model.Receipt
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun Dashboard(
    clients: List<Client>,
    budgets: List<Budget>,
    receipts: List<Receipt>,
    onOpenClients: () -> Unit,
    onOpenBudgets: () -> Unit,
    onOpenReceipts: () -> Unit,
    onCreateBudget: () -> Unit,
    onCreateReceipt: () -> Unit,
) {
    val metrics = remember(clients, budgets, receipts) {
        DashboardMetrics.from(clients, budgets, receipts)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.business_summary),
            style = MaterialTheme.typography.titleLarge
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = stringResource(R.string.received),
                value = metrics.totalReceived.formatForBrl(),
                subtitle = stringResource(R.string.in_receipts),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.open_amount),
                value = metrics.totalBudgeted.formatForBrl(),
                subtitle = stringResource(R.string.in_budgets),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = stringResource(R.string.client),
                value = metrics.clientCount.toString(),
                subtitle = stringResource(R.string.active_clients),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.this_month),
                value = metrics.monthReceived.formatForBrl(),
                subtitle = stringResource(R.string.received).lowercase(),
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.shortcuts),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                QuickActionRow(
                    icon = Icons.Outlined.Person,
                    label = stringResource(R.string.client),
                    value = stringResource(R.string.registered_clients, metrics.clientCount),
                    onClick = onOpenClients
                )
                QuickActionRow(
                    icon = Icons.Outlined.Edit,
                    label = stringResource(R.string.budget),
                    value = stringResource(R.string.created_budgets, metrics.budgetCount),
                    onClick = onOpenBudgets
                )
                QuickActionRow(
                    icon = Icons.Outlined.Done,
                    label = stringResource(R.string.receipt),
                    value = stringResource(R.string.issued_receipts, metrics.receiptCount),
                    onClick = onOpenReceipts
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilledTonalButton(
                onClick = onCreateBudget,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.budget))
            }
            FilledTonalButton(
                onClick = onCreateReceipt,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.receipt))
            }
        }

        RecentDocumentsCard(metrics.recentDocuments)
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.SemiBold)
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.78f)
            )
        }
    }
}

@Composable
private fun RecentDocumentsCard(documents: List<RecentDocument>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.recent_documents),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (documents.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_recent_documents),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            documents.forEachIndexed { index, document ->
                RecentDocumentRow(document)
                if (index < documents.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun RecentDocumentRow(document: RecentDocument) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
            text = stringResource(document.titleRes, document.documentId),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(
                    R.string.recent_document_meta,
                    document.clientName.ifBlank { stringResource(R.string.customer_not_informed) },
                    document.createdAt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = document.total.formatForBrl(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class DashboardMetrics(
    val clientCount: Int,
    val budgetCount: Int,
    val receiptCount: Int,
    val totalBudgeted: Long,
    val totalReceived: Long,
    val monthReceived: Long,
    val recentDocuments: List<RecentDocument>,
) {
    companion object {
        fun from(
            clients: List<Client>,
            budgets: List<Budget>,
            receipts: List<Receipt>,
        ): DashboardMetrics {
            val currentMonth = LocalDate.now().month
            val currentYear = LocalDate.now().year
            val recentBudgets = budgets.map {
                RecentDocument(
                    titleRes = R.string.budget_number,
                    documentId = it.id,
                    clientName = it.client?.name ?: "",
                    total = it.total,
                    createdAt = it.createdAt
                )
            }
            val recentReceipts = receipts.map {
                RecentDocument(
                    titleRes = R.string.receipt_number,
                    documentId = it.id,
                    clientName = it.client?.name ?: "",
                    total = it.total,
                    createdAt = it.createdAt
                )
            }

            return DashboardMetrics(
                clientCount = clients.size,
                budgetCount = budgets.size,
                receiptCount = receipts.size,
                totalBudgeted = budgets.sumOf { it.total },
                totalReceived = receipts.sumOf { it.total },
                monthReceived = receipts
                    .filter { it.date.month == currentMonth && it.date.year == currentYear }
                    .sumOf { it.total },
                recentDocuments = (recentBudgets + recentReceipts)
                    .sortedByDescending { it.createdAt }
                    .take(4)
            )
        }
    }
}

private data class RecentDocument(
    val titleRes: Int,
    val documentId: Long,
    val clientName: String,
    val total: Long,
    val createdAt: LocalDateTime,
)
