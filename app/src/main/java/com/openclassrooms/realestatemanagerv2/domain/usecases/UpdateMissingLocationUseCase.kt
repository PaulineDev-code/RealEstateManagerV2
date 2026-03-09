package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import javax.inject.Inject

/**
 * Use case responsible for resolving missing geographic coordinates across the database.
 *
 * This maintenance component performs a batch geocoding operation to ensure data
 * consistency, especially for properties added while the device was offline.
 * It identifies properties lacking latitude/longitude and attempts to resolve
 * their addresses via the [LocationRepository].
 */
interface UpdateMissingLocationUseCase {

    /**
     * Executes the background geocoding update process.
     *
     * @return The number of unique addresses identified for processing.
     */
    suspend operator fun invoke() : Int
}


/**
 * Standard implementation of [UpdateMissingLocationUseCase].
 *
 * The execution flow follows these steps:
 * 1. Retrieves all unique addresses that currently lack coordinates in the database.
 * 2. Requests geographic coordinates (LatLng) for each address from the location service.
 * 3. Upon successful resolution, updates all matching property records with the new coordinates.
 *
 * @property locationRepository The repository providing both address retrieval and geocoding services.
 */
class UpdateMissingLocationUseCaseImpl @Inject constructor(
    private val locationRepository: LocationRepository
) : UpdateMissingLocationUseCase {

    /**
     * Orchestrates the batch update.
     *
     * Note: This operation should ideally be called when the device is online
     * to ensure the geocoding service is reachable.
     */
    override suspend operator fun invoke(): Int {
        // 1) retrieve addresses to geocode
        val addresses = locationRepository.getAddressesToGeocode()

        // 2) for each address ask for unique geocode
        for (address in addresses) {
            val coordinates = locationRepository.geocodeNowOrNull(address) // return LatLng? or null
            if (coordinates != null) {
                // 3) update every property for this address
                locationRepository.updateLocationByAddress(address, coordinates.latitude, coordinates.longitude)
            }
        }
        return addresses.size
    }
}