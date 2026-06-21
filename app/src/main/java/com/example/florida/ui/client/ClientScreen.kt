package com.example.florida.ui.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.domain.model.ClientListItem
import com.example.florida.domain.model.Client

@Composable
fun ClientScreen(
    clients: List<ClientListItem>,
    onDeleteClick: (Client) -> Unit,
    onEditClick: (Client) -> Unit,
    onOpenClient: (Client) -> Unit,
    onClickBudget: (Client) -> Unit = {},
    onClickReceipt: (Client) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
        ) {
            if (clients.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_clients),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(clients) { client ->
                val fullClient = client.toClient()
                ClientCard(
                    fullClient,
                    onDelete = { onDeleteClick(fullClient) },
                    onClicked = { onOpenClient(fullClient) },
                    onEdit = { onEditClick(fullClient) },
                    onClickBudget = { onClickBudget(fullClient) },
                    onClickReceipt = { onClickReceipt(fullClient) },
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    }
}

private fun ClientListItem.toClient(): Client {
    return Client(
        id = id,
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = imagePath,
        deleted = false
    )
}
