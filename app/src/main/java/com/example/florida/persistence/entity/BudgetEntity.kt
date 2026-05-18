package com.example.florida.persistence.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("clientId")]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long?,
    val notes: String?,
    val validade: String?,
    val entrega: String?,
    val createdAt: LocalDateTime,
    val updateAt: LocalDateTime,
    val total: Long,
    val status: String = "DRAFT",
)
