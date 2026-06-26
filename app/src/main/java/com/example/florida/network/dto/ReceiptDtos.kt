package com.example.florida.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDto(
    val id: Long,
    val clientId: Long? = null,
    val budgetId: Long? = null,
    val total: Long = 0,
    val date: String,
    val createdAt: String,
    val items: List<ItemReadDto> = emptyList(),
)

@Serializable
data class ReceiptCreateDto(
    val clientId: Long? = null,
    val budgetId: Long? = null,
    val total: Long = 0,
    val date: String,
    val items: List<ItemCreateDto> = emptyList(),
)

@Serializable
data class ReceiptUpdateDto(
    val clientId: Long? = null,
    val budgetId: Long? = null,
    val total: Long? = null,
    val date: String? = null,
    val items: List<ItemCreateDto>? = null,
)

@Serializable
data class ReceiptListItemDto(
    val id: Long,
    val clientId: Long? = null,
    val clientName: String? = null,
    val budgetId: Long? = null,
    val total: Long,
    val date: String,
    val itemCount: Int,
)
