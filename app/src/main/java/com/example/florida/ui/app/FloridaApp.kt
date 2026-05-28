package com.example.florida.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
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
    val state by sessionViewModel.sessionState.collectAsState()

    when (state) {
        SessionViewModel.SessionState.Loading -> SplashScreen()
        SessionViewModel.SessionState.NoUser -> {
            OnboardingScreen(
                onSaveUser = sessionViewModel::saveUser
            )
        }
        is SessionViewModel.SessionState.Logged -> AppWithNavigation()
        is SessionViewModel.SessionState.Error -> {
            val errorState = state as SessionViewModel.SessionState.Error
            ErrorScreen(
                message = errorState.message,
                onRetry = sessionViewModel::retry
            )
        }
    }
}

@Composable
private fun AppWithNavigation() {
    val navController = rememberNavController()

    AppNavigator(navController = navController)
}
