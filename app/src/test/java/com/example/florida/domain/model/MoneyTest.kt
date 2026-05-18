package com.example.florida.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyTest {
    @Test
    fun sumAddsCentValuesWithoutFloatingPointMath() {
        val total = Money.sum(
            listOf(
                Money(10),
                Money(20),
                Money(30)
            )
        )

        assertEquals(60, total.cents)
    }
}
