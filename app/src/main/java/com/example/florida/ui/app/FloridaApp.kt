package com.example.florida.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.florida.domain.model.UserSetup
import com.example.florida.ui.home.ErrorScreen
import com.example.florida.ui.home.SplashScreen
import com.example.florida.ui.navigation.AppNavigator
import com.example.florida.ui.onboarding.OnboardingScreen
import com.example.florida.ui.session.SessionViewModel

@Composable
fun FloridaApp() {
    val context = LocalContext.current
    val sessionViewModel: SessionViewModel = viewModel(
        factory = SessionViewModel.factory(context)
    )
    val state by sessionViewModel.sessionState.collectAsStateWithLifecycle()

    FloridaAppContent(
        state = state,
        onSaveUser = sessionViewModel::saveUser,
        onLogout = sessionViewModel::logout,
        onRetry = sessionViewModel::retry
    )
}

@Composable
private fun FloridaAppContent(
    state: SessionViewModel.SessionState,
    onSaveUser: (UserSetup) -> Unit,
    onLogout: () -> Unit,
    onRetry: () -> Unit,
) {
    when (state) {
        SessionViewModel.SessionState.Loading -> SplashScreen()
        SessionViewModel.SessionState.NoUser -> {
            OnboardingScreen(
                onSaveUser = onSaveUser
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
