package com.example.florida.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClientDto(
    val id: Long,
    val name: String,
    val address: String,
    val document: String,
    val phone: String,
    val imagePath: String? = null,
    val deleted: Boolean = false,
)

@Serializable
data class ClientCreateDto(
    val name: String,
    val address: String,
    val document: String,
    val phone: String,
    val imagePath: String? = null,
    val deleted: Boolean = false,
)

@Serializable
data class ClientUpdateDto(
    val name: String? = null,
    val address: String? = null,
    val document: String? = null,
    val phone: String? = null,
    val imagePath: String? = null,
    val deleted: Boolean? = null,
)

@Serializable
data class ClientListItemDto(
    val id: Long,
    val name: String,
    val address: String,
    val document: String,
    val phone: String,
    val imagePath: String? = null,
)

@Serializable
data class ClientDocumentSummaryDto(
    val type: DocumentTypeDto,
    val documentId: Long,
    val date: String,
    val total: Long,
)
