package com.example.florida.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.florida.model.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val user = SessionManager.getCurrentUser()
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        UserProfileCard(user)
        Spacer(modifier = Modifier.height(16.dp))
        Dashboard()
    }
}