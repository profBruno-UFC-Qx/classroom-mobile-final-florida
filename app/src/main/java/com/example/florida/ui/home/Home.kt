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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.florida.model.SessionManager
import com.example.florida.model.UserSetup
import com.example.florida.ui.AppNavigation.AppNavigator
import com.example.florida.ui.onboarding.OnboardingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun MainApp() {
    val state = SessionManager.sessionState.value

    when (state) {
        SessionManager.SessionState.Loading -> {
            SplashScreen()
        }
        SessionManager.SessionState.NoUser -> {
            OnboardingScreen( )
        }
        is SessionManager.SessionState.Logged -> {
            AppWithNavigation()
        }
        is SessionManager.SessionState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry = { SessionManager.retry() }
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
                text = user?.name ?: "Nome do usuário",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Documento: ${user?.document}",
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
