package com.example.florida.ui.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import java.time.format.DateTimeFormatter

@Composable
fun ClientDocumentList(
    documents: List<ClientDocumentSummary>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                    ClientDocumentRow(document)
                    if (index < documents.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientDocumentRow(document: ClientDocumentSummary) {
    val titleRes = when (document.type) {
        DocumentType.BUDGET -> R.string.budget_number
        DocumentType.RECEIPT -> R.string.receipt_number
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(titleRes, document.documentId), fontWeight = FontWeight.SemiBold)
            Text(
                document.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(document.total.formatForBrl(), fontWeight = FontWeight.SemiBold)
    }
}
