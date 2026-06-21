package com.example.florida.domain.validation

import com.example.florida.domain.model.Item

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError> = emptyList()
)

enum class ValidationError {
    REQUIRED_NAME,
    REQUIRED_DOCUMENT,
    REQUIRED_ADDRESS,
    INVALID_DOCUMENT,
    INVALID_PHONE,
    EMPTY_ITEMS,
    INVALID_ITEM_DESCRIPTION,
    INVALID_ITEM_QUANTITY,
    INVALID_ITEM_PRICE
}

object FormValidators {
    fun validateClient(
        name: String,
        document: String,
        phone: String,
        address: String
    ): ValidationResult {
        val errors = buildList {
            if (name.isBlank()) add(ValidationError.REQUIRED_NAME)
            if (document.isBlank()) add(ValidationError.REQUIRED_DOCUMENT)
            if (address.isBlank()) add(ValidationError.REQUIRED_ADDRESS)
            if (document.isNotBlank() && document.length !in setOf(11, 14)) {
                add(ValidationError.INVALID_DOCUMENT)
            }
            if (phone.isNotBlank() && phone.length !in setOf(10, 11)) {
                add(ValidationError.INVALID_PHONE)
            }
        }
        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateIssuer(
        name: String,
        document: String,
        phone: String
    ): ValidationResult {
        val errors = buildList {
            if (name.isBlank()) add(ValidationError.REQUIRED_NAME)
            if (document.isBlank()) add(ValidationError.REQUIRED_DOCUMENT)
            if (document.isNotBlank() && document.length !in setOf(11, 14)) {
                add(ValidationError.INVALID_DOCUMENT)
            }
            if (phone.isNotBlank() && phone.length !in setOf(10, 11)) {
                add(ValidationError.INVALID_PHONE)
            }
        }
        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateItems(items: List<Item>): ValidationResult {
        val errors = buildList {
            if (items.isEmpty()) add(ValidationError.EMPTY_ITEMS)
            if (items.any { it.description.isBlank() }) add(ValidationError.INVALID_ITEM_DESCRIPTION)
            if (items.any { it.qty <= 0 }) add(ValidationError.INVALID_ITEM_QUANTITY)
            if (items.any { it.price <= 0 }) add(ValidationError.INVALID_ITEM_PRICE)
        }
        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateNewItem(description: String, qty: Int, price: Long): ValidationResult {
        val errors = buildList {
            if (description.isBlank()) add(ValidationError.INVALID_ITEM_DESCRIPTION)
            if (qty <= 0) add(ValidationError.INVALID_ITEM_QUANTITY)
            if (price <= 0) add(ValidationError.INVALID_ITEM_PRICE)
        }
        return ValidationResult(errors.isEmpty(), errors)
    }
}
