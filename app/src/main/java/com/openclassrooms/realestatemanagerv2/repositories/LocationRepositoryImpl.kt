package com.openclassrooms.realestatemanagerv2.repositories

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.data.network.NetworkMonitor
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val propertyDAO: PropertyLocalDAO,
    private val networkMonitor: NetworkMonitor
) : LocationRepository {
    private val geocoder by lazy { Geocoder(context, Locale.getDefault()) }

    override suspend fun getAddressesToGeocode(): List<String> =
        propertyDAO.getAddressesWithoutLatLng()

    override suspend fun updateLocationByAddress(
        address: String,
        latitude: Double,
        longitude: Double
    ): Unit = propertyDAO.updateLocationByAddress(address, latitude, longitude)

    /**
     * Try to resolve [address] by now if network is available.
     * @return LatLng if found, or null if offline, adress not found or error.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override suspend fun geocodeNowOrNull(address: String): LatLng? = withContext(Dispatchers.IO) {
        if (address.isBlank()) {
            return@withContext null
        }

        return@withContext try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine<LatLng?> { continuation ->
                    geocoder.getFromLocationName(
                        address, 1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(results: MutableList<Address>) {
                                val first = results.firstOrNull()
                                continuation.resume(first?.let {
                                    LatLng(
                                        it.latitude,
                                        it.longitude
                                    )
                                }) { cause, _, _ -> }
                            }
                            override fun onError(errorMessage: String?) {
                                continuation.resume(null) { cause, _, _ -> }
                            }
                        }
                    )
                    continuation.invokeOnCancellation {
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(address, 1, )
                    ?.firstOrNull()
                    ?.let { LatLng(it.latitude, it.longitude) }
            }
        } catch (e: IOException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    override fun observeNetworkStatus(): Flow<NetworkStatus> {
        return networkMonitor.networkStatus
    }

}