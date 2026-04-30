package com.example.florida.model

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.florida.persistence.DatabaseProvider
import com.example.florida.persistence.reposity.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import kotlinx.coroutines.withContext

object SessionManager {
    sealed class SessionState {
        object Loading : SessionState()
        object NoUser : SessionState()
        data class Logged(val user: UserSetup) : SessionState()
        data class Error(val message: String) : SessionState()
    }

    private var _sessionState = mutableStateOf<SessionState>(SessionState.Loading)
    val sessionState: State<SessionState> get() = _sessionState

    private lateinit var userRepository: UserRepository
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        userRepository = DatabaseProvider.getUserRepository(context)
        isInitialized = true

        // Carregar usuário do banco em background
        CoroutineScope(Dispatchers.Main).launch {
            loadUserFromDatabase()
        }
    }

    private suspend fun loadUserFromDatabase() {
        return withContext(Dispatchers.IO) {
            try {
                // Buscar no banco
                val user = userRepository.getUserSetup()

                // Atualizar estado
                _sessionState.value = if (user != null) {
                    SessionState.Logged(user)
                } else {
                    SessionState.NoUser
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _sessionState.value = SessionState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun saveUser(userSetup: UserSetup) {
        // Atualizar estado imediatamente
        _sessionState.value = SessionState.Logged(userSetup)

        // Salvar no banco em background (não bloqueia UI)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userRepository.saveUser(userSetup)
            } catch (e: Exception) {
                e.printStackTrace()
                _sessionState.value = SessionState.Error("Erro ao salvar: ${e.message}")
            }
        }
    }

    fun updateUser(userSetup: UserSetup) {
        // Atualizar estado imediatamente
        _sessionState.value = SessionState.Logged(userSetup)

        // Atualizar no banco em background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userRepository.updateUser(userSetup)
            } catch (e: Exception) {
                e.printStackTrace()
                _sessionState.value = SessionState.Error("Erro ao atualizar: ${e.message}")
            }
        }
    }

    fun clearUser() {
        // Atualizar estado imediatamente
        _sessionState.value = SessionState.NoUser

        // Deletar do banco em background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userRepository.deleteUser()
            } catch (e: Exception) {
                e.printStackTrace()
                _sessionState.value = SessionState.Error("Erro ao fazer logout: ${e.message}")
            }
        }
    }

    suspend fun hasUserSuspend(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                userRepository.hasUser()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun getCurrentUser(): UserSetup? {
        return when (val state = _sessionState.value) {
            is SessionState.Logged -> state.user
            else -> null
        }
    }

    fun retry() {
        _sessionState.value = SessionState.Loading
        CoroutineScope(Dispatchers.Main).launch {
            loadUserFromDatabase()
        }
    }

}
