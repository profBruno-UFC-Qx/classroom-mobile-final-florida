package com.example.florida.ui.receipt

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.example.florida.extensions.formatForBrl
import com.example.florida.model.Budget
import com.example.florida.model.Receipt
import com.example.florida.model.UserSetup
import com.example.florida.document.pdf.ReceiptPdfCreate
import com.example.florida.document.pdf.sharePdf
import java.time.format.DateTimeFormatter

@Composable
fun ReceiptDetailScreen(
    receipt: Receipt?,
    originBudget: Budget?,
    currentUser: UserSetup?,
    onEdit: (Receipt) -> Unit,
    onOpenBudget: (Budget) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (receipt == null) {
        Text(
            text = stringResource(R.string.receipt_not_found),
            modifier = modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.receipt_number, receipt.id),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = receipt.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = receipt.client?.name ?: stringResource(R.string.customer_not_informed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.total_value, receipt.total.formatForBrl()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        originBudget?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.origin), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.generated_from_budget, it.id))
                    Button(onClick = { onOpenBudget(it) }) {
                        Text(stringResource(R.string.open_budget))
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(stringResource(R.string.items), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                receipt.items.forEachIndexed { index, item ->
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
                    if (index < receipt.items.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }

        Button(onClick = { onEdit(receipt) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.edit_receipt))
        }

        Button(
            onClick = {
                currentUser?.let {
                    val file = ReceiptPdfCreate(
                        context = context,
                        user = it,
                        cliente = receipt.client,
                        itens = receipt.items,
                        budgetNumber = receipt.id.toInt(),
                        dateStr = receipt.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    )
                    sharePdf(context, file, context.getString(R.string.share_receipt))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.share_pdf))
        }

        TextButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.delete_receipt),
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(12.dp))
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete_receipt)) },
            text = { Text(stringResource(R.string.delete_receipt_question)) },
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
