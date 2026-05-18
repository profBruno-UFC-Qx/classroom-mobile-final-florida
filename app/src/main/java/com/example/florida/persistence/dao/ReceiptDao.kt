package com.example.florida.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.florida.persistence.entity.ReceiptEntity
import com.example.florida.persistence.entity.ReceiptItemEntity
import com.example.florida.persistence.projection.ReceiptListProjection
import com.example.florida.persistence.relations.ReceiptWithItems
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ReceiptDao {
    @Transaction
    @Query("SELECT * FROM receipts ORDER BY date DESC")
    fun observeReceipts(): Flow<List<ReceiptWithItems>>

    @Query("""
        SELECT
            receipts.id AS id,
            receipts.clientId AS clientId,
            clients.name AS clientName,
            receipts.budgetId AS budgetId,
            receipts.total AS total,
            receipts.date AS date,
            COUNT(receipt_items.id) AS itemCount
        FROM receipts
        LEFT JOIN clients ON clients.id = receipts.clientId
        LEFT JOIN receipt_items ON receipt_items.receiptId = receipts.id
        GROUP BY receipts.id
        ORDER BY receipts.date DESC
    """)
    fun observeReceiptListItems(): Flow<List<ReceiptListProjection>>

    @Query("SELECT COUNT(*) FROM receipts")
    fun observeReceiptCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(total), 0) FROM receipts")
    fun observeTotalReceived(): Flow<Long>

    @Query("SELECT COALESCE(SUM(total), 0) FROM receipts WHERE date >= :start AND date < :end")
    fun observeReceivedBetween(start: LocalDateTime, end: LocalDateTime): Flow<Long>

    @Transaction
    @Query("SELECT * FROM receipts WHERE id = :id LIMIT 1")
    fun observeReceipt(id: Long): Flow<ReceiptWithItems?>

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

    @Transaction
    suspend fun updateReceiptWithItems(
        receipt: ReceiptEntity,
        items: List<ReceiptItemEntity>,
    ) {
        insertReceipt(receipt)
        deleteItemsForReceipt(receipt.id)
        insertItems(items.map { it.copy(receiptId = receipt.id) })
    }

    @Query("DELETE FROM receipt_items WHERE receiptId = :receiptId")
    suspend fun deleteItemsForReceipt(receiptId: Long)

    @Query("DELETE FROM receipts WHERE id = :id")
    suspend fun deleteReceipt(id: Long)
}
