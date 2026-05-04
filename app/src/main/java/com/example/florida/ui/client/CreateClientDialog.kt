package com.example.florida.ui.client

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.model.Client
import com.example.florida.persistence.ImageStorage
import com.example.florida.ui.home.SplashScreen
import com.example.florida.ui.utils.CpfCnpjVisualTransformation
import com.example.florida.ui.utils.PhoneVisualTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateClientDialog(
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
    var document by remember(client) { mutableStateOf(client?.document.orEmpty()) }
    var phone by remember(client) { mutableStateOf(client?.phone.orEmpty()) }
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
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    name,
                    { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    isError = errorMessage != null && name.isBlank(),
                    singleLine = true
                )
                OutlinedTextField(
                    address,
                    { address = it },
                    label = { Text(stringResource(R.string.address)) },
                    isError = errorMessage != null && address.isBlank(),
                    singleLine = true
                )
                OutlinedTextField(
                    document,
                    {if (it.length <= 14) document = it},
                    label = { Text(stringResource(R.string.document)) },
                    visualTransformation = CpfCnpjVisualTransformation(),
                    isError = errorMessage != null && document.isBlank(),
                    singleLine = true
                )
                OutlinedTextField(
                    phone,
                    { if (it.length <= 14) phone = it },
                    label = { Text(stringResource(R.string.phone)) },
                    visualTransformation = PhoneVisualTransformation(),
                    isError = errorMessage != null && phone.isBlank(),
                    singleLine = true
                )
                errorMessage?.let {
                    Text(it)
                }
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.select_image))
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading,
                onClick = {
                    if (name.isBlank() || address.isBlank() || document.isBlank() || phone.isBlank()) {
                        errorMessage = context.getString(R.string.required_fields_error)
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
