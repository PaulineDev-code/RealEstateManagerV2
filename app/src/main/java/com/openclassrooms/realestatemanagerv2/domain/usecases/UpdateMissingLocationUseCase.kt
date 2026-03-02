package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import javax.inject.Inject

interface UpdateMissingLocationUseCase {
    suspend operator fun invoke() : Int
}

class UpdateMissingLocationUseCaseImpl @Inject constructor(
    private val locationRepository: LocationRepository
) : UpdateMissingLocationUseCase {

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