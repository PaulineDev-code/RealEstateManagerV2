package com.openclassrooms.realestatemanagerv2.data.network

import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val networkStatus: Flow<NetworkStatus>
}
        