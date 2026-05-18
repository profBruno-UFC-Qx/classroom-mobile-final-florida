package com.example.florida.persistence.repository

import com.example.florida.domain.model.BudgetListItem
import com.example.florida.model.Budget
import com.example.florida.model.BudgetStatus
import com.example.florida.model.Client
import com.example.florida.model.Item
import com.example.florida.persistence.entity.BudgetEntity
import com.example.florida.persistence.entity.BudgetItemEntity
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.dao.BudgetDao
import com.example.florida.persistence.projection.BudgetListProjection
import com.example.florida.persistence.relations.BudgetWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class BudgetRepository(private val budgetDao: BudgetDao) {
    fun observeBudgets(): Flow<List<Budget>> {
        return budgetDao.observeBudgets().map { budgets ->
            budgets.map { it.toBudget() }
        }
    }

    fun observeBudgetListItems(): Flow<List<BudgetListItem>> {
        return budgetDao.observeBudgetListItems().map { budgets ->
            budgets.map { it.toBudgetListItem() }
        }
    }

    fun observeBudget(id: Long): Flow<Budget?> {
        return budgetDao.observeBudget(id).map { it?.toBudget() }
    }

    suspend fun getBudget(id: Long): Budget? = withContext(Dispatchers.IO) {
        budgetDao.getBudget(id)?.toBudget()
    }

    suspend fun saveBudget(
        clientId: Long?,
        notes: String?,
        validade: String?,
        entrega: String?,
        items: List<Item>,
    ): Long = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()
        val total = items.sumOf { it.total }
        budgetDao.insertBudgetWithItems(
            budget = BudgetEntity(
                clientId = clientId,
                notes = notes,
                validade = validade,
                entrega = entrega,
                createdAt = now,
                updateAt = now,
                total = total,
                status = BudgetStatus.DRAFT.name
            ),
            items = items.map {
                BudgetItemEntity(
                    budgetId = 0,
                    description = it.description,
                    qty = it.qty,
                    price = it.price
                )
            }
        )
    }

    suspend fun deleteBudget(id: Long) = withContext(Dispatchers.IO) {
        budgetDao.deleteBudget(id)
    }

    suspend fun updateBudget(
        budget: Budget,
        clientId: Long?,
        notes: String?,
        validade: String?,
        entrega: String?,
        items: List<Item>,
    ) = withContext(Dispatchers.IO) {
        val total = items.sumOf { it.total }
        budgetDao.updateBudgetWithItems(
            budget = BudgetEntity(
                id = budget.id,
                clientId = clientId,
                notes = notes,
                validade = validade,
                entrega = entrega,
                createdAt = budget.createdAt,
                updateAt = LocalDateTime.now(),
                total = total,
                status = budget.status.name
            ),
            items = items.map {
                BudgetItemEntity(
                    budgetId = budget.id,
                    description = it.description,
                    qty = it.qty,
                    price = it.price
                )
            }
        )
    }

    suspend fun updateStatus(id: Long, status: BudgetStatus) = withContext(Dispatchers.IO) {
        budgetDao.updateStatus(id, status.name)
    }

    private fun BudgetWithItems.toBudget(): Budget {
        return Budget(
            id = budget.id,
            client = client?.toClient(),
            clientId = budget.clientId,
            notes = budget.notes,
            validade = budget.validade,
            entrega = budget.entrega,
            createdAt = budget.createdAt,
            updateAt = budget.updateAt,
            total = budget.total,
            status = BudgetStatus.from(budget.status),
            items = items.map { it.toItem() }
        )
    }

    private fun BudgetListProjection.toBudgetListItem(): BudgetListItem {
        return BudgetListItem(
            id = id,
            clientId = clientId,
            clientName = clientName,
            createdAt = createdAt,
            total = total,
            status = BudgetStatus.from(status),
            itemCount = itemCount,
            linkedReceiptId = linkedReceiptId
        )
    }

    private fun BudgetItemEntity.toItem(): Item {
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
