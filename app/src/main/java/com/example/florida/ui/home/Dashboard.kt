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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            text = "Resumo do negócio",
            style = MaterialTheme.typography.titleLarge
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Recebido",
                value = metrics.totalReceived.formatForBrl(),
                subtitle = "em recibos",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Em aberto",
                value = metrics.totalBudgeted.formatForBrl(),
                subtitle = "em orçamentos",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Clientes",
                value = metrics.clientCount.toString(),
                subtitle = "ativos",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Este mês",
                value = metrics.monthReceived.formatForBrl(),
                subtitle = "recebido",
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
                    text = "Atalhos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                QuickActionRow(
                    icon = Icons.Outlined.Person,
                    label = "Clientes",
                    value = "${metrics.clientCount} cadastrados",
                    onClick = onOpenClients
                )
                QuickActionRow(
                    icon = Icons.Outlined.Edit,
                    label = "Orçamentos",
                    value = "${metrics.budgetCount} criados",
                    onClick = onOpenBudgets
                )
                QuickActionRow(
                    icon = Icons.Outlined.Done,
                    label = "Recibos",
                    value = "${metrics.receiptCount} emitidos",
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
                Text("Orçamento")
            }
            FilledTonalButton(
                onClick = onCreateReceipt,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Recibo")
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
                text = "Últimos documentos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (documents.isEmpty()) {
                Text(
                    text = "Crie um orçamento ou recibo para acompanhar sua atividade aqui.",
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
                text = document.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${document.clientName} • ${document.createdAt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))}",
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
    val totalBudgeted: Double,
    val totalReceived: Double,
    val monthReceived: Double,
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
                    title = "Orçamento #${it.id}",
                    clientName = it.client?.name ?: "Cliente não informado",
                    total = it.total,
                    createdAt = it.createdAt
                )
            }
            val recentReceipts = receipts.map {
                RecentDocument(
                    title = "Recibo #${it.id}",
                    clientName = it.client?.name ?: "Cliente não informado",
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
    val title: String,
    val clientName: String,
    val total: Double,
    val createdAt: LocalDateTime,
)
