package com.example.florida.persistence.repository

import com.example.florida.domain.model.ReceiptListItem
import com.example.florida.model.Client
import com.example.florida.model.Item
import com.example.florida.model.Receipt
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.entity.ReceiptEntity
import com.example.florida.persistence.entity.ReceiptItemEntity
import com.example.florida.persistence.dao.ReceiptDao
import com.example.florida.persistence.projection.ReceiptListProjection
import com.example.florida.persistence.relations.ReceiptWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ReceiptRepository(private val receiptDao: ReceiptDao) {
    fun observeReceipts(): Flow<List<Receipt>> {
        return receiptDao.observeReceipts().map { receipts ->
            receipts.map { it.toReceipt() }
        }
    }

    fun observeReceiptListItems(): Flow<List<ReceiptListItem>> {
        return receiptDao.observeReceiptListItems().map { receipts ->
            receipts.map { it.toReceiptListItem() }
        }
    }

    fun observeReceipt(id: Long): Flow<Receipt?> {
        return receiptDao.observeReceipt(id).map { it?.toReceipt() }
    }

    suspend fun getReceipt(id: Long): Receipt? = withContext(Dispatchers.IO) {
        receiptDao.getReceipt(id)?.toReceipt()
    }

    suspend fun saveReceipt(
        clientId: Long?,
        items: List<Item>,
        budgetId: Long? = null,
    ): Long = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()
        val total = items.sumOf { it.total }
        receiptDao.insertReceiptWithItems(
            receipt = ReceiptEntity(
                clientId = clientId,
                budgetId = budgetId,
                total = total,
                date = now,
                createdAt = now
            ),
            items = items.map {
                ReceiptItemEntity(
                    receiptId = 0,
                    description = it.description,
                    qty = it.qty,
                    price = it.price
                )
            }
        )
    }

    suspend fun deleteReceipt(id: Long) = withContext(Dispatchers.IO) {
        receiptDao.deleteReceipt(id)
    }

    suspend fun updateReceipt(
        receipt: Receipt,
        clientId: Long?,
        items: List<Item>,
    ) = withContext(Dispatchers.IO) {
        val total = items.sumOf { it.total }
        receiptDao.updateReceiptWithItems(
            receipt = ReceiptEntity(
                id = receipt.id,
                clientId = clientId,
                budgetId = receipt.budgetId,
                total = total,
                date = receipt.date,
                createdAt = receipt.createdAt
            ),
            items = items.map {
                ReceiptItemEntity(
                    receiptId = receipt.id,
                    description = it.description,
                    qty = it.qty,
                    price = it.price
                )
            }
        )
    }

    private fun ReceiptWithItems.toReceipt(): Receipt {
        return Receipt(
            id = receipt.id,
            client = client?.toClient(),
            clientId = receipt.clientId,
            budgetId = receipt.budgetId,
            total = receipt.total,
            date = receipt.date,
            createdAt = receipt.createdAt,
            items = items.map { it.toItem() }
        )
    }

    private fun ReceiptListProjection.toReceiptListItem(): ReceiptListItem {
        return ReceiptListItem(
            id = id,
            clientId = clientId,
            clientName = clientName,
            budgetId = budgetId,
            total = total,
            date = date,
            itemCount = itemCount
        )
    }

    private fun ReceiptItemEntity.toItem(): Item {
        return Item(id = id, description = description, qty = qty, price = price)
    }

    private fun ClientEntity.toClient(): Client {
        return Client(
            id = id,
            name = name,
            address = address,
            document = document,
            phone = phone,
            imagePath = imagePath,
            deleted = deleted
        )
    }
}
