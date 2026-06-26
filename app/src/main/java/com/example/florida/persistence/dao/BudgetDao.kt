package com.example.florida.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.florida.persistence.entity.BudgetEntity
import com.example.florida.persistence.entity.BudgetItemEntity
import com.example.florida.persistence.projection.BudgetListProjection
import com.example.florida.persistence.relations.BudgetWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Transaction
    @Query("SELECT * FROM budgets WHERE pendingDelete = 0 ORDER BY createdAt DESC")
    fun observeBudgets(): Flow<List<BudgetWithItems>>

    @Query("""
        SELECT
            budgets.id AS id,
            budgets.clientId AS clientId,
            clients.name AS clientName,
            budgets.createdAt AS createdAt,
            budgets.total AS total,
            budgets.status AS status,
            COUNT(budget_items.id) AS itemCount,
            receipts.id AS linkedReceiptId
        FROM budgets
        LEFT JOIN clients ON clients.id = budgets.clientId
        LEFT JOIN budget_items ON budget_items.budgetId = budgets.id
        LEFT JOIN receipts ON receipts.budgetId = budgets.id
        WHERE budgets.pendingDelete = 0
        GROUP BY budgets.id
        ORDER BY budgets.createdAt DESC
    """)
    fun observeBudgetListItems(): Flow<List<BudgetListProjection>>

    @Query("SELECT COUNT(*) FROM budgets WHERE pendingDelete = 0")
    fun observeBudgetCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(total), 0) FROM budgets WHERE pendingDelete = 0")
    fun observeTotalBudgeted(): Flow<Long>

    @Transaction
    @Query("SELECT * FROM budgets WHERE id = :id LIMIT 1")
    fun observeBudget(id: Long): Flow<BudgetWithItems?>

    @Transaction
    @Query("SELECT * FROM budgets WHERE id = :id LIMIT 1")
    suspend fun getBudget(id: Long): BudgetWithItems?

    @Transaction
    @Query("SELECT * FROM budgets WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getBudgetByRemoteId(remoteId: Long): BudgetWithItems?

    @Transaction
    @Query("SELECT * FROM budgets")
    suspend fun getAllBudgets(): List<BudgetWithItems>

    @Transaction
    @Query("SELECT * FROM budgets WHERE syncPending = 1 ORDER BY createdAt ASC")
    suspend fun getPendingSyncBudgets(): List<BudgetWithItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<BudgetItemEntity>)

    @Transaction
    suspend fun insertBudgetWithItems(
        budget: BudgetEntity,
        items: List<BudgetItemEntity>,
    ): Long {
        val budgetId = insertBudget(budget)
        insertItems(items.map { it.copy(budgetId = budgetId) })
        return budgetId
    }

    @Transaction
    suspend fun updateBudgetWithItems(
        budget: BudgetEntity,
        items: List<BudgetItemEntity>,
    ) {
        insertBudget(budget)
        deleteItemsForBudget(budget.id)
        insertItems(items.map { it.copy(budgetId = budget.id) })
    }

    @Query("DELETE FROM budget_items WHERE budgetId = :budgetId")
    suspend fun deleteItemsForBudget(budgetId: Long)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudget(id: Long)

    @Query("UPDATE budgets SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
