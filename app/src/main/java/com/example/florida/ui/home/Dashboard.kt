package com.example.florida.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.domain.model.DashboardSummary
import com.example.florida.domain.model.DocumentType
import com.example.florida.domain.model.RecentDocumentSummary
import com.example.florida.extensions.formatForBrl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun Dashboard(
    summary: DashboardSummary,
    onOpenClients: () -> Unit,
    onOpenBudgets: () -> Unit,
    onOpenReceipts: () -> Unit,
    onCreateBudget: () -> Unit,
    onCreateReceipt: () -> Unit,
) {
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
                value = summary.totalReceived.formatForBrl(),
                subtitle = stringResource(R.string.in_receipts),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.open_amount),
                value = summary.totalBudgeted.formatForBrl(),
                subtitle = stringResource(R.string.in_budgets),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = stringResource(R.string.client),
                value = summary.clientCount.toString(),
                subtitle = stringResource(R.string.active_clients),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.this_month),
                value = summary.monthReceived.formatForBrl(),
                subtitle = stringResource(R.string.received).lowercase(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
                    value = stringResource(R.string.registered_clients, summary.clientCount),
                    onClick = onOpenClients
                )
                QuickActionRow(
                    icon = Icons.Outlined.Edit,
                    label = stringResource(R.string.budget),
                    value = stringResource(R.string.created_budgets, summary.budgetCount),
                    onClick = onOpenBudgets
                )
                QuickActionRow(
                    icon = Icons.Outlined.Done,
                    label = stringResource(R.string.receipt),
                    value = stringResource(R.string.issued_receipts, summary.receiptCount),
                    onClick = onOpenReceipts
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilledTonalButton(
                onClick = onCreateBudget,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.budget))
            }
            FilledTonalButton(
                onClick = onCreateReceipt,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.receipt))
            }
        }

        RecentDocumentsCard(summary.recentDocuments)
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
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
                color = contentColor
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.78f)
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
private fun RecentDocumentsCard(documents: List<RecentDocumentSummary>) {
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
private fun RecentDocumentRow(document: RecentDocumentSummary) {
    val titleRes = when (document.type) {
        DocumentType.BUDGET -> R.string.budget_number
        DocumentType.RECEIPT -> R.string.receipt_number
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(titleRes, document.documentId),
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
