package com.example.florida.persistence.repository

import com.example.florida.domain.model.UserSetup
import com.example.florida.persistence.dao.UserDao
import com.example.florida.persistence.mapper.isPlaceholderSetup
import com.example.florida.persistence.mapper.toDomain
import com.example.florida.persistence.mapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val syncRepository: SyncRepository,
) {

    // Flow para observar mudanças em tempo real
    fun getUserSetupFlow(): Flow<UserSetup?> {
        return userDao.getUserSetupFlow().map { userEntity ->
            userEntity
                ?.takeUnless { it.isPlaceholderSetup() }
                ?.toDomain()
        }
    }

    // Flow para saber se usuário existe
    fun hasUserFlow(): Flow<Boolean> {
        return userDao.hasUser()
    }

    // Obter usuário de forma síncrona
    suspend fun getUserSetup(): UserSetup? {
        return withContext(Dispatchers.IO) {
            userDao.getUserSetup()
                ?.takeUnless { it.isPlaceholderSetup() }
                ?.toDomain()
        }
    }

    // Verificar se existe usuário
    suspend fun hasUser(): Boolean {
        return withContext(Dispatchers.IO) {
            userDao.getUserSetup()?.isPlaceholderSetup() == false
        }
    }

    // Salvar novo usuário
    suspend fun saveUser(userSetup: UserSetup) {
        return withContext(Dispatchers.IO) {
            userDao.insertUser(userSetup.toEntity())
            runCatching { syncRepository.syncPendingChanges() }
        }
    }

    // Atualizar usuário existente
    suspend fun updateUser(userSetup: UserSetup) {
        return withContext(Dispatchers.IO) {
            userDao.insertUser(userSetup.toEntity())
            runCatching { syncRepository.syncPendingChanges() }
        }
    }

    suspend fun restoreFromRemoteBackup() {
        return withContext(Dispatchers.IO) {
            syncRepository.restoreFromRemoteBackup()
        }
    }

    suspend fun syncBackupNow() {
        return withContext(Dispatchers.IO) {
            syncRepository.pushFullStateToRemote()
        }
    }

    // Deletar usuário (logout)
    suspend fun deleteUser() {
        return withContext(Dispatchers.IO) {
            userDao.deleteUser()
        }
    }
}
