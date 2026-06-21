package com.example.florida.ui.budget

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.florida.domain.model.BudgetListItem
import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.BudgetStatus
import com.example.florida.domain.model.Item
import com.example.florida.persistence.DatabaseProvider
import com.example.florida.persistence.repository.BudgetRepository
import com.example.florida.persistence.repository.ReceiptRepository
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
class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val receiptRepository: ReceiptRepository,
) : ViewModel() {
    val budgets: StateFlow<List<BudgetListItem>> = budgetRepository.observeBudgetListItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val selectedBudgetId = MutableStateFlow<Long?>(null)

    val selectedBudget: StateFlow<Budget?> = selectedBudgetId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else budgetRepository.observeBudget(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val actions = MutableSharedFlow<BudgetAction>(
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        actions.onEach(::handleAction).launchIn(viewModelScope)
    }

    fun selectBudget(id: Long) {
        selectedBudgetId.value = id
    }

    fun createBudget(
        clientId: Long?,
        notes: String?,
        validade: String?,
        entrega: String?,
        items: List<Item>,
        onSaved: () -> Unit,
    ) {
        actions.tryEmit(BudgetAction.Create(clientId, notes, validade, entrega, items, onSaved))
    }

    fun updateBudget(
        budget: Budget,
        clientId: Long?,
        notes: String?,
        validade: String?,
        entrega: String?,
        items: List<Item>,
        onSaved: () -> Unit,
    ) {
        actions.tryEmit(BudgetAction.Update(budget, clientId, notes, validade, entrega, items, onSaved))
    }

    fun updateStatus(id: Long, status: BudgetStatus) {
        actions.tryEmit(BudgetAction.UpdateStatus(id, status))
    }

    fun deleteBudget(id: Long) {
        actions.tryEmit(BudgetAction.Delete(id))
    }

    fun createReceiptFromBudget(id: Long, onSaved: () -> Unit) {
        actions.tryEmit(BudgetAction.CreateReceiptFromBudget(id, onSaved))
    }

    private suspend fun handleAction(action: BudgetAction) {
        when (action) {
            is BudgetAction.Create -> {
                budgetRepository.saveBudget(action.clientId, action.notes, action.validade, action.entrega, action.items)
                action.onSaved()
            }
            is BudgetAction.Update -> {
                budgetRepository.updateBudget(
                    budget = action.budget,
                    clientId = action.clientId,
                    notes = action.notes,
                    validade = action.validade,
                    entrega = action.entrega,
                    items = action.items
                )
                action.onSaved()
            }
            is BudgetAction.UpdateStatus -> budgetRepository.updateStatus(action.id, action.status)
            is BudgetAction.Delete -> budgetRepository.deleteBudget(action.id)
            is BudgetAction.CreateReceiptFromBudget -> {
                val budget = budgetRepository.getBudget(action.id) ?: return
                receiptRepository.saveReceipt(
                    clientId = budget.clientId,
                    items = budget.items,
                    budgetId = budget.id
                )
                action.onSaved()
            }
        }
    }

    private sealed interface BudgetAction {
        data class Create(
            val clientId: Long?,
            val notes: String?,
            val validade: String?,
            val entrega: String?,
            val items: List<Item>,
            val onSaved: () -> Unit,
        ) : BudgetAction
        data class Update(
            val budget: Budget,
            val clientId: Long?,
            val notes: String?,
            val validade: String?,
            val entrega: String?,
            val items: List<Item>,
            val onSaved: () -> Unit,
        ) : BudgetAction
        data class UpdateStatus(val id: Long, val status: BudgetStatus) : BudgetAction
        data class Delete(val id: Long) : BudgetAction
        data class CreateReceiptFromBudget(val id: Long, val onSaved: () -> Unit) : BudgetAction
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BudgetViewModel(
                        budgetRepository = DatabaseProvider.getBudgetRepository(appContext),
                        receiptRepository = DatabaseProvider.getReceiptRepository(appContext)
                    ) as T
                }
            }
        }
    }
}
