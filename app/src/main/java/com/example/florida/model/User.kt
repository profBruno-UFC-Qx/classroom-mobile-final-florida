package com.example.florida.model

import android.graphics.Bitmap
import android.net.Uri

data class UserSetup(
    val name: String = "Francisco",
    val document: String = "06364254307",
    val street: String = "Rua dos Bobos",
    val number: String = "0",
    val neighborhood: String = "Bairro dos Bobos",
    val city: String = "Cidade dos Bobos",
    val state: String = "SP",
    val imageUri: Uri? = null,
    val imageBitmap: Bitmap? = null,
){
    fun addImage(uri: Uri) = copy(imageUri = uri)
}