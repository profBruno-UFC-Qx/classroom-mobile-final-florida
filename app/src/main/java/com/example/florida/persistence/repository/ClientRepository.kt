package com.example.florida.persistence.repository

import com.example.florida.domain.model.ClientDocumentSummary
import com.example.florida.domain.model.ClientListItem
import com.example.florida.domain.model.DocumentType
import com.example.florida.domain.model.Client
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.dao.ClientDao
import com.example.florida.persistence.projection.ClientDocumentProjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ClientRepository(private val clientDao: ClientDao) {
    fun observeClients(): Flow<List<Client>> {
        return clientDao.observeClients().map { clients ->
            clients.map { it.toClient() }
        }
    }

    fun observeClientListItems(): Flow<List<ClientListItem>> {
        return clientDao.observeClientListItems().map { clients ->
            clients.map { it.toClientListItem() }
        }
    }

    fun observeClient(id: Long): Flow<Client?> {
        return clientDao.observeClient(id).map { it?.toClient() }
    }

    fun observeClientDocuments(clientId: Long): Flow<List<ClientDocumentSummary>> {
        return clientDao.observeClientDocuments(clientId).map { documents ->
            documents.map { it.toClientDocumentSummary() }
        }
    }

    suspend fun saveClient(client: Client): Long = withContext(Dispatchers.IO) {
        clientDao.insert(client.toEntity())
    }

    suspend fun deleteClient(client: Client) = withContext(Dispatchers.IO) {
        clientDao.softDelete(client.id)
    }

    private fun ClientEntity.toClient(): Client {
        return Client(
            id = id,
            name = name,
            address = address,
            document = document,
            phone = phone,
            imagePath = imagePath,
            deleted = deleted
        )
    }

    private fun ClientEntity.toClientListItem(): ClientListItem {
        return ClientListItem(
            id = id,
            name = name,
            address = address,
            document = document,
            phone = phone,
            imagePath = imagePath
        )
    }

    private fun ClientDocumentProjection.toClientDocumentSummary(): ClientDocumentSummary {
        return ClientDocumentSummary(
            type = if (type == "RECEIPT") DocumentType.RECEIPT else DocumentType.BUDGET,
            documentId = documentId,
            date = date,
            total = total
        )
    }

    private fun Client.toEntity(): ClientEntity {
        return ClientEntity(
            id = id,
            name = name,
            address = address,
            document = document,
            phone = phone,
            imagePath = imagePath,
            deleted = deleted
        )
    }
}
