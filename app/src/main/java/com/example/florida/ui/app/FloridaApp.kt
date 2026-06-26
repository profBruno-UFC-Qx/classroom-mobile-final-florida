package com.example.florida.ui.app

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.florida.domain.model.UserSetup
import com.example.florida.ui.home.ErrorScreen
import com.example.florida.ui.home.SplashScreen
import com.example.florida.ui.navigation.AppNavigator
import com.example.florida.ui.onboarding.OnboardingScreen
import com.example.florida.ui.session.SessionViewModel

@Composable
fun FloridaApp() {
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val state by sessionViewModel.sessionState.collectAsStateWithLifecycle()
    val isRestoringBackup by sessionViewModel.isRestoringBackup.collectAsStateWithLifecycle()
    val restoreErrorMessage by sessionViewModel.restoreErrorMessage.collectAsStateWithLifecycle()

    FloridaAppContent(
        state = state,
        onSaveUser = sessionViewModel::saveUser,
        onRestoreBackup = sessionViewModel::restoreBackup,
        isRestoringBackup = isRestoringBackup,
        restoreErrorMessage = restoreErrorMessage,
        onLogout = sessionViewModel::logout,
        onRetry = sessionViewModel::retry
    )
}

@Composable
private fun FloridaAppContent(
    state: SessionViewModel.SessionState,
    onSaveUser: (UserSetup, Uri?) -> Unit,
    onRestoreBackup: () -> Unit,
    isRestoringBackup: Boolean,
    restoreErrorMessage: String?,
    onLogout: () -> Unit,
    onRetry: () -> Unit,
) {
    when (state) {
        SessionViewModel.SessionState.Loading -> SplashScreen()
        SessionViewModel.SessionState.NoUser -> {
            OnboardingScreen(
                onSaveUser = onSaveUser,
                onRestoreBackup = onRestoreBackup,
                isRestoringBackup = isRestoringBackup,
                restoreErrorMessage = restoreErrorMessage
            )
        }
        is SessionViewModel.SessionState.Logged -> AppWithNavigation(onLogout = onLogout)
        is SessionViewModel.SessionState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun AppWithNavigation(
    onLogout: () -> Unit,
) {
    val navController = rememberNavController()

    AppNavigator(
        navController = navController,
        onLogout = onLogout
    )
}
