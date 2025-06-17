package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.repositories.LocationRepository
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(address: String): LatLng? =
        locationRepository.geocodeNowOrNull(address)
}