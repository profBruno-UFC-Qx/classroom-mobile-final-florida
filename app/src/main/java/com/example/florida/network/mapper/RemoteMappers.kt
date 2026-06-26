package com.example.florida.network.mapper

import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.BudgetListItem
import com.example.florida.domain.model.BudgetStatus
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.ClientDocumentSummary
import com.example.florida.domain.model.ClientListItem
import com.example.florida.domain.model.DashboardSummary
import com.example.florida.domain.model.DocumentType
import com.example.florida.domain.model.Item
import com.example.florida.domain.model.RecentDocumentSummary
import com.example.florida.domain.model.Receipt
import com.example.florida.domain.model.ReceiptListItem
import com.example.florida.domain.model.UserSetup
import com.example.florida.network.dto.BudgetCreateDto
import com.example.florida.network.dto.BudgetDto
import com.example.florida.network.dto.BudgetListItemDto
import com.example.florida.network.dto.BudgetStatusDto
import com.example.florida.network.dto.BudgetUpdateDto
import com.example.florida.network.dto.ClientCreateDto
import com.example.florida.network.dto.ClientDocumentSummaryDto
import com.example.florida.network.dto.ClientDto
import com.example.florida.network.dto.ClientListItemDto
import com.example.florida.network.dto.ClientUpdateDto
import com.example.florida.network.dto.DashboardSummaryDto
import com.example.florida.network.dto.DocumentTypeDto
import com.example.florida.network.dto.ItemCreateDto
import com.example.florida.network.dto.ItemReadDto
import com.example.florida.network.dto.RecentDocumentSummaryDto
import com.example.florida.network.dto.ReceiptCreateDto
import com.example.florida.network.dto.ReceiptDto
import com.example.florida.network.dto.ReceiptListItemDto
import com.example.florida.network.dto.ReceiptUpdateDto
import com.example.florida.network.dto.UserSetupCreateDto
import com.example.florida.network.dto.UserSetupDto
import com.example.florida.network.dto.UserSetupUpdateDto
import com.example.florida.persistence.entity.BudgetEntity
import com.example.florida.persistence.entity.BudgetItemEntity
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.entity.ReceiptEntity
import com.example.florida.persistence.entity.ReceiptItemEntity
import com.example.florida.persistence.entity.UserEntity
import java.time.LocalDateTime

fun ClientDto.toDomain(): Client {
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

fun ClientDto.toEntity(): ClientEntity {
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

fun Client.toCreateDto(): ClientCreateDto {
    return ClientCreateDto(
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = imagePath,
        deleted = deleted
    )
}

fun Client.toUpdateDto(): ClientUpdateDto {
    return ClientUpdateDto(
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = imagePath,
        deleted = deleted
    )
}

fun ClientListItemDto.toDomain(): ClientListItem {
    return ClientListItem(
        id = id,
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = imagePath
    )
}

fun ClientDocumentSummaryDto.toDomain(): ClientDocumentSummary {
    return ClientDocumentSummary(
        type = type.toDomain(),
        documentId = documentId,
        date = LocalDateTime.parse(date),
        total = total
    )
}

fun BudgetDto.toDomain(): Budget {
    return Budget(
        id = id,
        clientId = clientId,
        notes = notes,
        validade = validade,
        entrega = entrega,
        createdAt = LocalDateTime.parse(createdAt),
        updateAt = LocalDateTime.parse(updateAt),
        total = total,
        status = status.toDomain(),
        items = items.map(ItemReadDto::toDomain)
    )
}

fun BudgetDto.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        clientId = clientId,
        notes = notes,
        validade = validade,
        entrega = entrega,
        createdAt = LocalDateTime.parse(createdAt),
        updateAt = LocalDateTime.parse(updateAt),
        total = total,
        status = status.name
    )
}

fun BudgetDto.toItemEntities(): List<BudgetItemEntity> {
    return items.map { it.toBudgetItemEntity(id) }
}

fun Budget.toCreateDto(): BudgetCreateDto {
    return BudgetCreateDto(
        clientId = clientId,
        notes = notes,
        validade = validade,
        entrega = entrega,
        total = total,
        status = status.toDto(),
        items = items.map(Item::toCreateDto)
    )
}

