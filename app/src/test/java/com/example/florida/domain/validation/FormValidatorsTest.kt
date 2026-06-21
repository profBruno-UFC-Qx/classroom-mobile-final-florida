package com.example.florida.domain.validation

import com.example.florida.domain.model.Item
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormValidatorsTest {
    @Test
    fun validateClient_acceptsRequiredFieldsAndFormattedLengths() {
        val result = FormValidators.validateClient(
            name = "Cliente",
            document = "12345678901",
            phone = "11999999999",
            address = "Rua 1"
        )

        assertTrue(result.isValid)
    }

    @Test
    fun validateClientRejectsInvalidDocumentAndPhone() {
        val result = FormValidators.validateClient(
            name = "Cliente",
            document = "123",
            phone = "999",
            address = "Rua 1"
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(ValidationError.INVALID_DOCUMENT))
        assertTrue(result.errors.contains(ValidationError.INVALID_PHONE))
    }

    @Test
    fun validateItemsRequiresAtLeastOneValidItem() {
        val emptyResult = FormValidators.validateItems(emptyList())
        val validResult = FormValidators.validateItems(
            listOf(Item(description = "Servico", qty = 2, price = 1500))
        )

        assertFalse(emptyResult.isValid)
        assertEquals(listOf(ValidationError.EMPTY_ITEMS), emptyResult.errors)
        assertTrue(validResult.isValid)
    }

    @Test
    fun validateNewItemRejectsZeroQuantityAndPrice() {
        val result = FormValidators.validateNewItem(
            description = "",
            qty = 0,
            price = 0
        )

        assertFalse(result.isValid)
        assertEquals(
            listOf(
                ValidationError.INVALID_ITEM_DESCRIPTION,
                ValidationError.INVALID_ITEM_QUANTITY,
                ValidationError.INVALID_ITEM_PRICE
            ),
            result.errors
        )
    }
}
