package com.example.florida.ui.receipt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.extencions.formatForBrl
import com.example.florida.extencions.parseCurrencyToCents
import com.example.florida.model.Client
import com.example.florida.model.Item
import kotlin.collections.forEach
import androidx.compose.foundation.lazy.items as lazyItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReceiptDialog(
    clients: List<Client>,
    initialClientId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (clientId: Long?, items: List<Item>) -> Unit,
) {
    var selectedClient by remember(initialClientId, clients) {
        mutableStateOf(clients.firstOrNull { it.id == initialClientId } ?: clients.firstOrNull())
    }
    var expanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<Item>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_receipt_title)) },
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
                            value = selectedClient?.name ?: stringResource(R.string.no_client),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.client)) },
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
                                text = { Text(stringResource(R.string.no_client)) },
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
                    Text(stringResource(R.string.items), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                }
                lazyItems(items) { item ->
                    AssistChip(
                        onClick = { items.remove(item) },
                        label = { Text(stringResource(R.string.item_summary, item.qty, item.description, item.total.formatForBrl())) }
                    )
                }
                item {
                    OutlinedTextField(description, { description = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(qty, { qty = it.filter(Char::isDigit) }, label = { Text(stringResource(R.string.quantity)) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(price, { price = it }, label = { Text(stringResource(R.string.value)) }, modifier = Modifier.weight(1f))
                    }
                    Button(
                        onClick = {
                            val parsedQty = qty.toIntOrNull() ?: 1
                            val parsedPrice = price.parseCurrencyToCents() ?: 0
                            if (description.isNotBlank() && parsedPrice > 0) {
                                items.add(Item(description = description, qty = parsedQty, price = parsedPrice))
                                description = ""
                                qty = "1"
                                price = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text(stringResource(R.string.add_item))
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.total_value, items.sumOf { it.total }.formatForBrl()),
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
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}