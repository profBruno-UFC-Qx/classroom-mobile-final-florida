package com.example.florida.ui.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.ui.utils.CpfCnpjVisualTransformation
import com.example.florida.ui.utils.PhoneVisualTransformation

@Composable
fun ClientFormFields(
    name: String,
    onNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    document: String,
    onDocumentChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    errorMessage: String?,
    showFieldErrors: Boolean,
    onSelectImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            name,
            onNameChange,
            label = { Text(stringResource(R.string.name)) },
            isError = showFieldErrors && name.isBlank(),
            singleLine = true
        )
        OutlinedTextField(
            address,
            onAddressChange,
            label = { Text(stringResource(R.string.address)) },
            isError = showFieldErrors && address.isBlank(),
            singleLine = true
        )
        OutlinedTextField(
            document,
            onDocumentChange,
            label = { Text(stringResource(R.string.document)) },
            visualTransformation = CpfCnpjVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showFieldErrors && document.isBlank(),
            singleLine = true
        )
        OutlinedTextField(
            phone,
            onPhoneChange,
            label = { Text(stringResource(R.string.phone)) },
            visualTransformation = PhoneVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = showFieldErrors && phone.isBlank(),
            singleLine = true
        )
        errorMessage?.let {
            Text(it)
        }
        Button(onClick = onSelectImage) {
            Text(stringResource(id = R.string.select_image))
        }
    }
}
