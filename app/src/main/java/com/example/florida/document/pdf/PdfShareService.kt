package com.example.florida.document.pdf

import android.content.Context
import com.example.florida.R
import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.Receipt
import com.example.florida.domain.model.UserSetup
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class PdfShareService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun shareBudget(user: UserSetup, budget: Budget) {
        val file = withContext(Dispatchers.IO) {
            BudgetPdfCreator(
                user = user,
                client = budget.client,
                itens = budget.items,
                observasion = budget.notes.orEmpty(),
                date = budget.createdAt.atZone(ZoneId.systemDefault()).toOffsetDateTime(),
                budgetNumber = budget.id.toString(),
                prazo = budget.entrega,
                validade = budget.validade,
                context = context
            )
        }
        withContext(Dispatchers.Main) {
            sharePdf(context, file, context.getString(R.string.share_budget))
        }
    }

    suspend fun shareReceipt(user: UserSetup, receipt: Receipt) {
        val file = withContext(Dispatchers.IO) {
            ReceiptPdfCreate(
                context = context,
                user = user,
                cliente = receipt.client,
                itens = receipt.items,
                budgetNumber = receipt.id.toInt(),
                dateStr = receipt.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            )
        }
        withContext(Dispatchers.Main) {
            sharePdf(context, file, context.getString(R.string.share_receipt))
        }
    }
}
