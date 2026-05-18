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
import com.example.florida.domain.model.DashboardSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    summary: DashboardSummary,
    onOpenClients: () -> Unit,
    onOpenBudgets: () -> Unit,
    onOpenReceipts: () -> Unit,
    onCreateBudget: () -> Unit,
    onCreateReceipt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Dashboard(
            summary = summary,
            onOpenClients = onOpenClients,
            onOpenBudgets = onOpenBudgets,
            onOpenReceipts = onOpenReceipts,
            onCreateBudget = onCreateBudget,
            onCreateReceipt = onCreateReceipt
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
