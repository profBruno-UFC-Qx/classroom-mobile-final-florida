package com.example.florida.persistence.reposity

import com.example.florida.model.Client
import com.example.florida.model.Item
import com.example.florida.model.Receipt
import com.example.florida.persistence.Entity.ClientEntity
import com.example.florida.persistence.Entity.ReceiptEntity
import com.example.florida.persistence.Entity.ReceiptItemEntity
import com.example.florida.persistence.dao.ReceiptDao
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

    suspend fun saveReceipt(
        clientId: Long?,
        items: List<Item>,
    ): Long = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()
        val total = items.sumOf { it.total }
        receiptDao.insertReceiptWithItems(
            receipt = ReceiptEntity(
                clientId = clientId,
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

    private fun ReceiptWithItems.toReceipt(): Receipt {
        return Receipt(
            id = receipt.id,
            client = client?.toClient(),
            clientId = receipt.clientId,
            total = receipt.total,
            date = receipt.date,
            createdAt = receipt.createdAt,
            items = items.map { it.toItem() }
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
