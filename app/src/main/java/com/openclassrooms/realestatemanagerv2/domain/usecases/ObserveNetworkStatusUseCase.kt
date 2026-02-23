package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveNetworkStatusUseCase {
    operator fun invoke(): Flow<NetworkStatus>
}

class ObserveNetworkStatusUseCaseImpl @Inject constructor(
    private val locationRepository: LocationRepository
) : ObserveNetworkStatusUseCase {

    override operator fun invoke(): Flow<NetworkStatus> =
        locationRepository.observeNetworkStatus()
}