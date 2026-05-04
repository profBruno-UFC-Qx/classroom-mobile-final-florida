package com.example.florida.model

import androidx.annotation.StringRes
import com.example.florida.R

enum class BudgetStatus(@StringRes val labelRes: Int) {
    DRAFT(R.string.status_draft),
    SENT(R.string.status_sent),
    APPROVED(R.string.status_approved),
    REJECTED(R.string.status_rejected),
    EXPIRED(R.string.status_expired);

    companion object {
        fun from(value: String?): BudgetStatus {
            return entries.firstOrNull { it.name == value } ?: DRAFT
        }
    }
}
