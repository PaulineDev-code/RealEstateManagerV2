package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import javax.inject.Inject

interface GetLocationUseCase {
    suspend operator fun invoke(address: String): LatLng?
}

class GetLocationUseCaseImpl @Inject constructor(
    private val locationRepository: LocationRepository
) : GetLocationUseCase {

    override suspend operator fun invoke(address: String): LatLng? =
        locationRepository.geocodeNowOrNull(address)
}