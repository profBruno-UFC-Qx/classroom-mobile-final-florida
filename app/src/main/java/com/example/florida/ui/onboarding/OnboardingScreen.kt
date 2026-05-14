package com.example.florida.ui.onboarding

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.model.SessionManager
import com.example.florida.model.UserSetup
import com.example.florida.persistence.ImageStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val bitmapState = rememberBitmapFromUri(imageUri)
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(id = R.string.initial_setup),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.inverseSurface
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
                document = it
                errorMessage = null
            },
            label = { Text(stringResource(id = R.string.document)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null && document.isBlank()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.inverseOnSurface),
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
            onClick = {  // ToDO refatorar para remover logica dA UI.
                if (name.isBlank()){
                    errorMessage = "Nome é obrigatório"
                    return@Button
                }
                if (document.isBlank()){
                    errorMessage = "Documento é obrigatório"
                    return@Button
                }
                isLoading = true
                scope.launch(Dispatchers.Default) {
                    try {
                        val imagePath = imageUri?.let { uri ->
                            withContext(Dispatchers.IO) {
                                ImageStorage.saveImage(context, uri)
                            }
                        }
                        val userSetup = UserSetup(
                            name = name,
                            document = document,
                            imagePath = imagePath
                        )
                        // SessionManager.saveUser já persiste no banco
                        SessionManager.saveUser(userSetup)

                    } catch (e: Exception) {
                        errorMessage = "Erro ao salvar: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.onSecondary),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.inverseOnSurface),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onSecondary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(id = R.string.continue_button))
            }
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
