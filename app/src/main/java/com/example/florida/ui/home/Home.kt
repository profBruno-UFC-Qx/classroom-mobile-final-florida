package com.example.florida.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.florida.R
import com.example.florida.model.UserSetup
import com.example.florida.ui.navigation.AppNavigator
import com.example.florida.ui.onboarding.OnboardingScreen
import com.example.florida.ui.session.SessionViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun MainApp() {
    val context = LocalContext.current
    val sessionViewModel: SessionViewModel = viewModel(
        factory = SessionViewModel.factory(context)
    )
    val state by sessionViewModel.sessionState.collectAsState()

    when (state) {
        SessionViewModel.SessionState.Loading -> {
            SplashScreen()
        }
        SessionViewModel.SessionState.NoUser -> {
            OnboardingScreen(
                onSaveUser = sessionViewModel::saveUser
            )
        }
        is SessionViewModel.SessionState.Logged -> {
            AppWithNavigation()
        }
        is SessionViewModel.SessionState.Error -> {
            val errorState = state as SessionViewModel.SessionState.Error
            ErrorScreen(
                message = errorState.message,
                onRetry = { sessionViewModel.retry() }
            )
        }
    }

}

@Composable
fun AppWithNavigation() {
    val navController = rememberNavController()

    AppNavigator(
        navController = navController,
//        onLogout = {
//            navController.navigate(Route.Home.route) {
//                popUpTo(Route.Home.route) { inclusive = true }
//            }
//        }
    )
}

@Composable
fun UserProfileCard(user: UserSetup?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = user?.name ?: stringResource(R.string.name),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.document_label, user?.document ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            user?.imagePath?.let { imagePath ->
                val bitmap = BitmapFactory.decodeFile(imagePath)
                if (bitmap != null) {
                Image(
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true).asImageBitmap(),
                    contentDescription = null,
                )
                }
            }
        }
    }
}
