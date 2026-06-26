package com.example.florida.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
enum class BudgetStatusDto {
    DRAFT,
    SENT,
    APPROVED,
    REJECTED,
    EXPIRED,
}

@Serializable
enum class DocumentTypeDto {
    BUDGET,
    RECEIPT,
}

@Serializable
data class ItemCreateDto(
    val description: String,
    val qty: Int,
    val price: Long,
)

@Serializable
data class ItemReadDto(
    val id: Long,
    val description: String,
    val qty: Int,
    val price: Long,
)

@Serializable
data class ValidationErrorDto(
    val detail: List<ValidationDetailDto> = emptyList(),
)

@Serializable
data class ValidationDetailDto(
    val loc: List<JsonElement> = emptyList(),
    val msg: String,
    val type: String,
    val input: JsonElement? = null,
    val ctx: JsonObject? = null,
)
