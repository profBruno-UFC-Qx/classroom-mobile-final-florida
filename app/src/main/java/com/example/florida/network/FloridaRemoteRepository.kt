package com.example.florida.network

import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.BudgetListItem
import com.example.florida.domain.model.BudgetStatus
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.ClientDocumentSummary
import com.example.florida.domain.model.ClientListItem
import com.example.florida.domain.model.DashboardSummary
import com.example.florida.domain.model.Receipt
import com.example.florida.domain.model.ReceiptListItem
import com.example.florida.domain.model.UserSetup
import com.example.florida.network.dto.SyncPayloadDto
import com.example.florida.network.mapper.toCreateDto
import com.example.florida.network.mapper.toDomain
import com.example.florida.network.mapper.toDto
import com.example.florida.network.mapper.toUpdateDto

class FloridaRemoteRepository(
    private val api: FloridaApi,
) {
    suspend fun getSyncPayload(): SyncPayloadDto = api.getSyncPayload()

    suspend fun listClients(): List<Client> = api.listClients().map { it.toDomain() }

    suspend fun listClientItems(): List<ClientListItem> = api.listClientItems().map { it.toDomain() }

    suspend fun getClient(clientId: Long): Client = api.getClient(clientId).toDomain()

    suspend fun createClient(client: Client): Client = api.createClient(client.toCreateDto()).toDomain()

    suspend fun updateClient(client: Client): Client = api.updateClient(client.id, client.toUpdateDto()).toDomain()

    suspend fun deleteClient(clientId: Long): Client = api.deleteClient(clientId).toDomain()

    suspend fun listClientDocuments(clientId: Long): List<ClientDocumentSummary> {
        return api.listClientDocuments(clientId).map { it.toDomain() }
    }

    suspend fun listBudgets(): List<Budget> = api.listBudgets().map { it.toDomain() }

    suspend fun listBudgetItems(): List<BudgetListItem> = api.listBudgetItems().map { it.toDomain() }

    suspend fun getBudget(budgetId: Long): Budget = api.getBudget(budgetId).toDomain()

    suspend fun createBudget(budget: Budget): Budget = api.createBudget(budget.toCreateDto()).toDomain()

    suspend fun updateBudget(budget: Budget): Budget = api.updateBudget(budget.id, budget.toUpdateDto()).toDomain()

    suspend fun deleteBudget(budgetId: Long) {
        api.deleteBudget(budgetId)
    }

    suspend fun updateBudgetStatus(budgetId: Long, status: BudgetStatus): Budget {
        return api.updateBudgetStatus(budgetId, status.toDto()).toDomain()
    }

    suspend fun listReceipts(): List<Receipt> = api.listReceipts().map { it.toDomain() }

    suspend fun listReceiptItems(): List<ReceiptListItem> = api.listReceiptItems().map { it.toDomain() }

    suspend fun getReceipt(receiptId: Long): Receipt = api.getReceipt(receiptId).toDomain()

    suspend fun createReceipt(receipt: Receipt): Receipt = api.createReceipt(receipt.toCreateDto()).toDomain()

    suspend fun updateReceipt(receipt: Receipt): Receipt = api.updateReceipt(receipt.id, receipt.toUpdateDto()).toDomain()

    suspend fun deleteReceipt(receiptId: Long) {
        api.deleteReceipt(receiptId)
    }

    suspend fun getUserSetup(): UserSetup = api.getUserSetup().toDomain()

    suspend fun saveUserSetup(userSetup: UserSetup): UserSetup {
        return api.saveUserSetup(userSetup.toCreateDto()).toDomain()
    }

    suspend fun getDashboardSummary(): DashboardSummary = api.getDashboardSummary().toDomain()
}
