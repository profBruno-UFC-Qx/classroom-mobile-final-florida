package com.example.florida.persistence.repository

import com.example.florida.model.UserSetup
import com.example.florida.persistence.entity.UserEntity
import com.example.florida.persistence.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    // Flow para observar mudanças em tempo real
    fun getUserSetupFlow(): Flow<UserSetup?> {
        return userDao.getUserSetupFlow().map { userEntity ->
            userEntity?.toUserSetup()
        }
    }

    // Flow para saber se usuário existe
    fun hasUserFlow(): Flow<Boolean> {
        return userDao.hasUser()
    }

    // Obter usuário de forma síncrona
    suspend fun getUserSetup(): UserSetup? {
        return withContext(Dispatchers.IO) {
            userDao.getUserSetup()?.toUserSetup()
        }
    }

    // Verificar se existe usuário
    suspend fun hasUser(): Boolean {
        return withContext(Dispatchers.IO) {
            userDao.hasUserSuspend()
        }
    }

    // Salvar novo usuário
    suspend fun saveUser(userSetup: UserSetup) {
        return withContext(Dispatchers.IO) {
            val userEntity = userSetup.toUserEntity()
            userDao.insertUser(userEntity)
        }
    }

    // Atualizar usuário existente
    suspend fun updateUser(userSetup: UserSetup) {
        return withContext(Dispatchers.IO) {
            val userEntity = userSetup.toUserEntity()
            userDao.updateUser(userEntity)
        }
    }

    // Deletar usuário (logout)
    suspend fun deleteUser() {
        return withContext(Dispatchers.IO) {
            userDao.deleteUser()
        }
    }

    // ==================== EXTENSION FUNCTIONS ====================
    private fun UserEntity.toUserSetup(): UserSetup {
        return UserSetup(
            name = this.name,
            document = this.document,
            street = this.street,
            number = this.number,
            neighborhood = this.neighborhood,
            city = this.city,
            state = this.state,
            phone = this.phone,
            imagePath = this.imagePath
        )
    }

    private fun UserSetup.toUserEntity(): UserEntity {
        return UserEntity(
            id = 1,
            name = this.name,
            document = this.document,
            street = this.street,
            number = this.number,
            neighborhood = this.neighborhood,
            city = this.city,
            state = this.state,
            phone = this.phone,
            imagePath = this.imagePath,
            updatedAt = System.currentTimeMillis()
        )
    }
}
