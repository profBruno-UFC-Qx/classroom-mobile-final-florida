package com.example.florida.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class BudgetDto(
    val id: Long,
    val clientId: Long? = null,
    val notes: String? = null,
    val validade: String? = null,
    val entrega: String? = null,
    val total: Long = 0,
    val status: BudgetStatusDto = BudgetStatusDto.DRAFT,
    val createdAt: String,
    val updateAt: String,
    val items: List<ItemReadDto> = emptyList(),
)

@Serializable
data class BudgetCreateDto(
    val clientId: Long? = null,
    val notes: String? = null,
    val validade: String? = null,
    val entrega: String? = null,
    val total: Long = 0,
    val status: BudgetStatusDto = BudgetStatusDto.DRAFT,
    val items: List<ItemCreateDto> = emptyList(),
)

@Serializable
data class BudgetUpdateDto(
    val clientId: Long? = null,
    val notes: String? = null,
    val validade: String? = null,
    val entrega: String? = null,
    val total: Long? = null,
    val status: BudgetStatusDto? = null,
    val items: List<ItemCreateDto>? = null,
)

@Serializable
data class BudgetListItemDto(
    val id: Long,
    val clientId: Long? = null,
    val clientName: String? = null,
    val createdAt: String,
    val total: Long,
    val status: BudgetStatusDto,
    val itemCount: Int,
    val linkedReceiptId: Long? = null,
)
