package com.example.florida.ui.home

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.florida.model.Budget
import com.example.florida.model.Client
import com.example.florida.model.Receipt
import com.example.florida.model.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    clients: List<Client>,
    budgets: List<Budget>,
    receipts: List<Receipt>,
    onOpenClients: () -> Unit,
    onOpenBudgets: () -> Unit,
    onOpenReceipts: () -> Unit,
    onCreateBudget: () -> Unit,
    onCreateReceipt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = SessionManager.getCurrentUser()
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        UserProfileCard(user)
        Spacer(modifier = Modifier.height(16.dp))
        Dashboard(
            clients = clients,
            budgets = budgets,
            receipts = receipts,
            onOpenClients = onOpenClients,
            onOpenBudgets = onOpenBudgets,
            onOpenReceipts = onOpenReceipts,
            onCreateBudget = onCreateBudget,
            onCreateReceipt = onCreateReceipt
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
