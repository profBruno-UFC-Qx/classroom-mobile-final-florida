package com.example.florida.persistence.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.Receipt
import com.example.florida.network.FloridaRemoteRepository
import com.example.florida.persistence.AppDatabase
import com.example.florida.persistence.dao.BudgetDao
import com.example.florida.persistence.dao.ClientDao
import com.example.florida.persistence.dao.ReceiptDao
import com.example.florida.persistence.dao.UserDao
import com.example.florida.persistence.entity.BudgetEntity
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.entity.ReceiptEntity
import com.example.florida.persistence.relations.BudgetWithItems
import com.example.florida.persistence.relations.ReceiptWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class SyncRepository(
    private val database: AppDatabase,
    private val userDao: UserDao,
    private val clientDao: ClientDao,
    private val budgetDao: BudgetDao,
    private val receiptDao: ReceiptDao,
    private val remoteRepository: FloridaRemoteRepository,
) {
    companion object {
        private const val TAG = "SyncRepository"
    }

    private val syncMutex = Mutex()

    suspend fun syncPendingChanges() = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            // sincronizando por etapa
            syncClients()
            syncBudgets()
            syncReceipts()
        }
    }

    private suspend fun syncClients() {
        clientDao.getPendingSyncClients().forEach { client ->
            runCatching {
                when {
                    client.deleted && client.remoteId != null -> {
                        remoteRepository.deleteClient(client.remoteId)
                        clientDao.deleteById(client.id)
                    }
                    client.deleted -> clientDao.deleteById(client.id)
                    client.remoteId == null -> {
                        val created = remoteRepository.createClient(client.toRemoteCreate())
                        clientDao.insert(client.copy(remoteId = created.id, syncPending = false))
                    }
                    else -> {
                        remoteRepository.updateClient(client.toRemoteUpdate())
                        clientDao.insert(client.copy(syncPending = false))
                    }
                }
            }.onFailure { throwable ->
                Log.e(
                    TAG,
                    "Failed to sync client localId=${client.id} remoteId=${client.remoteId}",
                    throwable
                )
            }
        }
    }

    private suspend fun syncBudgets() {
        budgetDao.getPendingSyncBudgets().forEach { budgetWithItems ->
            runCatching {
                val budget = budgetWithItems.budget
                val remoteClientId = budget.clientId?.let { clientDao.getClient(it)?.remoteId }
                if (budget.clientId != null && remoteClientId == null) return@runCatching

                when {
                    budget.pendingDelete && budget.remoteId != null -> {
                        remoteRepository.deleteBudget(budget.remoteId)
                        budgetDao.deleteBudget(budget.id)
                    }
                    budget.pendingDelete -> budgetDao.deleteBudget(budget.id)
                    budget.remoteId == null -> {
                        val created = remoteRepository.createBudget(
                            budgetWithItems.toRemoteBudget(remoteClientId, null)
                        )
                        budgetDao.insertBudget(budget.copy(remoteId = created.id, syncPending = false))
                    }
                    else -> {
                        remoteRepository.updateBudget(
                            budgetWithItems.toRemoteBudget(remoteClientId, budget.remoteId)
                        )
                        budgetDao.insertBudget(budget.copy(syncPending = false))
                    }
                }
            }.onFailure { throwable ->
                Log.e(
                    TAG,
                    "Failed to sync budget localId=${budgetWithItems.budget.id} remoteId=${budgetWithItems.budget.remoteId}",
                    throwable
                )
            }
        }
    }

    private suspend fun syncReceipts() {
        receiptDao.getPendingSyncReceipts().forEach { receiptWithItems ->
            runCatching {
                val receipt = receiptWithItems.receipt
                val remoteClientId = receipt.clientId?.let { clientDao.getClient(it)?.remoteId }
                val remoteBudgetId = receipt.budgetId?.let { budgetDao.getBudget(it)?.budget?.remoteId }

                if (receipt.clientId != null && remoteClientId == null) return@runCatching
                if (receipt.budgetId != null && remoteBudgetId == null) return@runCatching

                when {
                    receipt.pendingDelete && receipt.remoteId != null -> {
                        remoteRepository.deleteReceipt(receipt.remoteId)
                        receiptDao.deleteReceipt(receipt.id)
                    }
                    receipt.pendingDelete -> receiptDao.deleteReceipt(receipt.id)
                    receipt.remoteId == null -> {
                        val created = remoteRepository.createReceipt(
                            receiptWithItems.toRemoteReceipt(remoteClientId, remoteBudgetId, null)
                        )
                        receiptDao.insertReceipt(receipt.copy(remoteId = created.id, syncPending = false))
                    }
                    else -> {
                        remoteRepository.updateReceipt(
                            receiptWithItems.toRemoteReceipt(remoteClientId, remoteBudgetId, receipt.remoteId)
                        )
                        receiptDao.insertReceipt(receipt.copy(syncPending = false))
                    }
                }
            }.onFailure { throwable ->
                Log.e(
                    TAG,
                    "Failed to sync receipt localId=${receiptWithItems.receipt.id} remoteId=${receiptWithItems.receipt.remoteId}",
                    throwable
                )
            }
        }
    }
}

private fun ClientEntity.toRemoteCreate(): Client {
    return Client(
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = null,
        deleted = false
    )
}

private fun ClientEntity.toRemoteUpdate(): Client {
    return Client(
        id = remoteId ?: 0,
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = null,
        deleted = deleted
    )
}

private fun BudgetWithItems.toRemoteBudget(remoteClientId: Long?, remoteBudgetId: Long?): Budget {
    return Budget(
        id = remoteBudgetId ?: 0,
        clientId = remoteClientId,
        notes = budget.notes,
        validade = budget.validade,
        entrega = budget.entrega,
        createdAt = budget.createdAt,
        updateAt = budget.updateAt,
        total = budget.total,
        status = com.example.florida.domain.model.BudgetStatus.from(budget.status),
        items = items.map {
            com.example.florida.domain.model.Item(
                description = it.description,
                qty = it.qty,
                price = it.price
            )
        }
    )
}

private fun ReceiptWithItems.toRemoteReceipt(
    remoteClientId: Long?,
    remoteBudgetId: Long?,
    remoteReceiptId: Long?,
): Receipt {
    return Receipt(
        id = remoteReceiptId ?: 0,
        clientId = remoteClientId,
        budgetId = remoteBudgetId,
        total = receipt.total,
        date = receipt.date,
        createdAt = receipt.createdAt,
        items = items.map {
            com.example.florida.domain.model.Item(
                description = it.description,
                qty = it.qty,
                price = it.price
            )
        }
    )
}
