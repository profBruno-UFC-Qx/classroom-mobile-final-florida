package com.example.florida.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.florida.persistence.projection.RecentDocumentProjection
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {
    @Query("""
        SELECT *
        FROM (
            SELECT
                'BUDGET' AS type,
                budgets.id AS documentId,
                clients.name AS clientName,
                budgets.total AS total,
                budgets.createdAt AS createdAt
            FROM budgets
            LEFT JOIN clients ON clients.id = budgets.clientId
            UNION ALL
            SELECT
                'RECEIPT' AS type,
                receipts.id AS documentId,
                clients.name AS clientName,
                receipts.total AS total,
                receipts.date AS createdAt
            FROM receipts
            LEFT JOIN clients ON clients.id = receipts.clientId
        )
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    fun observeRecentDocuments(limit: Int): Flow<List<RecentDocumentProjection>>
}
