package com.example.florida.ui.settings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.domain.validation.FormValidators
import com.example.florida.domain.validation.ValidationError
import com.example.florida.extensions.normalizeCpfCnpjInput
import com.example.florida.extensions.normalizePhoneInput
import com.example.florida.domain.model.UserSetup
import com.example.florida.ui.utils.CpfCnpjVisualTransformation
import com.example.florida.ui.utils.PhoneVisualTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SettingsScreen(
    currentUser: UserSetup?,
    onSaveUser: (
        user: UserSetup,
        imageUri: Uri?,
        onSaved: (String?) -> Unit,
        onError: (Throwable) -> Unit,
    ) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val issuer = currentUser
    if (issuer == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var name by remember(issuer) { mutableStateOf(issuer.name) }
    var document by remember(issuer) { mutableStateOf(issuer.document.normalizeCpfCnpjInput()) }
    var phone by remember(issuer) { mutableStateOf(issuer.phone.normalizePhoneInput()) }
    var street by remember(issuer) { mutableStateOf(issuer.street) }
    var number by remember(issuer) { mutableStateOf(issuer.number) }
    var neighborhood by remember(issuer) { mutableStateOf(issuer.neighborhood) }
    var city by remember(issuer) { mutableStateOf(issuer.city) }
    var state by remember(issuer) { mutableStateOf(issuer.state) }
    var imagePath by remember(issuer) { mutableStateOf(issuer.imagePath) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var savedMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    val selectedBitmap = rememberBitmapFromUri(selectedImageUri)
    val savedBitmap = rememberBitmapFromPath(imagePath)
    val previewBitmap = selectedBitmap.value ?: savedBitmap.value

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        if (uri != null) {
            savedMessage = null
            errorMessage = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = stringResource(R.string.issuer_data_help),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SettingsSection(
            title = stringResource(R.string.image_for_pdf),
            description = stringResource(R.string.image_for_pdf_help)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PdfImagePreview(bitmap = previewBitmap)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (previewBitmap != null) {
                            stringResource(R.string.image_selected)
                        } else {
                            stringResource(R.string.no_image)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = stringResource(R.string.image_for_pdf_status),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (previewBitmap != null) {
                                stringResource(R.string.change_image)
                            } else {
                                stringResource(R.string.select_image)
                            }
                        )
                    }
                }
            }
        }

        SettingsSection(
            title = stringResource(R.string.business_data),
            description = stringResource(R.string.business_data_help)
        ) {
            SettingsTextField(
                value = name,
                onValueChange = {
                    name = it
                    clearFeedback(
                        clearSaved = { savedMessage = null },
                        clearError = { errorMessage = null }
                    )
                },
                label = stringResource(R.string.name),
                isError = errorMessage != null && name.isBlank()
            )
            SettingsTextField(
                value = document,
                onValueChange = {
                    document = it.normalizeCpfCnpjInput()
                    clearFeedback(
                        clearSaved = { savedMessage = null },
                        clearError = { errorMessage = null }
                    )
                },
                label = stringResource(R.string.document),
                visualTransformation = CpfCnpjVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorMessage != null && document.isBlank()
            )
            SettingsTextField(
                value = phone,
                onValueChange = {
                    phone = it.normalizePhoneInput()
                    clearFeedback(
                        clearSaved = { savedMessage = null },
                        clearError = { errorMessage = null }
                    )
                },
                label = stringResource(R.string.phone),
                visualTransformation = PhoneVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }

        SettingsSection(
            title = stringResource(R.string.address_data),
            description = stringResource(R.string.address_data_help)
        ) {
            SettingsTextField(
                value = street,
                onValueChange = {
                    street = it
                    savedMessage = null
                },
                label = stringResource(R.string.street)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsTextField(
                    value = number,
                    onValueChange = {
                        number = it
                        savedMessage = null
                    },
                    label = stringResource(R.string.number),
                    modifier = Modifier.weight(0.45f)
                )
                SettingsTextField(
                    value = neighborhood,
                    onValueChange = {
                        neighborhood = it
                        savedMessage = null
                    },
                    label = stringResource(R.string.neighborhood),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsTextField(
                    value = city,
                    onValueChange = {
                        city = it
                        savedMessage = null
                    },
                    label = stringResource(R.string.city),
                    modifier = Modifier.weight(1f)
                )
                SettingsTextField(
                    value = state,
                    onValueChange = {
                        state = it.take(2).uppercase()
                        savedMessage = null
                    },
                    label = stringResource(R.string.state),
                    modifier = Modifier.weight(0.35f)
                )
            }
        }

        Button(
            onClick = {
                val validation = FormValidators.validateIssuer(
                    name = name,
                    document = document,
                    phone = phone
                )
                if (!validation.isValid) {
                    errorMessage = context.getValidationMessage(validation.errors.first())
                    savedMessage = null
                    return@Button
                }

                isSaving = true
                savedMessage = null
                errorMessage = null

                val updatedUser = issuer.copy(
                    name = name,
                    document = document,
                    phone = phone,
                    street = street,
                    number = number,
                    neighborhood = neighborhood,
                    city = city,
                    state = state,
                    imagePath = imagePath
                )

                onSaveUser(
                    updatedUser,
                    selectedImageUri,
                    { pathToSave ->
                        imagePath = pathToSave
                        selectedImageUri = null
                        savedMessage = context.getString(R.string.saved_data)
                        isSaving = false
                    },
                    { exception ->
                        errorMessage = context.getString(
                            R.string.save_error,
                            exception.message ?: context.getString(R.string.unknown_error)
                        )
                        isSaving = false
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(R.string.save_changes))
            }
        }

        FeedbackMessage(
            savedMessage = savedMessage,
            errorMessage = errorMessage
        )

        SettingsSection(
            title = stringResource(R.string.account),
            description = stringResource(R.string.logout_help)
        ) {
            OutlinedButton(
                onClick = { showLogoutConfirm = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.logout))
            }
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text(stringResource(R.string.logout)) },
            text = { Text(stringResource(R.string.logout_question)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    }
                ) {
                    Text(stringResource(R.string.logout))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}

@Composable
private fun PdfImagePreview(bitmap: Bitmap?) {
    Surface(
        modifier = Modifier.size(width = 112.dp, height = 88.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.image_selected),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_image),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        isError = isError
    )
}

@Composable
private fun FeedbackMessage(
    savedMessage: String?,
    errorMessage: String?
) {
    val message = errorMessage ?: savedMessage ?: return
    val isError = errorMessage != null

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isError) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            color = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun rememberBitmapFromUri(uri: Uri?): State<Bitmap?> {
    val context = LocalContext.current
    return produceState(initialValue = null, uri) {
        value = uri?.let {
            withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(it).use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }.getOrNull()
            }
        }
    }
}

@Composable
private fun rememberBitmapFromPath(path: String?): State<Bitmap?> {
    return produceState(initialValue = null, path) {
        value = path?.let {
            withContext(Dispatchers.IO) {
                runCatching { BitmapFactory.decodeFile(it) }.getOrNull()
            }
        }
    }
}

private fun clearFeedback(
    clearSaved: () -> Unit,
    clearError: () -> Unit
) {
    clearSaved()
    clearError()
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
