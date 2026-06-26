package com.example.florida

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import com.example.florida.persistence.repository.SyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FloridaApplication : Application() {
    @Inject
    lateinit var syncRepository: SyncRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            runCatching { syncRepository.syncPendingChanges() }
                .onFailure { throwable ->
                    Log.e("FloridaApplication", "Initial sync failed", throwable)
                }
        }

        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        runCatching {
            connectivityManager?.registerDefaultNetworkCallback(
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        applicationScope.launch {
                            runCatching { syncRepository.syncPendingChanges() }
                                .onFailure { throwable ->
                                    Log.e("FloridaApplication", "Network recovery sync failed", throwable)
                                }
                        }
                    }
                }
            )
        }.onFailure { throwable ->
            Log.w("FloridaApplication", "Unable to register network callback", throwable)
        }
    }
}
