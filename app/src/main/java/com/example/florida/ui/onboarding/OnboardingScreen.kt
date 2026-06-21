package com.example.florida.ui.onboarding

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
fun OnboardingScreen(
    onSaveUser: (UserSetup, Uri?) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val bitmapState = rememberBitmapFromUri(imageUri)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(id = R.string.initial_setup),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                errorMessage = null
            },
            label = { Text(stringResource(id = R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null && name.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = document,
            onValueChange = {
                document = it.normalizeCpfCnpjInput()
                errorMessage = null
            },
            label = { Text(stringResource(id = R.string.document)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = CpfCnpjVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = errorMessage != null && document.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it.normalizePhoneInput()
                errorMessage = null
            },
            label = { Text(stringResource(id = R.string.phone)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PhoneVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = street,
            onValueChange = {
                street = it
                errorMessage = null
            },
            label = { Text(stringResource(id = R.string.address)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text(stringResource(R.string.number)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = neighborhood,
            onValueChange = { neighborhood = it },
            label = { Text(stringResource(R.string.neighborhood)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(stringResource(R.string.city)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = state,
                onValueChange = { state = it.take(2).uppercase() },
                label = { Text(stringResource(R.string.state)) },
                modifier = Modifier.weight(0.4f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.select_image))
        }

        Spacer(modifier = Modifier.height(16.dp))

        bitmapState.value?.let { bitmap ->
            Card(
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = stringResource(id = R.string.image_selected),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } ?: run {
            Surface(
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(stringResource(id = R.string.no_image), style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val validation = FormValidators.validateIssuer(
                    name = name,
                    document = document,
                    phone = phone
                )
                if (!validation.isValid) {
                    errorMessage = context.getValidationMessage(validation.errors.first())
                    return@Button
                }
                onSaveUser(
                    UserSetup(
                        name = name,
                        document = document,
                        street = street,
                        number = number,
                        neighborhood = neighborhood,
                        city = city,
                        state = state,
                        phone = phone,
                        imagePath = null
                    ),
                    imageUri
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            Text(stringResource(id = R.string.continue_button))
        }
    }
}

@Composable
fun rememberBitmapFromUri(uri: Uri?): State<Bitmap?> {
    val context = LocalContext.current
    return produceState(initialValue = null, uri) {
        uri?.let {
            value = withContext(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
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
