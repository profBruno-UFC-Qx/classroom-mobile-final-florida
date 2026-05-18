package com.example.florida.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.entity.ReceiptEntity
import com.example.florida.persistence.entity.ReceiptItemEntity

data class ReceiptWithItems(
    @Embedded val receipt: ReceiptEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "receiptId"
    )
    val items: List<ReceiptItemEntity>,
    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: ClientEntity?,
)
