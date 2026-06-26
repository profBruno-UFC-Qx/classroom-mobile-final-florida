package com.example.florida.network

import com.example.florida.network.dto.BudgetCreateDto
import com.example.florida.network.dto.BudgetDto
import com.example.florida.network.dto.BudgetListItemDto
import com.example.florida.network.dto.BudgetStatusDto
import com.example.florida.network.dto.BudgetUpdateDto
import com.example.florida.network.dto.ClientCreateDto
import com.example.florida.network.dto.ClientDocumentSummaryDto
import com.example.florida.network.dto.ClientDto
import com.example.florida.network.dto.ClientListItemDto
import com.example.florida.network.dto.ClientUpdateDto
import com.example.florida.network.dto.DashboardSummaryDto
import com.example.florida.network.dto.ReceiptCreateDto
import com.example.florida.network.dto.ReceiptDto
import com.example.florida.network.dto.ReceiptListItemDto
import com.example.florida.network.dto.ReceiptUpdateDto
import com.example.florida.network.dto.SyncPayloadDto
import com.example.florida.network.dto.UserSetupDto
import com.example.florida.network.dto.UserSetupUpdateDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class FloridaApi(
    private val httpClient: HttpClient,
) {
    suspend fun listClients(): List<ClientDto> = httpClient.get("/clients/").body()

    suspend fun createClient(payload: ClientCreateDto): ClientDto {
        return httpClient.post("/clients/") {
            setBody(payload)
        }.body()
    }

    suspend fun listClientItems(): List<ClientListItemDto> {
        return httpClient.get("/clients/list-items").body()
    }

    suspend fun getClient(clientId: Long): ClientDto {
        return httpClient.get("/clients/$clientId").body()
    }

    suspend fun updateClient(clientId: Long, payload: ClientUpdateDto): ClientDto {
        return httpClient.patch("/clients/$clientId") {
            setBody(payload)
        }.body()
    }

    suspend fun deleteClient(clientId: Long): ClientDto {
        return httpClient.delete("/clients/$clientId").body()
    }

    suspend fun listClientDocuments(clientId: Long): List<ClientDocumentSummaryDto> {
        return httpClient.get("/clients/$clientId/documents").body()
    }

    suspend fun listBudgets(): List<BudgetDto> = httpClient.get("/budgets/").body()

    suspend fun createBudget(payload: BudgetCreateDto): BudgetDto {
        return httpClient.post("/budgets/") {
            setBody(payload)
        }.body()
    }

    suspend fun listBudgetItems(): List<BudgetListItemDto> {
        return httpClient.get("/budgets/list-items").body()
    }

    suspend fun getBudget(budgetId: Long): BudgetDto {
        return httpClient.get("/budgets/$budgetId").body()
    }

    suspend fun updateBudget(budgetId: Long, payload: BudgetUpdateDto): BudgetDto {
        return httpClient.patch("/budgets/$budgetId") {
            setBody(payload)
        }.body()
    }

    suspend fun deleteBudget(budgetId: Long) {
        httpClient.delete("/budgets/$budgetId")
    }

    suspend fun updateBudgetStatus(
        budgetId: Long,
        budgetStatus: BudgetStatusDto,
    ): BudgetDto {
        return httpClient.patch("/budgets/$budgetId/status") {
            parameter("budget_status", budgetStatus.name)
        }.body()
    }

    suspend fun listReceipts(): List<ReceiptDto> = httpClient.get("/receipts/").body()

    suspend fun createReceipt(payload: ReceiptCreateDto): ReceiptDto {
        return httpClient.post("/receipts/") {
            setBody(payload)
        }.body()
    }

    suspend fun listReceiptItems(): List<ReceiptListItemDto> {
        return httpClient.get("/receipts/list-items").body()
    }

    suspend fun getReceipt(receiptId: Long): ReceiptDto {
        return httpClient.get("/receipts/$receiptId").body()
    }

    suspend fun updateReceipt(receiptId: Long, payload: ReceiptUpdateDto): ReceiptDto {
        return httpClient.patch("/receipts/$receiptId") {
            setBody(payload)
        }.body()
    }

    suspend fun deleteReceipt(receiptId: Long) {
        httpClient.delete("/receipts/$receiptId")
    }

    suspend fun getUserSetup(): UserSetupDto = httpClient.get("/user-setup/").body()

    suspend fun updateUserSetup(payload: UserSetupUpdateDto): UserSetupDto {
        return httpClient.patch("/user-setup/") {
            setBody(payload)
        }.body()
    }

    suspend fun getDashboardSummary(): DashboardSummaryDto {
        return httpClient.get("/dashboard/").body()
    }

    suspend fun getSyncPayload(): SyncPayloadDto = httpClient.get("/sync/").body()
}
