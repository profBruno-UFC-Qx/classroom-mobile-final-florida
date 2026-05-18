package com.example.florida.ui.client

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.florida.domain.model.ClientDocumentSummary
import com.example.florida.domain.model.ClientListItem
import com.example.florida.model.Client
import com.example.florida.persistence.DatabaseProvider
import com.example.florida.persistence.repository.ClientRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class ClientViewModel(
    private val clientRepository: ClientRepository
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
        actions.onEach(::handleAction).launchIn(viewModelScope)
    }

    fun selectClient(id: Long) {
        selectedClientId.value = id
    }

    fun saveClient(client: Client) {
        actions.tryEmit(ClientAction.Save(client))
    }

    fun deleteClient(client: Client) {
        actions.tryEmit(ClientAction.Delete(client))
    }

    private suspend fun handleAction(action: ClientAction) {
        when (action) {
            is ClientAction.Save -> clientRepository.saveClient(action.client)
            is ClientAction.Delete -> clientRepository.deleteClient(action.client)
        }
    }

    private sealed interface ClientAction {
        data class Save(val client: Client) : ClientAction
        data class Delete(val client: Client) : ClientAction
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ClientViewModel(DatabaseProvider.getClientRepository(appContext)) as T
                }
            }
        }
    }
}
