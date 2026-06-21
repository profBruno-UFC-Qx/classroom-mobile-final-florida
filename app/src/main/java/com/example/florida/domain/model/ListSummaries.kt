package com.example.florida.domain.model

import java.time.LocalDateTime

data class ClientListItem(
    val id: Long,
    val name: String,
    val address: String,
    val document: String,
    val phone: String,
    val imagePath: String?,
)

data class BudgetListItem(
    val id: Long,
    val clientId: Long?,
    val clientName: String?,
    val createdAt: LocalDateTime,
    val total: Long,
    val status: BudgetStatus,
    val itemCount: Int,
    val linkedReceiptId: Long?,
)

data class ReceiptListItem(
    val id: Long,
    val clientId: Long?,
    val clientName: String?,
    val budgetId: Long?,
    val total: Long,
    val date: LocalDateTime,
    val itemCount: Int,
)

data class ClientDocumentSummary(
    val type: DocumentType,
    val documentId: Long,
    val date: LocalDateTime,
    val total: Long,
)
