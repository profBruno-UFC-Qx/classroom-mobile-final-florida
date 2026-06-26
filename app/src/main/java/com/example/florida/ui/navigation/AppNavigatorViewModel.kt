package com.example.florida.ui.navigation

import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppNavigatorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageStorageService: ImageStorageService,
) : ViewModel() {
    companion object {
        private const val TAG = "AppNavigatorViewModel"
    }

    val currentUser = userRepository.getUserSetupFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _isSyncingBackup = MutableStateFlow(false)
    val isSyncingBackup: StateFlow<Boolean> = _isSyncingBackup.asStateFlow()

    private val _backupSyncMessage = MutableStateFlow<String?>(null)
    val backupSyncMessage: StateFlow<String?> = _backupSyncMessage.asStateFlow()

    private val actions = MutableSharedFlow<AppAction>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        actions
            .onEach(::handleAction)
            .launchIn(viewModelScope)
    }

    fun updateUser(user: UserSetup) {
        actions.tryEmit(AppAction.UpdateUser(user))
    }

    fun updateUser(
        user: UserSetup,
        imageUri: Uri?,
        onSaved: (String?) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        actions.tryEmit(AppAction.UpdateUserWithImage(user, imageUri, onSaved, onError))
    }

    fun syncBackupNow() {
        actions.tryEmit(AppAction.SyncBackupNow)
    }

    private suspend fun handleAction(action: AppAction) {
        when (action) {
            is AppAction.UpdateUser -> userRepository.updateUser(action.user)
            is AppAction.UpdateUserWithImage -> {
                runCatching {
                    val imagePath = action.imageUri?.let { imageStorageService.saveImage(it) }
                        ?: action.user.imagePath
                    val updatedUser = action.user.copy(imagePath = imagePath)
                    userRepository.updateUser(updatedUser)
                    imagePath
                }.onSuccess(action.onSaved)
                    .onFailure(action.onError)
            }
            AppAction.SyncBackupNow -> {
                _isSyncingBackup.value = true
                _backupSyncMessage.value = null
                try {
                    runCatching { userRepository.syncBackupNow() }
                        .onSuccess {
                            _backupSyncMessage.value = "Backup sincronizado com a nuvem."
                        }
                        .onFailure { throwable ->
                            Log.e(TAG, "Failed to sync backup to remote", throwable)
                            _backupSyncMessage.value =
                                throwable.message ?: "Erro ao sincronizar backup."
                        }
                } finally {
                    _isSyncingBackup.value = false
                }
            }
        }
    }

    private sealed interface AppAction {
        data class UpdateUser(val user: UserSetup) : AppAction
        data class UpdateUserWithImage(
            val user: UserSetup,
            val imageUri: Uri?,
            val onSaved: (String?) -> Unit,
            val onError: (Throwable) -> Unit,
        ) : AppAction
        data object SyncBackupNow : AppAction
    }
}
