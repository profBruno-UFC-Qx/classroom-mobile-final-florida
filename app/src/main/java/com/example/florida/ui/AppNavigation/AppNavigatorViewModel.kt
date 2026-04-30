package com.example.florida.ui.AppNavigation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.florida.model.Client
import com.example.florida.model.Item
import com.example.florida.persistence.DatabaseProvider
import com.example.florida.persistence.reposity.BudgetRepository
import com.example.florida.persistence.reposity.ClientRepository
import com.example.florida.persistence.reposity.ReceiptRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppNavigatorViewModel(
    private val clientRepository: ClientRepository,
    private val budgetRepository: BudgetRepository,
    private val receiptRepository: ReceiptRepository,
) : ViewModel() {
    val clients = clientRepository.observeClients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val budgets = budgetRepository.observeBudgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val receipts = receiptRepository.observeReceipts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createClient(
        name: String,
        document: String,
        phone: String,
        address: String,
        imagePath: String?,
    ) {
        viewModelScope.launch {
            clientRepository.saveClient(
                Client(
                    name = name,
                    address = address,
                    document = document,
                    phone = phone,
                    imagePath = imagePath
                )
            )
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            clientRepository.deleteClient(client)
        }
    }

    fun createBudget(
        clientId: Long?,
        notes: String?,
        validade: String?,
        entrega: String?,
        items: List<Item>,
        onSaved: () -> Unit,
    ) {
        viewModelScope.launch {
            budgetRepository.saveBudget(clientId, notes, validade, entrega, items)
            onSaved()
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(id)
        }
    }

    fun createReceipt(
        clientId: Long?,
        items: List<Item>,
        onSaved: () -> Unit,
    ) {
        viewModelScope.launch {
            receiptRepository.saveReceipt(clientId, items)
            onSaved()
        }
    }

    fun deleteReceipt(id: Long) {
        viewModelScope.launch {
            receiptRepository.deleteReceipt(id)
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AppNavigatorViewModel(
                        clientRepository = DatabaseProvider.getClientRepository(appContext),
                        budgetRepository = DatabaseProvider.getBudgetRepository(appContext),
                        receiptRepository = DatabaseProvider.getReceiptRepository(appContext),
                    ) as T
                }
            }
        }
    }
}
