package com.example.florida.ui.receipt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.florida.extencions.formatForBrl
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
    onDismissCreateDialog: () -> Unit,
    onCreateReceipt: (clientId: Long?, items: List<Item>) -> Unit,
    onDeleteReceipt: (Receipt) -> Unit,
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
                    text = "Nenhum recibo cadastrado.",
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
                        sharePdf(context, file, "Compartilhar recibo")
                    }
                }
            )
        }
    }

    if (showCreateDialog) {
        CreateReceiptDialog(
            clients = clients,
            onDismiss = onDismissCreateDialog,
            onConfirm = onCreateReceipt
        )
    }
}

@Composable
private fun ReceiptCard(
    receipt: Receipt,
    onDelete: () -> Unit,
    onSharePdf: () -> Unit,
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
                Text(
                    text = receipt.client?.name ?: "Cliente não informado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "#${receipt.id}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = receipt.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = "Itens: ${receipt.items.size}")
            Text(
                text = receipt.total.formatForBrl(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onSharePdf) {
                    Text("PDF")
                }
                TextButton(onClick = onDelete) {
                    Text("Excluir")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateReceiptDialog(
    clients: List<Client>,
    onDismiss: () -> Unit,
    onConfirm: (clientId: Long?, items: List<Item>) -> Unit,
) {
    var selectedClient by remember { mutableStateOf<Client?>(clients.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<Item>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo recibo") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedClient?.name ?: "Sem cliente",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Cliente") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sem cliente") },
                                onClick = {
                                    selectedClient = null
                                    expanded = false
                                }
                            )
                            clients.forEach { client ->
                                DropdownMenuItem(
                                    text = { Text(client.name) },
                                    onClick = {
                                        selectedClient = client
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Text("Itens", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                }
                lazyItems(items) { item ->
                    AssistChip(
                        onClick = { items.remove(item) },
                        label = { Text("${item.qty}x ${item.description} - ${item.total.formatForBrl()}") }
                    )
                }
                item {
                    OutlinedTextField(description, { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(qty, { qty = it.filter(Char::isDigit) }, label = { Text("Qtd") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(price, { price = it }, label = { Text("Valor") }, modifier = Modifier.weight(1f))
                    }
                    Button(
                        onClick = {
                            val parsedQty = qty.toIntOrNull() ?: 1
                            val parsedPrice = price.replace(",", ".").toDoubleOrNull() ?: 0.0
                            if (description.isNotBlank() && parsedPrice > 0.0) {
                                items.add(Item(description = description, qty = parsedQty, price = parsedPrice))
                                description = ""
                                qty = "1"
                                price = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("Adicionar item")
                    }
                }
                item {
                    Text(
                        text = "Total: ${items.sumOf { it.total }.formatForBrl()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = items.isNotEmpty(),
                onClick = { onConfirm(selectedClient?.id, items.toList()) }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
