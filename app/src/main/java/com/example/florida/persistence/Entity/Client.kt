package com.example.florida.persistence.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val document: String,
    val phone: String,
    val imagePath: String? = null,
    val deleted: Boolean = false,
)
