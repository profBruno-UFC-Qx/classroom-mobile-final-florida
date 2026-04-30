package com.example.florida.persistence.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.florida.persistence.Converters

@Entity(tableName = "user_setup")
@TypeConverters(Converters::class)
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1, // Apenas um usuário por app
    val name: String,
    val document: String,
    val street: String,
    val number: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val phone: String,
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
