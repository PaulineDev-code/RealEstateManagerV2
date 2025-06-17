package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.repositories.LocationRepository
import javax.inject.Inject

class UpdateMissingLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke() {
        // 1) récupère les adresses à géocoder
        val addresses = locationRepository.getAddressesToGeocode()

        // 2) pour chaque adresse, demande un unique geocode
        for (address in addresses) {
            val coordinates = locationRepository.geocodeNowOrNull(address) // renvoie LatLng? ou null
            if (coordinates != null) {
                // 3) met à jour toutes les propriétés de cette adresse
                locationRepository.updateLocationByAddress(address, coordinates.latitude, coordinates.longitude)
            }
        }
    }
}