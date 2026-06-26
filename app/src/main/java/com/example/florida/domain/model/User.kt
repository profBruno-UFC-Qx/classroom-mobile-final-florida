package com.example.florida.domain.model

data class UserSetup(
    val name: String,
    val document: String,
    val street: String,
    val number: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val phone: String,
    val imagePath: String? = null,
) {
    val address: String
        get() = "$street, $number, $neighborhood, $city - $state"
}
