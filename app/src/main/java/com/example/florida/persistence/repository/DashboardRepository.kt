package com.example.florida.persistence.repository

import com.example.florida.domain.model.DashboardSummary
import com.example.florida.domain.model.DocumentType
import com.example.florida.domain.model.RecentDocumentSummary
import com.example.florida.persistence.dao.BudgetDao
import com.example.florida.persistence.dao.ClientDao
import com.example.florida.persistence.dao.DashboardDao
import com.example.florida.persistence.dao.ReceiptDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class DashboardRepository(
    private val clientDao: ClientDao,
    private val budgetDao: BudgetDao,
    private val receiptDao: ReceiptDao,
    private val dashboardDao: DashboardDao,
) {
    fun observeDashboardSummary(): Flow<DashboardSummary> {
        val now = LocalDate.now()
        val monthStart = now.withDayOfMonth(1).atStartOfDay()
        val nextMonthStart = now.plusMonths(1).withDayOfMonth(1).atStartOfDay()

        return combine(
            clientDao.observeActiveClientCount(),
            budgetDao.observeBudgetCount(),
            receiptDao.observeReceiptCount(),
            budgetDao.observeTotalBudgeted(),
            receiptDao.observeTotalReceived(),
            receiptDao.observeReceivedBetween(monthStart, nextMonthStart),
            dashboardDao.observeRecentDocuments(limit = 7)
        ) { values ->
            DashboardSummary(
                clientCount = values[0] as Int,
                budgetCount = values[1] as Int,
                receiptCount = values[2] as Int,
                totalBudgeted = values[3] as Long,
                totalReceived = values[4] as Long,
                monthReceived = values[5] as Long,
                recentDocuments = (values[6] as List<*>).mapNotNull { projection ->
                    val document = projection as? com.example.florida.persistence.projection.RecentDocumentProjection
                    document?.let {
                        RecentDocumentSummary(
                            type = if (it.type == "RECEIPT") DocumentType.RECEIPT else DocumentType.BUDGET,
                            documentId = it.documentId,
                            clientName = it.clientName.orEmpty(),
                            total = it.total,
                            createdAt = it.createdAt
                        )
                    }
                }
            )
        }
    }
}
