package com.example.florida.persistence.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budget_items",
    foreignKeys = [
        ForeignKey(
            entity = BudgetEntity::class,
            parentColumns = ["id"],
            childColumns = ["budgetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("budgetId")]
)
data class BudgetItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val budgetId: Long,
    val description: String,
    val qty: Int,
    val price: Long,
)
