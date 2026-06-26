package com.example.florida.persistence.repository

import com.example.florida.domain.model.ClientDocumentSummary
import com.example.florida.domain.model.ClientListItem
import com.example.florida.domain.model.Client
import com.example.florida.network.FloridaRemoteRepository
import com.example.florida.persistence.dao.ClientDao
import com.example.florida.persistence.mapper.toDomain
import com.example.florida.persistence.mapper.toEntity
import com.example.florida.persistence.mapper.toListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ClientRepository(
    private val clientDao: ClientDao,
    private val remoteRepository: FloridaRemoteRepository,
    private val syncRepository: SyncRepository,
) {
    fun observeClients(): Flow<List<Client>> {
        return clientDao.observeClients().map { clients ->
            clients.map { it.toDomain() }
        }
    }

    fun observeClientListItems(): Flow<List<ClientListItem>> {
        return clientDao.observeClientListItems().map { clients ->
            clients.map { it.toListItem() }
        }
    }

    fun observeClient(id: Long): Flow<Client?> {
        return clientDao.observeClient(id).map { it?.toDomain() }
    }

    fun observeClientDocuments(clientId: Long): Flow<List<ClientDocumentSummary>> {
        return clientDao.observeClientDocuments(clientId).map { documents ->
            documents.map { it.toDomain() }
        }
    }

    suspend fun saveClient(client: Client): Long = withContext(Dispatchers.IO) {
        val existingClient = client.takeIf { it.id != 0L }?.let { clientDao.getClient(it.id) }
        val localId = clientDao.insert(
            client.toEntity().copy(
                imagePath = client.imagePath ?: existingClient?.imagePath,
                remoteId = existingClient?.remoteId,
                syncPending = true,
            )
        )
        runCatching { syncRepository.syncPendingChanges() }
        if (client.id == 0L) {
            localId
        } else {
            client.id
        }
    }

    suspend fun deleteClient(client: Client) = withContext(Dispatchers.IO) {
        val existingClient = clientDao.getClient(client.id) ?: return@withContext
        if (existingClient.remoteId == null) {
            clientDao.deleteById(existingClient.id)
        } else {
            clientDao.insert(existingClient.copy(deleted = true, syncPending = true))
            runCatching { syncRepository.syncPendingChanges() }
        }
    }
}
