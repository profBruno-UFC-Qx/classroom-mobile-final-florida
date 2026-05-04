package com.example.florida.ui.receipt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.model.Client
import com.example.florida.model.Item
import com.example.florida.model.Receipt
import com.example.florida.model.SessionManager
import com.example.florida.ui.utils.ReceiptPdfCreate
import com.example.florida.ui.utils.sharePdf
import java.time.format.DateTimeFormatter

@Composable
fun ReceiptScreen(
    receipts: List<Receipt>,
    clients: List<Client>,
    showCreateDialog: Boolean,
    initialClientId: Long? = null,
    onDismissCreateDialog: () -> Unit,
    onCreateReceipt: (clientId: Long?, items: List<Item>) -> Unit,
    onDeleteReceipt: (Receipt) -> Unit,
    onOpenReceipt: (Receipt) -> Unit,
) {
    val context = LocalContext.current
    val user = SessionManager.getCurrentUser()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (receipts.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_receipts),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        lazyItems(receipts, key = { it.id }) { receipt ->
            ReceiptCard(
                receipt = receipt,
                onDelete = { onDeleteReceipt(receipt) },
                onOpen = { onOpenReceipt(receipt) },
                onSharePdf = {
                    user?.let {
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
                }
            )
        }
    }

    if (showCreateDialog) {
        CreateReceiptDialog(
            clients = clients,
            initialClientId = initialClientId,
            onDismiss = onDismissCreateDialog,
            onConfirm = onCreateReceipt
        )
    }
}
