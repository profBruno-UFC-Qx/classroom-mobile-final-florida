package com.example.florida.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.florida.R
import com.example.florida.model.UserSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SessionManager {
    var userSetup by mutableStateOf<UserSetup?>(null)

    fun saveUser(userSetup: UserSetup) {
        SessionManager.userSetup = userSetup
    }

    fun clearUser() {
        SessionManager.userSetup = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(modifier: Modifier = Modifier) {
    val user = SessionManager.userSetup

    if (user == null) {
        OnboardingScreen(modifier.background(MaterialTheme.colorScheme.background))
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            topBar = {
                TopAppBar(
                    title = { Text("HOME") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                BottomBar(
                    modifier = Modifier.padding(20.dp),
                    onLogout = { SessionManager.clearUser() }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Mostrar os dados da UserSetup
                UserProfileCard(user)
                Spacer(modifier = Modifier.height(16.dp))
                Dashboard()
            }
        }
    }
}

@Composable
fun UserProfileCard(user: UserSetup) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mostrar imagem do usuário se disponível
            user.imageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto do usuário",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 12.dp)
                )
            } ?: run {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Sem imagem", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Nome
            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Documento
            Text(
                text = "Documento: ${user.document}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val bitmapState = rememberBitmapFromUri(imageUri)

    Column(
        modifier = modifier
            .fillMaxSize()
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
            onValueChange = { name = it },
            label = { Text(stringResource(id = R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = document,
            onValueChange = { document = it },
            label = { Text(stringResource(id = R.string.document)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { launcher.launch("image/*") },
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

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && document.isNotBlank() && bitmapState.value != null) {
                    isLoading = true
                    val userSetup = UserSetup(
                        name = name,
                        document = document,
                        imageBitmap = bitmapState.value,
                        imageUri = imageUri
                    )
                    SessionManager.saveUser(userSetup)
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && name.isNotBlank() && document.isNotBlank() && bitmapState.value != null
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(id = R.string.continue_button))
            }
        }
    }
}

@Composable
fun BottomBar(modifier: Modifier = Modifier, onLogout: () -> Unit = {}) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "App v1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.inverseSurface
            )

            Button(
                onClick = onLogout,
                modifier = Modifier.size(width = 120.dp, height = 40.dp)
            ) {
                Text(stringResource(id = R.string.logout))
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