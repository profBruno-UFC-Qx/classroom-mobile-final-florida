package com.example.florida.ui.AppNavigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource


@Composable
fun AppBottomBar(
    currentRoute: String?,
    navActions: NavigationActions
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
    ) {
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    when (item) {
                        BottomNavItem.HOME -> navActions.navigateToHome()
                        BottomNavItem.BUDGET -> navActions.navigateTobudget()
                        BottomNavItem.CLIENT -> navActions.navigateToClient()
                        BottomNavItem.RECEIPT -> navActions.navigateToReceipt()
                        BottomNavItem.SETTINGS -> navActions.navigateToSettings()
                    }
                },
                icon = item.icon,
                label = { Text(stringResource(item.label)) },
                alwaysShowLabel = true
            )
        }
    }
}