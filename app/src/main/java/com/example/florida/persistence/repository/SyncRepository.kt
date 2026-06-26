package com.example.florida.persistence.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.florida.domain.model.Budget
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.Receipt
import com.example.florida.network.FloridaRemoteRepository
import com.example.florida.network.dto.BudgetDto
import com.example.florida.network.dto.ClientDto
import com.example.florida.network.dto.ReceiptDto
import com.example.florida.network.dto.UserSetupDto
import com.example.florida.persistence.AppDatabase
import com.example.florida.persistence.dao.BudgetDao
import com.example.florida.persistence.dao.ClientDao
import com.example.florida.persistence.dao.ReceiptDao
import com.example.florida.persistence.dao.UserDao
import com.example.florida.persistence.entity.BudgetItemEntity
import com.example.florida.persistence.entity.BudgetEntity
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.entity.ReceiptItemEntity
import com.example.florida.persistence.entity.ReceiptEntity
import com.example.florida.persistence.entity.UserEntity
import com.example.florida.persistence.mapper.UserSetupPlaceholders
import com.example.florida.persistence.mapper.isPlaceholderSetup
import com.example.florida.persistence.mapper.toDomain
import com.example.florida.persistence.relations.BudgetWithItems
import com.example.florida.persistence.relations.ReceiptWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class SyncRepository(
    private val database: AppDatabase,
    private val userDao: UserDao,
    private val clientDao: ClientDao,
    private val budgetDao: BudgetDao,
    private val receiptDao: ReceiptDao,
    private val remoteRepository: FloridaRemoteRepository,
) {
    companion object {
        private const val TAG = "SyncRepository"
    }

    private val syncMutex = Mutex()

    suspend fun syncPendingChanges() = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            syncUserSetup()
            syncClients()
            syncBudgets()
            syncReceipts()
            syncRemoteState()
        }
    }

    suspend fun restoreFromRemoteBackup() = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            runCatching {
                val payload = remoteRepository.getSyncPayload()
                database.clearAllTables()
                database.withTransaction {
                    mergeUserSetup(payload.userSetup)
                    val clientIdsByRemoteId = mergeClients(payload.clients)
                    val budgetIdsByRemoteId = mergeBudgets(payload.budgets, clientIdsByRemoteId)
                    mergeReceipts(payload.receipts, clientIdsByRemoteId, budgetIdsByRemoteId)
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to restore remote backup", throwable)
                throw throwable
            }
        }
    }

    suspend fun pushFullStateToRemote() = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            val failures = mutableListOf<String>()

            syncUserSetup(failures)

            val remotePayload = remoteRepository.getSyncPayload()

            pushAllClientsToRemote(failures)
            pushAllBudgetsToRemote(failures)
            pushAllReceiptsToRemote(failures)

            deleteRemoteOnlyReceipts(remotePayload.receipts.mapTo(mutableSetOf()) { it.id }, failures)
            deleteRemoteOnlyBudgets(remotePayload.budgets.mapTo(mutableSetOf()) { it.id }, failures)
            deleteRemoteOnlyClients(remotePayload.clients, failures)

            if (failures.isNotEmpty()) {
                throw IllegalStateException(failures.joinToString(separator = "\n"))
            }
        }
    }

    private suspend fun syncUserSetup(failures: MutableList<String>? = null) {
        val localUser = userDao.getUserSetup() ?: return
        if (localUser.isPlaceholderSetup()) return
        runCatching {
            remoteRepository.saveUserSetup(localUser.toDomain())
        }.onFailure { throwable ->
            Log.e(TAG, "Failed to sync user setup", throwable)
            failures?.add("user setup: ${throwable.message ?: "erro ao sincronizar"}")
        }
    }

    private suspend fun pushAllClientsToRemote(failures: MutableList<String>? = null) {
        clientDao.getAllClients().forEach { client ->
            runCatching {
                when {
                    client.deleted && client.remoteId != null -> {
                        remoteRepository.deleteClient(client.remoteId)
                        clientDao.deleteById(client.id)
                    }
                    client.deleted -> clientDao.deleteById(client.id)
                    client.remoteId == null -> {
                        val created = remoteRepository.createClient(client.toRemoteCreate())
                        clientDao.insert(client.copy(remoteId = created.id, syncPending = false, deleted = false))
                    }
                    else -> {
                        remoteRepository.updateClient(client.toRemoteUpdate())
                        clientDao.insert(client.copy(syncPending = false, deleted = false))
                    }
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to push full client localId=${client.id}", throwable)
                failures?.add("cliente ${client.name}: ${throwable.message ?: "erro ao enviar"}")
            }
        }
    }

    private suspend fun pushAllBudgetsToRemote(failures: MutableList<String>? = null) {
        budgetDao.getAllBudgets().forEach { budgetWithItems ->
            val budget = budgetWithItems.budget
            runCatching {
                val remoteClientId = budget.clientId?.let { clientDao.getClient(it)?.remoteId }

                when {
                    budget.pendingDelete && budget.remoteId != null -> {
                        remoteRepository.deleteBudget(budget.remoteId)
                        budgetDao.deleteBudget(budget.id)
                    }
                    budget.pendingDelete -> budgetDao.deleteBudget(budget.id)
                    budget.clientId != null && remoteClientId == null -> Unit
                    budget.remoteId == null -> {
                        val created = remoteRepository.createBudget(
                            budgetWithItems.toRemoteBudget(remoteClientId, null)
                        )
                        budgetDao.insertBudget(
                            budget.copy(remoteId = created.id, syncPending = false, pendingDelete = false)
                        )
                    }
                    else -> {
                        remoteRepository.updateBudget(
                            budgetWithItems.toRemoteBudget(remoteClientId, budget.remoteId)
                        )
                        budgetDao.insertBudget(
                            budget.copy(syncPending = false, pendingDelete = false)
                        )
                    }
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to push full budget localId=${budget.id}", throwable)
                failures?.add("orcamento ${budget.id}: ${throwable.message ?: "erro ao enviar"}")
            }
        }
    }

    private suspend fun pushAllReceiptsToRemote(failures: MutableList<String>? = null) {
        receiptDao.getAllReceipts().forEach { receiptWithItems ->
            val receipt = receiptWithItems.receipt
            runCatching {
                val remoteClientId = receipt.clientId?.let { clientDao.getClient(it)?.remoteId }
                val remoteBudgetId = receipt.budgetId?.let { budgetDao.getBudget(it)?.budget?.remoteId }

                when {
                    receipt.pendingDelete && receipt.remoteId != null -> {
                        remoteRepository.deleteReceipt(receipt.remoteId)
                        receiptDao.deleteReceipt(receipt.id)
                    }
                    receipt.pendingDelete -> receiptDao.deleteReceipt(receipt.id)
                    receipt.clientId != null && remoteClientId == null -> Unit
                    receipt.budgetId != null && remoteBudgetId == null -> Unit
                    receipt.remoteId == null -> {
                        val created = remoteRepository.createReceipt(
                            receiptWithItems.toRemoteReceipt(remoteClientId, remoteBudgetId, null)
                        )
                        receiptDao.insertReceipt(
                            receipt.copy(remoteId = created.id, syncPending = false, pendingDelete = false)
                        )
                    }
                    else -> {
                        remoteRepository.updateReceipt(
                            receiptWithItems.toRemoteReceipt(remoteClientId, remoteBudgetId, receipt.remoteId)
                        )
                        receiptDao.insertReceipt(
                            receipt.copy(syncPending = false, pendingDelete = false)
                        )
                    }
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to push full receipt localId=${receipt.id}", throwable)
                failures?.add("recibo ${receipt.id}: ${throwable.message ?: "erro ao enviar"}")
            }
        }
    }

    private suspend fun deleteRemoteOnlyReceipts(
        remoteReceiptIds: Set<Long>,
        failures: MutableList<String>? = null,
    ) {
        val localRemoteIds = receiptDao.getAllReceipts()
            .mapNotNull { receiptWithItems -> receiptWithItems.receipt.remoteId }
            .toSet()

        remoteReceiptIds
            .filterNot(localRemoteIds::contains)
            .forEach { remoteId ->
                runCatching { remoteRepository.deleteReceipt(remoteId) }
                    .onFailure { throwable ->
                        Log.e(TAG, "Failed to delete remote-only receipt remoteId=$remoteId", throwable)
                        failures?.add("recibo remoto $remoteId: ${throwable.message ?: "erro ao excluir"}")
                    }
            }
    }

    private suspend fun deleteRemoteOnlyBudgets(
        remoteBudgetIds: Set<Long>,
        failures: MutableList<String>? = null,
    ) {
        val localRemoteIds = budgetDao.getAllBudgets()
            .mapNotNull { budgetWithItems -> budgetWithItems.budget.remoteId }
            .toSet()

        remoteBudgetIds
            .filterNot(localRemoteIds::contains)
            .forEach { remoteId ->
                runCatching { remoteRepository.deleteBudget(remoteId) }
                    .onFailure { throwable ->
                        Log.e(TAG, "Failed to delete remote-only budget remoteId=$remoteId", throwable)
                        failures?.add("orcamento remoto $remoteId: ${throwable.message ?: "erro ao excluir"}")
                    }
            }
    }

    private suspend fun deleteRemoteOnlyClients(
        remoteClients: List<ClientDto>,
        failures: MutableList<String>? = null,
    ) {
        val localRemoteIds = clientDao.getAllClients()
            .mapNotNull { client -> client.remoteId }
            .toSet()

        remoteClients
            .filter { !it.deleted && it.id !in localRemoteIds }
            .forEach { remoteClient ->
                runCatching { remoteRepository.deleteClient(remoteClient.id) }
                    .onFailure { throwable ->
                        Log.e(TAG, "Failed to delete remote-only client remoteId=${remoteClient.id}", throwable)
                        failures?.add("cliente remoto ${remoteClient.id}: ${throwable.message ?: "erro ao excluir"}")
                    }
            }
    }

    private suspend fun syncClients() {
        clientDao.getPendingSyncClients().forEach { client ->
            runCatching {
                when {
                    client.deleted && client.remoteId != null -> {
                        remoteRepository.deleteClient(client.remoteId)
                        clientDao.deleteById(client.id)
                    }
                    client.deleted -> clientDao.deleteById(client.id)
                    client.remoteId == null -> {
                        val created = remoteRepository.createClient(client.toRemoteCreate())
                        clientDao.insert(client.copy(remoteId = created.id, syncPending = false))
                    }
                    else -> {
                        remoteRepository.updateClient(client.toRemoteUpdate())
                        clientDao.insert(client.copy(syncPending = false))
                    }
                }
            }.onFailure { throwable ->
                Log.e(
                    TAG,
                    "Failed to sync client localId=${client.id} remoteId=${client.remoteId}",
                    throwable
                )
            }
        }
    }

    private suspend fun syncBudgets() {
        budgetDao.getPendingSyncBudgets().forEach { budgetWithItems ->
            runCatching {
                val budget = budgetWithItems.budget
                val remoteClientId = budget.clientId?.let { clientDao.getClient(it)?.remoteId }
                if (budget.clientId != null && remoteClientId == null) return@runCatching

                when {
                    budget.pendingDelete && budget.remoteId != null -> {
                        remoteRepository.deleteBudget(budget.remoteId)
                        budgetDao.deleteBudget(budget.id)
                    }
                    budget.pendingDelete -> budgetDao.deleteBudget(budget.id)
                    budget.remoteId == null -> {
                        val created = remoteRepository.createBudget(
                            budgetWithItems.toRemoteBudget(remoteClientId, null)
                        )
                        budgetDao.insertBudget(budget.copy(remoteId = created.id, syncPending = false))
                    }
                    else -> {
                        remoteRepository.updateBudget(
                            budgetWithItems.toRemoteBudget(remoteClientId, budget.remoteId)
                        )
                        budgetDao.insertBudget(budget.copy(syncPending = false))
                    }
                }
            }.onFailure { throwable ->
                Log.e(
                    TAG,
                    "Failed to sync budget localId=${budgetWithItems.budget.id} remoteId=${budgetWithItems.budget.remoteId}",
                    throwable
                )
            }
        }
    }

    private suspend fun syncReceipts() {
        receiptDao.getPendingSyncReceipts().forEach { receiptWithItems ->
            runCatching {
                val receipt = receiptWithItems.receipt
                val remoteClientId = receipt.clientId?.let { clientDao.getClient(it)?.remoteId }
                val remoteBudgetId = receipt.budgetId?.let { budgetDao.getBudget(it)?.budget?.remoteId }

                if (receipt.clientId != null && remoteClientId == null) return@runCatching
                if (receipt.budgetId != null && remoteBudgetId == null) return@runCatching

                when {
                    receipt.pendingDelete && receipt.remoteId != null -> {
                        remoteRepository.deleteReceipt(receipt.remoteId)
                        receiptDao.deleteReceipt(receipt.id)
                    }
                    receipt.pendingDelete -> receiptDao.deleteReceipt(receipt.id)
                    receipt.remoteId == null -> {
                        val created = remoteRepository.createReceipt(
                            receiptWithItems.toRemoteReceipt(remoteClientId, remoteBudgetId, null)
                        )
                        receiptDao.insertReceipt(receipt.copy(remoteId = created.id, syncPending = false))
                    }
                    else -> {
                        remoteRepository.updateReceipt(
                            receiptWithItems.toRemoteReceipt(remoteClientId, remoteBudgetId, receipt.remoteId)
                        )
                        receiptDao.insertReceipt(receipt.copy(syncPending = false))
                    }
                }
            }.onFailure { throwable ->
                Log.e(
                    TAG,
                    "Failed to sync receipt localId=${receiptWithItems.receipt.id} remoteId=${receiptWithItems.receipt.remoteId}",
                    throwable
                )
            }
        }
    }

    private suspend fun syncRemoteState() {
        runCatching {
            val payload = remoteRepository.getSyncPayload()
            database.withTransaction {
                mergeUserSetup(payload.userSetup)
                val clientIdsByRemoteId = mergeClients(payload.clients)
                val budgetIdsByRemoteId = mergeBudgets(payload.budgets, clientIdsByRemoteId)
                mergeReceipts(payload.receipts, clientIdsByRemoteId, budgetIdsByRemoteId)
                pruneDeletedReceipts(payload.receipts.mapTo(mutableSetOf()) { it.id })
                pruneDeletedBudgets(payload.budgets.mapTo(mutableSetOf()) { it.id })
                pruneDeletedClients(payload.clients.mapTo(mutableSetOf()) { it.id })
            }
        }.onFailure { throwable ->
            Log.e(TAG, "Failed to pull remote state", throwable)
        }
    }

    private suspend fun mergeUserSetup(remoteUserSetup: UserSetupDto?) {
        if (remoteUserSetup == null) return
        if (remoteUserSetup.isPlaceholderSetup()) return
        val localUser = userDao.getUserSetup()
        if (localUser != null) return

        userDao.insertUser(
            UserEntity(
                id = 1,
                name = remoteUserSetup.name,
                document = remoteUserSetup.document,
                street = remoteUserSetup.street,
                number = remoteUserSetup.number,
                neighborhood = remoteUserSetup.neighborhood,
                city = remoteUserSetup.city,
                state = remoteUserSetup.state,
                phone = remoteUserSetup.phone,
                imagePath = remoteUserSetup.imagePath
            )
        )
    }

    private suspend fun mergeClients(remoteClients: List<ClientDto>): Map<Long, Long> {
        val clientIdsByRemoteId = mutableMapOf<Long, Long>()

        remoteClients.forEach { remoteClient ->
            val localClient = clientDao.getClientByRemoteId(remoteClient.id)
            if (localClient?.syncPending == true) {
                clientIdsByRemoteId[remoteClient.id] = localClient.id
                return@forEach
            }

            val mergedClient = ClientEntity(
                id = localClient?.id ?: 0,
                remoteId = remoteClient.id,
                name = remoteClient.name,
                address = remoteClient.address,
                document = remoteClient.document,
                phone = remoteClient.phone,
                imagePath = localClient?.imagePath ?: remoteClient.imagePath,
                deleted = remoteClient.deleted,
                syncPending = false
            )
            val localId = clientDao.insert(mergedClient)
            clientIdsByRemoteId[remoteClient.id] = localClient?.id ?: localId
        }

        return clientIdsByRemoteId
    }

    private suspend fun mergeBudgets(
        remoteBudgets: List<BudgetDto>,
        clientIdsByRemoteId: Map<Long, Long>,
    ): Map<Long, Long> {
        val budgetIdsByRemoteId = mutableMapOf<Long, Long>()

        remoteBudgets.forEach { remoteBudget ->
            val localBudgetWithItems = budgetDao.getBudgetByRemoteId(remoteBudget.id)
            val localBudget = localBudgetWithItems?.budget
            if (localBudget?.syncPending == true) {
                budgetIdsByRemoteId[remoteBudget.id] = localBudget.id
                return@forEach
            }

            val mergedBudget = BudgetEntity(
                id = localBudget?.id ?: 0,
                remoteId = remoteBudget.id,
                clientId = remoteBudget.clientId?.let(clientIdsByRemoteId::get),
                notes = remoteBudget.notes,
                validade = remoteBudget.validade,
                entrega = remoteBudget.entrega,
                createdAt = LocalDateTime.parse(remoteBudget.createdAt),
                updateAt = LocalDateTime.parse(remoteBudget.updateAt),
                total = remoteBudget.total,
                status = remoteBudget.status.name,
                syncPending = false,
                pendingDelete = false
            )
            val mergedItems = remoteBudget.items.map {
                BudgetItemEntity(
                    budgetId = 0,
                    description = it.description,
                    qty = it.qty,
                    price = it.price
                )
            }

            val localId = if (localBudget == null) {
                budgetDao.insertBudgetWithItems(mergedBudget, mergedItems)
            } else {
                budgetDao.updateBudgetWithItems(mergedBudget, mergedItems)
                localBudget.id
            }

            budgetIdsByRemoteId[remoteBudget.id] = localId
        }

        return budgetIdsByRemoteId
    }

    private suspend fun mergeReceipts(
        remoteReceipts: List<ReceiptDto>,
        clientIdsByRemoteId: Map<Long, Long>,
        budgetIdsByRemoteId: Map<Long, Long>,
    ) {
        remoteReceipts.forEach { remoteReceipt ->
            val localReceiptWithItems = receiptDao.getReceiptByRemoteId(remoteReceipt.id)
            val localReceipt = localReceiptWithItems?.receipt
            if (localReceipt?.syncPending == true) return@forEach

            val mergedReceipt = ReceiptEntity(
                id = localReceipt?.id ?: 0,
                remoteId = remoteReceipt.id,
                clientId = remoteReceipt.clientId?.let(clientIdsByRemoteId::get),
                budgetId = remoteReceipt.budgetId?.let(budgetIdsByRemoteId::get),
                total = remoteReceipt.total,
                date = LocalDateTime.parse(remoteReceipt.date),
                createdAt = LocalDateTime.parse(remoteReceipt.createdAt),
                syncPending = false,
                pendingDelete = false
            )
            val mergedItems = remoteReceipt.items.map {
                ReceiptItemEntity(
                    receiptId = 0,
                    description = it.description,
                    qty = it.qty,
                    price = it.price
                )
            }

            if (localReceipt == null) {
                receiptDao.insertReceiptWithItems(mergedReceipt, mergedItems)
            } else {
                receiptDao.updateReceiptWithItems(mergedReceipt, mergedItems)
            }
        }
    }

    private suspend fun pruneDeletedReceipts(remoteReceiptIds: Set<Long>) {
        receiptDao.getAllReceipts().forEach { localReceiptWithItems ->
            val localReceipt = localReceiptWithItems.receipt
            val remoteId = localReceipt.remoteId ?: return@forEach
            if (localReceipt.syncPending) return@forEach
            if (remoteId !in remoteReceiptIds) {
                receiptDao.deleteReceipt(localReceipt.id)
            }
        }
    }

    private suspend fun pruneDeletedBudgets(remoteBudgetIds: Set<Long>) {
        budgetDao.getAllBudgets().forEach { localBudgetWithItems ->
            val localBudget = localBudgetWithItems.budget
            val remoteId = localBudget.remoteId ?: return@forEach
            if (localBudget.syncPending) return@forEach
            if (remoteId !in remoteBudgetIds) {
                budgetDao.deleteBudget(localBudget.id)
            }
        }
    }

    private suspend fun pruneDeletedClients(remoteClientIds: Set<Long>) {
        clientDao.getAllClients().forEach { localClient ->
            val remoteId = localClient.remoteId ?: return@forEach
            if (localClient.syncPending) return@forEach
            if (remoteId !in remoteClientIds) {
                clientDao.deleteById(localClient.id)
            }
        }
    }
}

private fun UserSetupDto.isPlaceholderSetup(): Boolean {
    return name == UserSetupPlaceholders.NAME &&
        document == UserSetupPlaceholders.DOCUMENT &&
        street == UserSetupPlaceholders.STREET &&
        number == UserSetupPlaceholders.NUMBER &&
        neighborhood == UserSetupPlaceholders.NEIGHBORHOOD &&
        city == UserSetupPlaceholders.CITY &&
        state == UserSetupPlaceholders.STATE &&
        phone == UserSetupPlaceholders.PHONE &&
        imagePath == null
}

private fun ClientEntity.toRemoteCreate(): Client {
    return Client(
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = null,
        deleted = false
    )
}

private fun ClientEntity.toRemoteUpdate(): Client {
    return Client(
        id = remoteId ?: 0,
        name = name,
        address = address,
        document = document,
        phone = phone,
        imagePath = null,
        deleted = deleted
    )
}

private fun BudgetWithItems.toRemoteBudget(remoteClientId: Long?, remoteBudgetId: Long?): Budget {
    return Budget(
        id = remoteBudgetId ?: 0,
        clientId = remoteClientId,
        notes = budget.notes,
        validade = budget.validade,
        entrega = budget.entrega,
        createdAt = budget.createdAt,
        updateAt = budget.updateAt,
        total = budget.total,
        status = com.example.florida.domain.model.BudgetStatus.from(budget.status),
        items = items.map {
            com.example.florida.domain.model.Item(
                description = it.description,
                qty = it.qty,
                price = it.price
            )
        }
    )
}

private fun ReceiptWithItems.toRemoteReceipt(
    remoteClientId: Long?,
    remoteBudgetId: Long?,
    remoteReceiptId: Long?,
): Receipt {
    return Receipt(
        id = remoteReceiptId ?: 0,
        clientId = remoteClientId,
        budgetId = remoteBudgetId,
        total = receipt.total,
        date = receipt.date,
        createdAt = receipt.createdAt,
        items = items.map {
            com.example.florida.domain.model.Item(
                description = it.description,
                qty = it.qty,
                price = it.price
            )
        }
    )
}
