package com.example.florida.persistence.repository

import com.example.florida.domain.model.ReceiptListItem
import com.example.florida.domain.model.Item
import com.example.florida.domain.model.Receipt
import com.example.florida.network.FloridaRemoteRepository
import com.example.florida.persistence.dao.ReceiptDao
import com.example.florida.persistence.mapper.toDomain
import com.example.florida.persistence.mapper.toEntity
import com.example.florida.persistence.mapper.toReceiptItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ReceiptRepository(
    private val receiptDao: ReceiptDao,
    private val remoteRepository: FloridaRemoteRepository,
    private val syncRepository: SyncRepository,
) {
    fun observeReceipts(): Flow<List<Receipt>> {
        return receiptDao.observeReceipts().map { receipts ->
            receipts.map { it.toDomain() }
        }
    }

    fun observeReceiptListItems(): Flow<List<ReceiptListItem>> {
        return receiptDao.observeReceiptListItems().map { receipts ->
            receipts.map { it.toDomain() }
        }
    }

    fun observeReceipt(id: Long): Flow<Receipt?> {
        return receiptDao.observeReceipt(id).map { it?.toDomain() }
    }

    suspend fun getReceipt(id: Long): Receipt? = withContext(Dispatchers.IO) {
        receiptDao.getReceipt(id)?.toDomain()
    }

    suspend fun saveReceipt(
        clientId: Long?,
        items: List<Item>,
        budgetId: Long? = null,
    ): Long = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()
        val draft = Receipt(
            clientId = clientId,
            budgetId = budgetId,
            total = items.sumOf { it.total },
            date = now,
            createdAt = now,
            items = items
        )
        val localId = receiptDao.insertReceiptWithItems(
            receipt = draft.toEntity().copy(syncPending = true),
            items = draft.items.map { it.toReceiptItemEntity(0) }
        )
        runCatching { syncRepository.syncPendingChanges() }
        localId
    }

    suspend fun deleteReceipt(id: Long) = withContext(Dispatchers.IO) {
        val existingReceipt = receiptDao.getReceipt(id)?.receipt ?: return@withContext
        if (existingReceipt.remoteId == null) {
            receiptDao.deleteReceipt(id)
        } else {
            receiptDao.insertReceipt(existingReceipt.copy(syncPending = true, pendingDelete = true))
            runCatching { syncRepository.syncPendingChanges() }
        }
    }

    suspend fun updateReceipt(
        receipt: Receipt,
        clientId: Long?,
        items: List<Item>,
    ) = withContext(Dispatchers.IO) {
        val existingReceipt = receiptDao.getReceipt(receipt.id)?.receipt ?: return@withContext
        receiptDao.updateReceiptWithItems(
            receipt = receipt.copy(
                clientId = clientId,
                total = items.sumOf { it.total },
                items = items
            ).toEntity().copy(
                remoteId = existingReceipt.remoteId,
                syncPending = true,
                pendingDelete = false,
            ),
            items = items.map { it.toReceiptItemEntity(receipt.id) }
        )
        runCatching { syncRepository.syncPendingChanges() }
    }
}
