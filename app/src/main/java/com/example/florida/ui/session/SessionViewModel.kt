package com.example.florida.ui.session

import android.util.Log
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.florida.domain.model.UserSetup
import com.example.florida.persistence.ImageStorageService
import com.example.florida.persistence.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageStorageService: ImageStorageService,
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

    private val _isRestoringBackup = MutableStateFlow(false)
    val isRestoringBackup: StateFlow<Boolean> = _isRestoringBackup.asStateFlow()
    private val _restoreErrorMessage = MutableStateFlow<String?>(null)
    val restoreErrorMessage: StateFlow<String?> = _restoreErrorMessage.asStateFlow()

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

    fun saveUser(userSetup: UserSetup, imageUri: Uri?) {
        actions.tryEmit(SessionAction.SaveUserWithImage(userSetup, imageUri))
    }

    fun updateUser(userSetup: UserSetup) {
        actions.tryEmit(SessionAction.UpdateUser(userSetup))
    }

    fun retry() {
        actions.tryEmit(SessionAction.Refresh)
    }

    fun restoreBackup() {
        actions.tryEmit(SessionAction.RestoreBackup)
    }

    fun logout() {
        actions.tryEmit(SessionAction.Logout)
    }

    private suspend fun handleAction(action: SessionAction) {
        when (action) {
            is SessionAction.SaveUser -> userRepository.saveUser(action.userSetup)
            is SessionAction.SaveUserWithImage -> {
                val imagePath = action.imageUri?.let { imageStorageService.saveImage(it) }
                userRepository.saveUser(action.userSetup.copy(imagePath = imagePath))
            }
            is SessionAction.UpdateUser -> userRepository.updateUser(action.userSetup)
            SessionAction.RestoreBackup -> {
                _isRestoringBackup.value = true
                _restoreErrorMessage.value = null
                try {
                    runCatching {
                        userRepository.restoreFromRemoteBackup()
                    }.onFailure { throwable ->
                        Log.e("SessionViewModel", "Failed to restore remote backup", throwable)
                        _restoreErrorMessage.value = throwable.message ?: "Erro ao restaurar backup"
                    }
                } finally {
                    _isRestoringBackup.value = false
                }
            }
            SessionAction.Logout -> userRepository.deleteUser()
            SessionAction.Refresh -> userRepository.getUserSetup()
        }
    }

    private sealed interface SessionAction {
        data class SaveUser(val userSetup: UserSetup) : SessionAction
        data class SaveUserWithImage(val userSetup: UserSetup, val imageUri: Uri?) : SessionAction
        data class UpdateUser(val userSetup: UserSetup) : SessionAction
        data object RestoreBackup : SessionAction
        data object Logout : SessionAction
        data object Refresh : SessionAction
    }
}
