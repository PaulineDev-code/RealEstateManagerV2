package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case responsible for monitoring the device's real-time network connectivity status.
 *
 * It provides a continuous [Flow] of [NetworkStatus], allowing the UI and other
 * components to reactively update based on whether the device is Online, Offline,
 * or in an Unknown state.
 */
interface ObserveNetworkStatusUseCase {

    /**
     * Executes the network status observation.
     *
     * @return A [Flow] emitting the current [NetworkStatus].
     */
    operator fun invoke(): Flow<NetworkStatus>
}

/**
 * Standard implementation of [ObserveNetworkStatusUseCase].
 *
 * It delegates the observation logic to the [LocationRepository], which
 * interfaces with the system's ConnectivityManager.
 *
 * @property locationRepository The repository providing the network status stream.
 */
class ObserveNetworkStatusUseCaseImpl @Inject constructor(
    private val locationRepository: LocationRepository
) : ObserveNetworkStatusUseCase {

    /**
     * Returns the network status flow from the repository.
     */
    override operator fun invoke(): Flow<NetworkStatus> =
        locationRepository.observeNetworkStatus()
}