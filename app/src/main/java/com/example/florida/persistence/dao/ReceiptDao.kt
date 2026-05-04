package com.example.florida.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.florida.persistence.Entity.ReceiptEntity
import com.example.florida.persistence.Entity.ReceiptItemEntity
import com.example.florida.persistence.relations.ReceiptWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Transaction
    @Query("SELECT * FROM receipts ORDER BY date DESC")
    fun observeReceipts(): Flow<List<ReceiptWithItems>>

    @Transaction
    @Query("SELECT * FROM receipts WHERE id = :id LIMIT 1")
    suspend fun getReceipt(id: Long): ReceiptWithItems?

    @Transaction
    @Query("SELECT * FROM receipts WHERE budgetId = :budgetId LIMIT 1")
    suspend fun getReceiptByBudgetId(budgetId: Long): ReceiptWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ReceiptItemEntity>)

    @Transaction
    suspend fun insertReceiptWithItems(
        receipt: ReceiptEntity,
        items: List<ReceiptItemEntity>,
    ): Long {
        receipt.budgetId?.let { budgetId ->
            val existing = getReceiptByBudgetId(budgetId)
            if (existing != null) return existing.receipt.id
        }
        val receiptId = insertReceipt(receipt)
        insertItems(items.map { it.copy(receiptId = receiptId) })
        return receiptId
    }

    @Query("DELETE FROM receipts WHERE id = :id")
    suspend fun deleteReceipt(id: Long)
}
