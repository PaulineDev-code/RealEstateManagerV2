package com.openclassrooms.realestatemanagerv2.domain.repositories

import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface providing geolocation services and network monitoring.
 *
 * This repository is the bridge between the application's domain layer and
 * external connectivity services, handling both geocoding operations and
 * connectivity status tracking.
 */
interface LocationRepository {

    /**
     * Retrieves a list of physical addresses from the local database that
     * do not yet have geographic coordinates (latitude/longitude).
     *
     * @return A list of address strings pending geocoding.
     */
    suspend fun getAddressesToGeocode(): List<String>

    /**
     * Updates a specific address entry in the local data source with its
     * resolved geographic coordinates.
     *
     * @param address The primary address string used as the key.
     * @param latitude The resolved latitude.
     * @param longitude The resolved longitude.
     */
    suspend fun updateLocationByAddress(address: String, latitude: Double, longitude: Double)

    /**
     * Performs a direct geocoding request to resolve an address into [LatLng].
     *
     * @param address The address string to resolve.
     * @return The geographic coordinates as [LatLng], or null if the address
     * cannot be resolved or an error occurs.
     */
    suspend fun geocodeNowOrNull(address: String): LatLng?

    /**
     * Provides a real-time stream of the device's connectivity status.
     *
     * This allows the UI and other components to react to network changes
     * (e.g., notifying the user that geocoding is unavailable offline).
     *
     * @return A [Flow] emitting the current [NetworkStatus].
     */
    fun observeNetworkStatus(): Flow<NetworkStatus>
}


