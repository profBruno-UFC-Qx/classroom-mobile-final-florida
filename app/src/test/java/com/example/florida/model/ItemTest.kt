package com.example.florida.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ItemTest {
    @Test
    fun totalUsesQuantityTimesCentPrice() {
        val item = Item(description = "Servico", qty = 3, price = 1250)

        assertEquals(3750, item.total)
    }
}
