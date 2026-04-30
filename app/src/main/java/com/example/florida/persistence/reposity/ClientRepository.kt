package com.example.florida.persistence.reposity

import com.example.florida.model.Client
import com.example.florida.persistence.Entity.ClientEntity
import com.example.florida.persistence.dao.ClientDao
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
