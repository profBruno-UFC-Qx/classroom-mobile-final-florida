package com.example.florida.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.florida.domain.model.DashboardSummary
import com.example.florida.persistence.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    dashboardRepository: DashboardRepository
) : ViewModel() {
    val summary: StateFlow<DashboardSummary> = dashboardRepository.observeDashboardSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardSummary())
}
