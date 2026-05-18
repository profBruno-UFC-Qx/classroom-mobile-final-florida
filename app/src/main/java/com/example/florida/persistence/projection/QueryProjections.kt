package com.example.florida.persistence.projection

import java.time.LocalDateTime

data class BudgetListProjection(
    val id: Long,
    val clientId: Long?,
    val clientName: String?,
    val createdAt: LocalDateTime,
    val total: Long,
    val status: String,
    val itemCount: Int,
    val linkedReceiptId: Long?,
)

data class ReceiptListProjection(
    val id: Long,
    val clientId: Long?,
    val clientName: String?,
    val budgetId: Long?,
    val total: Long,
    val date: LocalDateTime,
    val itemCount: Int,
)

data class RecentDocumentProjection(
    val type: String,
    val documentId: Long,
    val clientName: String?,
    val total: Long,
    val createdAt: LocalDateTime,
)

data class ClientDocumentProjection(
    val type: String,
    val documentId: Long,
    val date: LocalDateTime,
    val total: Long,
)
