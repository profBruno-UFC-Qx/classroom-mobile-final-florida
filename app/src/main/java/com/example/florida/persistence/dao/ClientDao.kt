package com.example.florida.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.projection.ClientDocumentProjection
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients WHERE deleted = 0 ORDER BY name")
    fun observeClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE deleted = 0 ORDER BY name")
    fun observeClientListItems(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    fun observeClient(id: Long): Flow<ClientEntity?>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun getClient(id: Long): ClientEntity?

    @Query("SELECT * FROM clients WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getClientByRemoteId(remoteId: Long): ClientEntity?

    @Query("SELECT * FROM clients")
    suspend fun getAllClients(): List<ClientEntity>

    @Query("SELECT * FROM clients WHERE syncPending = 1 ORDER BY id ASC")
    suspend fun getPendingSyncClients(): List<ClientEntity>

    @Query("SELECT COUNT(*) FROM clients WHERE deleted = 0")
    fun observeActiveClientCount(): Flow<Int>

    @Query("""
        SELECT 'BUDGET' AS type, id AS documentId, createdAt AS date, total
        FROM budgets
        WHERE clientId = :clientId AND pendingDelete = 0
        UNION ALL
        SELECT 'RECEIPT' AS type, id AS documentId, date AS date, total
        FROM receipts
        WHERE clientId = :clientId AND pendingDelete = 0
        ORDER BY date DESC
    """)
    fun observeClientDocuments(clientId: Long): Flow<List<ClientDocumentProjection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: ClientEntity): Long

    @Update
    suspend fun update(client: ClientEntity)

    @Query("UPDATE clients SET deleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteById(id: Long)
}
