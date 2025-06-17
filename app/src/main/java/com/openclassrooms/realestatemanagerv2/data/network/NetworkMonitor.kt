package com.openclassrooms.realestatemanagerv2.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.net.NetworkCapabilities
import androidx.lifecycle.distinctUntilChanged
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

enum class NetworkStatus {
    Available, Unavailable
}

@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context){
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkStatus: Flow<NetworkStatus> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Available)
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.Unavailable)
            }

            override fun onUnavailable() {
                trySend(NetworkStatus.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)

        // Send current state immediately
        val currentNetwork = connectivityManager.activeNetwork
        if (currentNetwork != null &&
            connectivityManager.getNetworkCapabilities(currentNetwork)?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
            trySend(NetworkStatus.Available)
        } else {
            trySend(NetworkStatus.Unavailable)
        }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged() // Only emit when status actually changes
}