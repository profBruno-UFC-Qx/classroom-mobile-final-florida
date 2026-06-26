package com.example.florida.ui.client

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.florida.domain.model.ClientDocumentSummary
import com.example.florida.domain.model.ClientListItem
import com.example.florida.domain.model.Client
import com.example.florida.persistence.ImageStorageService
import com.example.florida.persistence.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val imageStorageService: ImageStorageService,
) : ViewModel() {
    val clients: StateFlow<List<ClientListItem>> = clientRepository.observeClientListItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val selectedClientId = MutableStateFlow<Long?>(null)

    val selectedClient: StateFlow<Client?> = selectedClientId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else clientRepository.observeClient(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val selectedClientDocuments: StateFlow<List<ClientDocumentSummary>> = selectedClientId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else clientRepository.observeClientDocuments(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val actions = MutableSharedFlow<ClientAction>(
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        actions
            .onEach { action ->
                runCatching { handleAction(action) }
            }
            .launchIn(viewModelScope)
    }

    fun selectClient(id: Long) {
        selectedClientId.value = id
    }

    fun saveClient(client: Client) {
        actions.tryEmit(ClientAction.Save(client))
    }

    fun saveClientForm(
        editingClient: Client?,
        name: String,
        document: String,
        phone: String,
        address: String,
        imageUri: Uri?,
        onSaved: () -> Unit,
    ) {
        actions.tryEmit(
            ClientAction.SaveForm(
                editingClient = editingClient,
                name = name,
                document = document,
                phone = phone,
                address = address,
                imageUri = imageUri,
                onSaved = onSaved
            )
        )
    }

    fun deleteClient(client: Client) {
        actions.tryEmit(ClientAction.Delete(client))
    }

    private suspend fun handleAction(action: ClientAction) {
        when (action) {
            is ClientAction.Save -> clientRepository.saveClient(action.client)
            is ClientAction.Delete -> clientRepository.deleteClient(action.client)
            is ClientAction.SaveForm -> {
                val imagePath = action.imageUri?.let { imageStorageService.saveImage(it) }
                    ?: action.editingClient?.imagePath
                val client = action.editingClient?.copy(
                    name = action.name,
                    document = action.document,
                    phone = action.phone,
                    address = action.address,
                    imagePath = imagePath
                ) ?: Client(
                    name = action.name,
                    document = action.document,
                    phone = action.phone,
                    address = action.address,
                    imagePath = imagePath
                )
                clientRepository.saveClient(client)
                action.onSaved()
            }
        }
    }

    private sealed interface ClientAction {
        data class Save(val client: Client) : ClientAction
        data class Delete(val client: Client) : ClientAction
        data class SaveForm(
            val editingClient: Client?,
            val name: String,
            val document: String,
            val phone: String,
            val address: String,
            val imageUri: Uri?,
            val onSaved: () -> Unit,
        ) : ClientAction
    }
}
