package com.example.florida.persistence.mapper

import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.BudgetListItem
import com.example.florida.domain.model.BudgetStatus
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.ClientDocumentSummary
import com.example.florida.domain.model.ClientListItem
import com.example.florida.domain.model.DocumentType
import com.example.florida.domain.model.Item
import com.example.florida.domain.model.RecentDocumentSummary
import com.example.florida.domain.model.Receipt
import com.example.florida.domain.model.ReceiptListItem
import com.example.florida.domain.model.UserSetup
import com.example.florida.persistence.entity.BudgetItemEntity
import com.example.florida.persistence.entity.BudgetEntity
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.entity.ReceiptEntity
import com.example.florida.persistence.entity.ReceiptItemEntity
import com.example.florida.persistence.entity.UserEntity
import com.example.florida.persistence.projection.BudgetListProjection
import com.example.florida.persistence.projection.ClientDocumentProjection
import com.example.florida.persistence.projection.RecentDocumentProjection
import com.example.florida.persistence.projection.ReceiptListProjection
import com.example.florida.persistence.relations.BudgetWithItems
import com.example.florida.persistence.relations.ReceiptWithItems

fun ClientEntity.toDomain(): Client {
    return Client(
        id = id,
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = imagePath,
        deleted = deleted
    )
}

fun ClientEntity.toListItem(): ClientListItem {
    return ClientListItem(
        id = id,
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = imagePath
    )
}

fun Client.toEntity(): ClientEntity {
    return ClientEntity(
        id = id,
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = imagePath,
        deleted = deleted
    )
}

fun ClientDocumentProjection.toDomain(): ClientDocumentSummary {
    return ClientDocumentSummary(
        type = if (type == "RECEIPT") DocumentType.RECEIPT else DocumentType.BUDGET,
        documentId = documentId,
        date = date,
        total = total
    )
}

fun BudgetWithItems.toDomain(): Budget {
    return Budget(
        id = budget.id,
        client = client?.toDomain(),
        clientId = budget.clientId,
        notes = budget.notes,
        validade = budget.validade,
        entrega = budget.entrega,
        createdAt = budget.createdAt,
        updateAt = budget.updateAt,
        total = budget.total,
        status = BudgetStatus.from(budget.status),
        items = items.map(BudgetItemEntity::toDomain)
    )
}

fun BudgetListProjection.toDomain(): BudgetListItem {
    return BudgetListItem(
        id = id,
        clientId = clientId,
        clientName = clientName,
        createdAt = createdAt,
        total = total,
        status = BudgetStatus.from(status),
        itemCount = itemCount,
        linkedReceiptId = linkedReceiptId
    )
}

fun BudgetItemEntity.toDomain(): Item {
    return Item(
        id = id,
        description = description,
        qty = qty,
        price = price
    )
}

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        clientId = clientId,
        notes = notes,
        validade = validade,
        entrega = entrega,
        createdAt = createdAt,
        updateAt = updateAt,
        total = total,
        status = status.name
    )
}

fun Item.toBudgetItemEntity(budgetId: Long): BudgetItemEntity {
    return BudgetItemEntity(
        id = id,
        budgetId = budgetId,
        description = description,
        qty = qty,
        price = price
    )
}

fun ReceiptWithItems.toDomain(): Receipt {
    return Receipt(
        id = receipt.id,
        client = client?.toDomain(),
        clientId = receipt.clientId,
        budgetId = receipt.budgetId,
        total = receipt.total,
        date = receipt.date,
        createdAt = receipt.createdAt,
        items = items.map(ReceiptItemEntity::toDomain)
    )
}

fun ReceiptListProjection.toDomain(): ReceiptListItem {
    return ReceiptListItem(
        id = id,
        clientId = clientId,
        clientName = clientName,
        budgetId = budgetId,
        total = total,
        date = date,
        itemCount = itemCount
    )
}

fun ReceiptItemEntity.toDomain(): Item {
    return Item(
        id = id,
        description = description,
        qty = qty,
        price = price
    )
}

fun Receipt.toEntity(): ReceiptEntity {
    return ReceiptEntity(
        id = id,
        clientId = clientId,
        budgetId = budgetId,
        total = total,
        date = date,
        createdAt = createdAt
    )
}

fun Item.toReceiptItemEntity(receiptId: Long): ReceiptItemEntity {
    return ReceiptItemEntity(
        id = id,
        receiptId = receiptId,
        description = description,
        qty = qty,
        price = price
    )
}

fun RecentDocumentProjection.toDomain(): RecentDocumentSummary {
    return RecentDocumentSummary(
        type = if (type == "RECEIPT") DocumentType.RECEIPT else DocumentType.BUDGET,
        documentId = documentId,
        clientName = clientName.orEmpty(),
        total = total,
        createdAt = createdAt
    )
}

fun UserEntity.toDomain(): UserSetup {
    return UserSetup(
        name = name,
        document = document,
        street = street,
        number = number,
        neighborhood = neighborhood,
        city = city,
        state = state,
        phone = phone,
        imagePath = imagePath
    )
}

fun UserSetup.toEntity(): UserEntity {
    return UserEntity(
        id = 1,
        name = name,
        document = document,
        street = street,
        number = number,
        neighborhood = neighborhood,
        city = city,
        state = state,
        phone = phone,
        imagePath = imagePath,
        updatedAt = System.currentTimeMillis()
    )
}
