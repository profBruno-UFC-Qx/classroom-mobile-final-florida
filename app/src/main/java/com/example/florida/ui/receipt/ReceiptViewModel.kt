package com.example.florida.ui.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.florida.domain.model.ReceiptListItem
import com.example.florida.domain.model.Item
import com.example.florida.domain.model.Receipt
import com.example.florida.persistence.repository.ReceiptRepository
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
class ReceiptViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository
) : ViewModel() {
    val receipts: StateFlow<List<ReceiptListItem>> = receiptRepository.observeReceiptListItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val selectedReceiptId = MutableStateFlow<Long?>(null)

    val selectedReceipt: StateFlow<Receipt?> = selectedReceiptId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else receiptRepository.observeReceipt(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val actions = MutableSharedFlow<ReceiptAction>(
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        actions.onEach(::handleAction).launchIn(viewModelScope)
    }

    fun selectReceipt(id: Long) {
        selectedReceiptId.value = id
    }

    fun createReceipt(
        clientId: Long?,
        items: List<Item>,
        budgetId: Long? = null,
        onSaved: () -> Unit,
    ) {
        actions.tryEmit(ReceiptAction.Create(clientId, items, budgetId, onSaved))
    }

    fun updateReceipt(
        receipt: Receipt,
        clientId: Long?,
        items: List<Item>,
        onSaved: () -> Unit,
    ) {
        actions.tryEmit(ReceiptAction.Update(receipt, clientId, items, onSaved))
    }

    fun deleteReceipt(id: Long) {
        actions.tryEmit(ReceiptAction.Delete(id))
    }

    private suspend fun handleAction(action: ReceiptAction) {
        when (action) {
            is ReceiptAction.Create -> {
                receiptRepository.saveReceipt(action.clientId, action.items, action.budgetId)
                action.onSaved()
            }
            is ReceiptAction.Update -> {
                receiptRepository.updateReceipt(action.receipt, action.clientId, action.items)
                action.onSaved()
            }
            is ReceiptAction.Delete -> receiptRepository.deleteReceipt(action.id)
        }
    }

    private sealed interface ReceiptAction {
        data class Create(
            val clientId: Long?,
            val items: List<Item>,
            val budgetId: Long?,
            val onSaved: () -> Unit,
        ) : ReceiptAction
        data class Update(
            val receipt: Receipt,
            val clientId: Long?,
            val items: List<Item>,
            val onSaved: () -> Unit,
        ) : ReceiptAction
        data class Delete(val id: Long) : ReceiptAction
    }
}
