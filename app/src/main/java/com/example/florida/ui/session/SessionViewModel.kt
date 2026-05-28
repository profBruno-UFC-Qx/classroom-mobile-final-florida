package com.example.florida.ui.session

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.florida.persistence.DatabaseProvider
import com.example.florida.model.UserSetup
import com.example.florida.persistence.repository.UserRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class SessionViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    sealed class SessionState {
        data object Loading : SessionState()
        data object NoUser : SessionState()
        data class Logged(val user: UserSetup) : SessionState()
        data class Error(val message: String) : SessionState()
    }

    val sessionState: StateFlow<SessionState> = userRepository.getUserSetupFlow()
        .map<UserSetup?, SessionState> { user ->
            if (user == null) SessionState.NoUser else SessionState.Logged(user)
        }
        .catch { exception ->
            emit(SessionState.Error(exception.message ?: "Erro desconhecido"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionState.Loading)

    private val actions = MutableSharedFlow<SessionAction>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        actions
            .onEach(::handleAction)
            .launchIn(viewModelScope)
    }

    fun saveUser(userSetup: UserSetup) {
        actions.tryEmit(SessionAction.SaveUser(userSetup))
    }

    fun updateUser(userSetup: UserSetup) {
        actions.tryEmit(SessionAction.UpdateUser(userSetup))
    }

    fun retry() {
        actions.tryEmit(SessionAction.Refresh)
    }

    fun logout() {
        actions.tryEmit(SessionAction.Logout)
    }

    private suspend fun handleAction(action: SessionAction) {
        when (action) {
            is SessionAction.SaveUser -> userRepository.saveUser(action.userSetup)
            is SessionAction.UpdateUser -> userRepository.updateUser(action.userSetup)
            SessionAction.Logout -> userRepository.deleteUser()
            SessionAction.Refresh -> userRepository.getUserSetup()
        }
    }

    private sealed interface SessionAction {
        data class SaveUser(val userSetup: UserSetup) : SessionAction
        data class UpdateUser(val userSetup: UserSetup) : SessionAction
        data object Logout : SessionAction
        data object Refresh : SessionAction
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SessionViewModel(
                        userRepository = DatabaseProvider.getUserRepository(appContext)
                    ) as T
                }
            }
        }
    }
}
