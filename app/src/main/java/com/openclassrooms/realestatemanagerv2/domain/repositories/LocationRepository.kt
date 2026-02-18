package com.openclassrooms.realestatemanagerv2.domain.repositories

import com.google.android.gms.maps.model.LatLng

interface LocationRepository {
    suspend fun getAddressesToGeocode(): List<String>
    suspend fun updateLocationByAddress(address: String, latitude: Double, longitude: Double)
    suspend fun geocodeNowOrNull(address: String): LatLng?
}


