package com.example.florida.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.florida.persistence.Entity.BudgetEntity
import com.example.florida.persistence.Entity.BudgetItemEntity
import com.example.florida.persistence.relations.BudgetWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Transaction
    @Query("SELECT * FROM budgets ORDER BY createdAt DESC")
    fun observeBudgets(): Flow<List<BudgetWithItems>>

    @Transaction
    @Query("SELECT * FROM budgets WHERE id = :id LIMIT 1")
    suspend fun getBudget(id: Long): BudgetWithItems?

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
