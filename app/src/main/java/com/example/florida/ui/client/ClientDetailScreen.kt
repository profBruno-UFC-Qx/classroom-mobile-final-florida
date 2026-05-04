package com.example.florida.ui.client

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.extencions.formatForBrl
import com.example.florida.model.Budget
import com.example.florida.model.Client
import com.example.florida.model.Receipt
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ClientDetailScreen(
    client: Client?,
    budgets: List<Budget>,
    receipts: List<Receipt>,
    onEditClient: (Client) -> Unit,
    onCreateBudget: (Client) -> Unit,
    onCreateReceipt: (Client) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (client == null) {
        Text(
            text = stringResource(R.string.client_not_found),
            modifier = modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val totalBudgeted = budgets.sumOf { it.total }
    val totalReceived = receipts.sumOf { it.total }
    val documents = buildList {
        budgets.forEach {
            add(ClientDocument(R.string.budget_number, it.id, it.createdAt, it.total))
        }
        receipts.forEach {
            add(ClientDocument(R.string.receipt_number, it.id, it.date, it.total))
        }
    }.sortedByDescending { it.date }

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
                            text = client.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = client.document,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(onClick = { onEditClient(client) }) {
                        Icon(Icons.Outlined.Edit, contentDescription = null)
                        Text(stringResource(R.string.edit))
                    }
                }
                InfoLine(icon = Icons.Outlined.Phone, text = client.phone)
                InfoLine(icon = Icons.Outlined.LocationOn, text = client.address)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryCard(
                title = stringResource(R.string.budgeted),
                value = totalBudgeted.formatForBrl(),
                subtitle = stringResource(R.string.budget_count, budgets.size),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = stringResource(R.string.received),
                value = totalReceived.formatForBrl(),
                subtitle = stringResource(R.string.receipt_count, receipts.size),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { onCreateBudget(client) },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.new_budget))
            }
            FilledTonalButton(
                onClick = { onCreateReceipt(client) },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.new_receipt))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.customer_history),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (documents.isEmpty()) {
                    Text(
                        text = stringResource(R.string.empty_customer_documents),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    documents.forEachIndexed { index, document ->
                        HistoryRow(document)
                        if (index < documents.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun InfoLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryRow(document: ClientDocument) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(document.titleRes, document.documentId), fontWeight = FontWeight.SemiBold)
            Text(
                document.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(document.total.formatForBrl(), fontWeight = FontWeight.SemiBold)
    }
}

private data class ClientDocument(
    val titleRes: Int,
    val documentId: Long,
    val date: LocalDateTime,
    val total: Long,
)
