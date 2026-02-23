package com.openclassrooms.realestatemanagerv2.domain.repositories

import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getAddressesToGeocode(): List<String>
    suspend fun updateLocationByAddress(address: String, latitude: Double, longitude: Double)
    suspend fun geocodeNowOrNull(address: String): LatLng?
    fun observeNetworkStatus(): Flow<NetworkStatus>
}


