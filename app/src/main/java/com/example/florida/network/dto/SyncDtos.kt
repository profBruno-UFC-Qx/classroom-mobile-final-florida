package com.example.florida.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SyncPayloadDto(
    val userSetup: UserSetupDto? = null,
    val clients: List<ClientDto> = emptyList(),
    val budgets: List<BudgetDto> = emptyList(),
    val receipts: List<ReceiptDto> = emptyList(),
)
