package com.example.florida.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.florida.persistence.Entity.BudgetEntity
import com.example.florida.persistence.Entity.BudgetItemEntity
import com.example.florida.persistence.Entity.ClientEntity

data class BudgetWithItems(
    @Embedded val budget: BudgetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "budgetId"
    )
    val items: List<BudgetItemEntity>,
    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: ClientEntity?,
)
