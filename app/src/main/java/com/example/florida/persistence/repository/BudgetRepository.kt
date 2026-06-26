package com.example.florida.persistence.repository

import com.example.florida.domain.model.BudgetListItem
import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.BudgetStatus
import com.example.florida.domain.model.Item
import com.example.florida.network.FloridaRemoteRepository
import com.example.florida.persistence.dao.BudgetDao
import com.example.florida.persistence.mapper.toBudgetItemEntity
import com.example.florida.persistence.mapper.toDomain
import com.example.florida.persistence.mapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val remoteRepository: FloridaRemoteRepository,
    private val syncRepository: SyncRepository,
) {
    fun observeBudgets(): Flow<List<Budget>> {
        return budgetDao.observeBudgets().map { budgets ->
            budgets.map { it.toDomain() }
        }
    }

    fun observeBudgetListItems(): Flow<List<BudgetListItem>> {
        return budgetDao.observeBudgetListItems().map { budgets ->
            budgets.map { it.toDomain() }
        }
    }

    fun observeBudget(id: Long): Flow<Budget?> {
        return budgetDao.observeBudget(id).map { it?.toDomain() }
    }

    suspend fun getBudget(id: Long): Budget? = withContext(Dispatchers.IO) {
        budgetDao.getBudget(id)?.toDomain()
    }

    suspend fun saveBudget(
        clientId: Long?,
        notes: String?,
        validade: String?,
        entrega: String?,
        items: List<Item>,
    ): Long = withContext(Dispatchers.IO) {
        val now = LocalDateTime.now()
        val draft = Budget(
            clientId = clientId,
            notes = notes,
            validade = validade,
            entrega = entrega,
            createdAt = now,
            updateAt = now,
            total = items.sumOf { it.total },
            status = BudgetStatus.DRAFT,
            items = items
        )
        val localId = budgetDao.insertBudgetWithItems(
            budget = draft.toEntity().copy(syncPending = true),
            items = draft.items.map { it.toBudgetItemEntity(0) }
        )
        runCatching { syncRepository.syncPendingChanges() }
        localId
    }

    suspend fun deleteBudget(id: Long) = withContext(Dispatchers.IO) {
        val existingBudget = budgetDao.getBudget(id)?.budget ?: return@withContext
        if (existingBudget.remoteId == null) {
            budgetDao.deleteBudget(id)
        } else {
            budgetDao.insertBudget(existingBudget.copy(syncPending = true, pendingDelete = true))
            runCatching { syncRepository.syncPendingChanges() }
        }
    }

    suspend fun updateBudget(
        budget: Budget,
        clientId: Long?,
        notes: String?,
        validade: String?,
        entrega: String?,
        items: List<Item>,
    ) = withContext(Dispatchers.IO) {
        val existingBudget = budgetDao.getBudget(budget.id)?.budget ?: return@withContext
        budgetDao.updateBudgetWithItems(
            budget = budget.copy(
                clientId = clientId,
                notes = notes,
                validade = validade,
                entrega = entrega,
                updateAt = LocalDateTime.now(),
                total = items.sumOf { it.total },
                items = items
            ).toEntity().copy(
                remoteId = existingBudget.remoteId,
                syncPending = true,
                pendingDelete = false,
            ),
            items = items.map { it.toBudgetItemEntity(budget.id) }
        )
        runCatching { syncRepository.syncPendingChanges() }
    }

    suspend fun updateStatus(id: Long, status: BudgetStatus) = withContext(Dispatchers.IO) {
        val currentBudget = budgetDao.getBudget(id)?.toDomain() ?: return@withContext
        val currentEntity = budgetDao.getBudget(id)?.budget ?: return@withContext
        budgetDao.updateBudgetWithItems(
            budget = currentBudget.copy(status = status).toEntity().copy(
                remoteId = currentEntity.remoteId,
                syncPending = true,
                pendingDelete = false,
            ),
            items = currentBudget.items.map { it.toBudgetItemEntity(currentBudget.id) }
        )
        runCatching { syncRepository.syncPendingChanges() }
    }
}
