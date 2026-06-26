package com.example.florida.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserSetupDto(
    val id: Long,
    val name: String,
    val document: String,
    val street: String,
    val number: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val phone: String,
    val imagePath: String? = null,
)

@Serializable
data class UserSetupCreateDto(
    val name: String,
    val document: String,
    val street: String,
    val number: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val phone: String,
    val imagePath: String? = null,
)

@Serializable
data class UserSetupUpdateDto(
    val name: String? = null,
    val document: String? = null,
    val street: String? = null,
    val number: String? = null,
    val neighborhood: String? = null,
    val city: String? = null,
    val state: String? = null,
    val phone: String? = null,
    val imagePath: String? = null,
)
