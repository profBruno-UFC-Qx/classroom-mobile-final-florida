package com.example.florida.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.florida.model.SessionManager
import com.example.florida.model.UserSetup

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val currentUser = SessionManager.getCurrentUser() ?: UserSetup()
    val scrollState = rememberScrollState()
    var name by remember(currentUser) { mutableStateOf(currentUser.name) }
    var document by remember(currentUser) { mutableStateOf(currentUser.document) }
    var phone by remember(currentUser) { mutableStateOf(currentUser.phone) }
    var street by remember(currentUser) { mutableStateOf(currentUser.street) }
    var number by remember(currentUser) { mutableStateOf(currentUser.number) }
    var neighborhood by remember(currentUser) { mutableStateOf(currentUser.neighborhood) }
    var city by remember(currentUser) { mutableStateOf(currentUser.city) }
    var state by remember(currentUser) { mutableStateOf(currentUser.state) }
    var savedMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Dados do emissor",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Essas informações aparecem nos orçamentos e recibos em PDF.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                savedMessage = null
            },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = document,
            onValueChange = {
                document = it
                savedMessage = null
            },
            label = { Text("CPF/CNPJ") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                savedMessage = null
            },
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = street,
            onValueChange = {
                street = it
                savedMessage = null
            },
            label = { Text("Rua") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = number,
                onValueChange = {
                    number = it
                    savedMessage = null
                },
                label = { Text("Número") },
                modifier = Modifier.weight(0.45f),
                singleLine = true
            )
            OutlinedTextField(
                value = neighborhood,
                onValueChange = {
                    neighborhood = it
                    savedMessage = null
                },
                label = { Text("Bairro") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = city,
                onValueChange = {
                    city = it
                    savedMessage = null
                },
                label = { Text("Cidade") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = state,
                onValueChange = {
                    state = it.take(2).uppercase()
                    savedMessage = null
                },
                label = { Text("UF") },
                modifier = Modifier.weight(0.35f),
                singleLine = true
            )
        }

        Button(
            onClick = {
                SessionManager.updateUser(
                    currentUser.copy(
                        name = name,
                        document = document,
                        phone = phone,
                        street = street,
                        number = number,
                        neighborhood = neighborhood,
                        city = city,
                        state = state
                    )
                )
                savedMessage = "Dados salvos."
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && document.isNotBlank()
        ) {
            Text("Salvar alterações")
        }

        savedMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
