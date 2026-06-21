package com.example.florida.ui.client

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.florida.R
import com.example.florida.domain.validation.FormValidators
import com.example.florida.domain.validation.ValidationError
import com.example.florida.extensions.normalizeCpfCnpjInput
import com.example.florida.extensions.normalizePhoneInput
import com.example.florida.domain.model.Client
import com.example.florida.persistence.ImageStorage
import com.example.florida.ui.home.SplashScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ClientFormDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        document: String,
        phone: String,
        address: String,
        imagePath: String?
    ) -> Unit,
    client: Client? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var name by remember(client) { mutableStateOf(client?.name.orEmpty()) }
    var document by remember(client) { mutableStateOf(client?.document.orEmpty().normalizeCpfCnpjInput()) }
    var phone by remember(client) { mutableStateOf(client?.phone.orEmpty().normalizePhoneInput()) }
    var address by remember(client) { mutableStateOf(client?.address.orEmpty()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(if (client == null) stringResource(R.string.new_client) else stringResource(R.string.edit_client)) },
        text = {
            ClientFormFields(
                name = name,
                onNameChange = { name = it },
                address = address,
                onAddressChange = { address = it },
                document = document,
                onDocumentChange = { document = it.normalizeCpfCnpjInput() },
                phone = phone,
                onPhoneChange = { phone = it.normalizePhoneInput() },
                errorMessage = errorMessage,
                showFieldErrors = errorMessage != null,
                onSelectImage = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                enabled = !isLoading,
                onClick = {
                    val validation = FormValidators.validateClient(
                        name = name,
                        document = document,
                        phone = phone,
                        address = address
                    )
                    if (!validation.isValid) {
                        errorMessage = context.getValidationMessage(validation.errors.first())
                        return@Button
                    }
                    scope.launch(Dispatchers.Default) {
                        isLoading = true
                        try {
                            val imagePath = imageUri?.let { uri ->
                                withContext(Dispatchers.IO) {
                                    ImageStorage.saveImage(context, uri)
                                }
                            } ?: client?.imagePath
                            onConfirm(
                                name,
                                document,
                                phone,
                                address,
                                imagePath
                            )

                        } catch (e: Exception) {
                            errorMessage = context.getString(R.string.save_error, e.message ?: "")
                        } finally {
                            isLoading = false
                        }
                    }
                 }
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
    if (isLoading) {
        SplashScreen()
    }
}

private fun android.content.Context.getValidationMessage(error: ValidationError): String {
    return when (error) {
        ValidationError.REQUIRED_NAME,
        ValidationError.REQUIRED_DOCUMENT,
        ValidationError.REQUIRED_ADDRESS,
        ValidationError.EMPTY_ITEMS -> getString(R.string.required_fields_error)
        ValidationError.INVALID_DOCUMENT -> getString(R.string.invalid_document_error)
        ValidationError.INVALID_PHONE -> getString(R.string.invalid_phone_error)
        ValidationError.INVALID_ITEM_DESCRIPTION -> getString(R.string.item_description_error)
        ValidationError.INVALID_ITEM_QUANTITY -> getString(R.string.item_quantity_error)
        ValidationError.INVALID_ITEM_PRICE -> getString(R.string.item_price_error)
    }
}
