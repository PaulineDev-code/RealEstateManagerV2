package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.repositories.LocationRepository
import javax.inject.Inject

class UpdateMissingLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke() {
        // 1) retrieve addresses to geocode
        val addresses = locationRepository.getAddressesToGeocode()

        // 2) for each address ask for unique geocode
        for (address in addresses) {
            val coordinates = locationRepository.geocodeNowOrNull(address) // renvoie LatLng? ou null
            if (coordinates != null) {
                // 3) update every estates for this address
                locationRepository.updateLocationByAddress(address, coordinates.latitude, coordinates.longitude)
            }
        }
    }
}