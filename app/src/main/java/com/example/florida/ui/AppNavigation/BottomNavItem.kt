package com.example.florida.ui.AppNavigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.florida.R

enum class BottomNavItem(
    val route: String,
    @StringRes val label: Int,
    val icon: @Composable () -> Unit,
) {
    HOME(
        route = Route.Home.route,
        label = R.string.home,
        icon = {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = stringResource(R.string.home)
            )
        }
    ),
    CLIENT(
        route = Route.Client.route,
        label = R.string.client,
        icon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.person_icon)
            )
        }
    ),
    BUDGET(
        route = Route.Budget.route,
        label = R.string.budget,
        icon = {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = stringResource(R.string.person_icon)
            )
        }
    ),
    RECEIPT(
        route = Route.Receipt.route,
        label = R.string.receipt,
        icon = {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = stringResource(R.string.receipt_icon)
            )
        }
    ),
    SETTINGS(
        route = Route.Settings.route,
        label = R.string.settings,
        icon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_icon)
            )
        }
    )
}
