package com.example.florida.ui.client

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.model.Client

@Composable
fun ClientScreen(
    modifier: Modifier = Modifier
) {
    var showCreateClientDialog by remember { mutableStateOf(false) }
    val clients = remember {
        mutableStateListOf(
            Client(name = "Francisco", address = "Rua dos Bobos", document = "06364254307", phone = "123456789", imagePath = null),
        )
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
        ) {
            item {
                Button(
                    onClick = { showCreateClientDialog = true },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(stringResource(R.string.new_client))
                }
            }
            items(clients) { client ->
                ClientCard(
                    client,
                    onDelete = {
                        clients.remove(client)
                    },
                    onClicked = { },
                    onClickBudget = { },
                    onClickReceipt = { },
                    modifier = Modifier
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                )
            }
        }


    }

    if (showCreateClientDialog) {
        CreateClientDialog(
            onDismiss = { showCreateClientDialog = false },
            onConfirm = { name, document, phone, address, imagePath ->
                clients.add(
                    Client(name, address, document, phone, imagePath)
                )
                showCreateClientDialog = false
            }
        )
    }
}