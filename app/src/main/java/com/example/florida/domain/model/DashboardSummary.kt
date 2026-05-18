package com.example.florida.domain.model

import java.time.LocalDateTime

data class DashboardSummary(
    val clientCount: Int = 0,
    val budgetCount: Int = 0,
    val receiptCount: Int = 0,
    val totalBudgeted: Long = 0,
    val totalReceived: Long = 0,
    val monthReceived: Long = 0,
    val recentDocuments: List<RecentDocumentSummary> = emptyList(),
)

data class RecentDocumentSummary(
    val type: DocumentType,
    val documentId: Long,
    val clientName: String,
    val total: Long,
    val createdAt: LocalDateTime,
)

enum class DocumentType {
    BUDGET,
    RECEIPT
}
