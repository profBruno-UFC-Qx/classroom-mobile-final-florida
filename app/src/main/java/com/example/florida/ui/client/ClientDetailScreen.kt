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
import com.example.florida.domain.model.ClientDocumentSummary
import com.example.florida.domain.model.DocumentType
import com.example.florida.extensions.formatForBrl
import com.example.florida.model.Client

@Composable
fun ClientDetailScreen(
    client: Client?,
    documents: List<ClientDocumentSummary>,
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

    val totalBudgeted = documents
        .filter { it.type == DocumentType.BUDGET }
        .sumOf { it.total }
    val totalReceived = documents
        .filter { it.type == DocumentType.RECEIPT }
        .sumOf { it.total }
    val budgetCount = documents.count { it.type == DocumentType.BUDGET }
    val receiptCount = documents.count { it.type == DocumentType.RECEIPT }

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
                subtitle = stringResource(R.string.budget_count, budgetCount),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = stringResource(R.string.received),
                value = totalReceived.formatForBrl(),
                subtitle = stringResource(R.string.receipt_count, receiptCount),
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

        ClientDocumentList(documents = documents)

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
