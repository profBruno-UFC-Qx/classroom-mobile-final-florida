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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.domain.model.ReceiptListItem
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.Item
import com.example.florida.domain.model.UserSetup

@Composable
fun ReceiptScreen(
    receipts: List<ReceiptListItem>,
    clients: List<Client>,
    currentUser: UserSetup?,
    showCreateDialog: Boolean,
    initialClientId: Long? = null,
    onDismissCreateDialog: () -> Unit,
    onCreateReceipt: (clientId: Long?, items: List<Item>) -> Unit,
    onDeleteReceipt: (ReceiptListItem) -> Unit,
    onOpenReceipt: (ReceiptListItem) -> Unit,
    onShareReceiptPdf: (ReceiptListItem) -> Unit,
) {
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
                onSharePdf = { if (currentUser != null) onShareReceiptPdf(receipt) }
            )
        }
    }

    if (showCreateDialog) {
        ReceiptFormDialog(
            clients = clients,
            receipt = null,
            initialClientId = initialClientId,
            onDismiss = onDismissCreateDialog,
            onConfirm = onCreateReceipt
        )
    }
}