fun Budget.toUpdateDto(): BudgetUpdateDto {
    return BudgetUpdateDto(
        clientId = clientId,
        notes = notes,
        validade = validade,
        entrega = entrega,
        total = total,
        status = status.toDto(),
        items = items.map(Item::toCreateDto)
    )
}

fun BudgetListItemDto.toDomain(): BudgetListItem {
    return BudgetListItem(
        id = id,
        clientId = clientId,
        clientName = clientName,
        createdAt = LocalDateTime.parse(createdAt),
        total = total,
        status = status.toDomain(),
        itemCount = itemCount,
        linkedReceiptId = linkedReceiptId
    )
}

fun ReceiptDto.toDomain(): Receipt {
    return Receipt(
        id = id,
        clientId = clientId,
        budgetId = budgetId,
        total = total,
        date = LocalDateTime.parse(date),
        createdAt = LocalDateTime.parse(createdAt),
        items = items.map(ItemReadDto::toDomain)
    )
}

fun ReceiptDto.toEntity(): ReceiptEntity {
    return ReceiptEntity(
        id = id,
        clientId = clientId,
        budgetId = budgetId,
        total = total,
        date = LocalDateTime.parse(date),
        createdAt = LocalDateTime.parse(createdAt)
    )
}

fun ReceiptDto.toItemEntities(): List<ReceiptItemEntity> {
    return items.map { it.toReceiptItemEntity(id) }
}

fun Receipt.toCreateDto(): ReceiptCreateDto {
    return ReceiptCreateDto(
        clientId = clientId,
        budgetId = budgetId,
        total = total,
        date = date.toString(),
        items = items.map(Item::toCreateDto)
    )
}

fun Receipt.toUpdateDto(): ReceiptUpdateDto {
    return ReceiptUpdateDto(
        clientId = clientId,
        budgetId = budgetId,
        total = total,
        date = date.toString(),
        items = items.map(Item::toCreateDto)
    )
}

fun ReceiptListItemDto.toDomain(): ReceiptListItem {
    return ReceiptListItem(
        id = id,
        clientId = clientId,
        clientName = clientName,
        budgetId = budgetId,
        total = total,
        date = LocalDateTime.parse(date),
        itemCount = itemCount
    )
}

fun UserSetupDto.toDomain(): UserSetup {
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

fun UserSetupDto.toEntity(): UserEntity {
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
        imagePath = imagePath
    )
}

fun UserSetup.toCreateDto(): UserSetupCreateDto {
    return UserSetupCreateDto(
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

fun UserSetup.toUpdateDto(): UserSetupUpdateDto {
    return UserSetupUpdateDto(
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

fun DashboardSummaryDto.toDomain(): DashboardSummary {
    return DashboardSummary(
        clientCount = clientCount,
        budgetCount = budgetCount,
        receiptCount = receiptCount,
        totalBudgeted = totalBudgeted,
        totalReceived = totalReceived,
        monthReceived = monthReceived,
        recentDocuments = recentDocuments.map(RecentDocumentSummaryDto::toDomain)
    )
}

fun RecentDocumentSummaryDto.toDomain(): RecentDocumentSummary {
    return RecentDocumentSummary(
        type = type.toDomain(),
        documentId = documentId,
        clientName = clientName,
        total = total,
        createdAt = LocalDateTime.parse(createdAt)
    )
}

fun ItemReadDto.toDomain(): Item {
    return Item(
        id = id,
        description = description,
        qty = qty,
        price = price
    )
}

fun Item.toCreateDto(): ItemCreateDto {
    return ItemCreateDto(
        description = description,
        qty = qty,
        price = price
    )
}

fun ItemReadDto.toBudgetItemEntity(budgetId: Long): BudgetItemEntity {
    return BudgetItemEntity(
        id = id,
        budgetId = budgetId,
        description = description,
        qty = qty,
        price = price
    )
}

fun ItemReadDto.toReceiptItemEntity(receiptId: Long): ReceiptItemEntity {
    return ReceiptItemEntity(
        id = id,
        receiptId = receiptId,
        description = description,
        qty = qty,
        price = price
    )
}

fun BudgetStatusDto.toDomain(): BudgetStatus = BudgetStatus.from(name)

fun BudgetStatus.toDto(): BudgetStatusDto = BudgetStatusDto.valueOf(name)

fun DocumentTypeDto.toDomain(): DocumentType = DocumentType.valueOf(name)
