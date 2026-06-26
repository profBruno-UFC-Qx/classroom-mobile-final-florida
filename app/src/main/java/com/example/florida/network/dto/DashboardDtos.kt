package com.example.florida.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardSummaryDto(
    val clientCount: Int = 0,
    val budgetCount: Int = 0,
    val receiptCount: Int = 0,
    val totalBudgeted: Long = 0,
    val totalReceived: Long = 0,
    val monthReceived: Long = 0,
    val recentDocuments: List<RecentDocumentSummaryDto> = emptyList(),
)

@Serializable
data class RecentDocumentSummaryDto(
    val type: DocumentTypeDto,
    val documentId: Long,
    val clientName: String,
    val total: Long,
    val createdAt: String,
)
