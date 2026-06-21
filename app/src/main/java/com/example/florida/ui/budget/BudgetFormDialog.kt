package com.example.florida.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.domain.validation.FormValidators
import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetFormDialog(
    clients: List<Client>,
    budget: Budget? = null,
    initialClientId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (clientId: Long?, notes: String?, validade: String?, entrega: String?, items: List<Item>) -> Unit,
) {
    val selectedClientId = budget?.clientId ?: initialClientId
    var selectedClient by remember(selectedClientId, clients) {
        mutableStateOf(clients.firstOrNull { it.id == selectedClientId } ?: clients.firstOrNull())
    }
    var expanded by remember { mutableStateOf(false) }
    var notes by remember(budget?.id) { mutableStateOf(budget?.notes.orEmpty()) }
    var validade by remember(budget?.id) { mutableStateOf(budget?.validade.orEmpty()) }
    var entrega by remember(budget?.id) { mutableStateOf(budget?.entrega.orEmpty()) }
    val items = remember(budget?.id) {
        mutableStateListOf<Item>().also { it.addAll(budget?.items.orEmpty()) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if (budget == null) R.string.new_budget_title else R.string.edit_budget)) },
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
                    OutlinedTextField(notes, { notes = it }, label = { Text(stringResource(R.string.notes)) }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(validade, { validade = it }, label = { Text(stringResource(R.string.validity)) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(entrega, { entrega = it }, label = { Text(stringResource(R.string.delivery)) }, modifier = Modifier.weight(1f))
                    }
                }
                budgetItemEditor(items)
            }
        },
        confirmButton = {
            Button(
                enabled = FormValidators.validateItems(items).isValid,
                onClick = {
                    onConfirm(
                        selectedClient?.id,
                        notes.ifBlank { null },
                        validade.ifBlank { null },
                        entrega.ifBlank { null },
                        items.toList()
                    )
                }
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
