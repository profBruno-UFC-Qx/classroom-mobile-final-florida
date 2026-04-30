package com.example.florida.model

data class UserSetup(
    val name: String = "Francisco",
    val document: String = "06364254307",
    val street: String = "Rua dos Bobos",
    val number: String = "0",
    val neighborhood: String = "Bairro dos Bobos",
    val city: String = "Cidade dos Bobos",
    val state: String = "SP",
    val phone: String = "11999999999",
    val imagePath: String? = null,
) {
    val address: String
        get() = "$street, $number, $neighborhood, $city - $state"
}
