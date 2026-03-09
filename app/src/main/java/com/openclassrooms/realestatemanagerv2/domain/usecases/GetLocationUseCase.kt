package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import javax.inject.Inject

/**
 * Use case responsible for resolving a physical address into geographic coordinates. *
 * It acts as a domain-level bridge to the geocoding service, allowing the application
 * to transform user-entered addresses into [LatLng] objects for map positioning
 * and distance calculations.
 */
interface GetLocationUseCase {

    /**
     * Executes the geocoding request for a specific address.
     *
     * @param address The full physical address string provided by the user.
     * @return A [LatLng] object containing latitude and longitude if resolved,
     * or null if the service fails or the address is invalid.
     */
    suspend operator fun invoke(address: String): LatLng?
}

/**
 * Standard implementation of [GetLocationUseCase].
 *
 * It delegates the external service call to the [LocationRepository],
 * ensuring that the domain layer remains agnostic of the specific
 * Geocoding provider (e.g., Google Maps Geocoding API or Android Geocoder).
 *
 * @property locationRepository The repository providing access to location-based services.
 */
class GetLocationUseCaseImpl @Inject constructor(
    private val locationRepository: LocationRepository
) : GetLocationUseCase {

    override suspend operator fun invoke(address: String): LatLng? =
        locationRepository.geocodeNowOrNull(address)
}