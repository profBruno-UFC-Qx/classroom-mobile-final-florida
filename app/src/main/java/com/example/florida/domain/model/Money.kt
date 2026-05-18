package com.example.florida.domain.model

import com.example.florida.extensions.formatForBrl

@JvmInline
value class Money(val cents: Long) {
    operator fun plus(other: Money): Money = Money(cents + other.cents)

    fun formatted(): String = cents.formatForBrl()

    companion object {
        val Zero = Money(0)

        fun sum(values: Iterable<Money>): Money {
            return values.fold(Zero) { total, money -> total + money }
        }
    }
}
