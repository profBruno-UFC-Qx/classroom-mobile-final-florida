package com.example.florida.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.florida.domain.model.DashboardSummary
import com.example.florida.persistence.DatabaseProvider
import com.example.florida.persistence.repository.DashboardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    dashboardRepository: DashboardRepository
) : ViewModel() {
    val summary: StateFlow<DashboardSummary> = dashboardRepository.observeDashboardSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardSummary())

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DashboardViewModel(
                        dashboardRepository = DatabaseProvider.getDashboardRepository(appContext)
                    ) as T
                }
            }
        }
    }
}
