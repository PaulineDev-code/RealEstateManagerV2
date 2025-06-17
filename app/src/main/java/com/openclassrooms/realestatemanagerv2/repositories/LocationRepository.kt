package com.openclassrooms.realestatemanagerv2.repositories

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val propertyDAO: PropertyLocalDAO
) {
    private val geocoder by lazy { Geocoder(context, Locale.getDefault()) }

    suspend fun getAddressesToGeocode(): List<String> =
        propertyDAO.getAddressesWithoutLatLng()

    suspend fun updateLocationByAddress(
        address: String,
        latitude: Double,
        longitude: Double
    ): Unit = propertyDAO.updateLocationByAddress(address, latitude, longitude)

    /**
     * Essaie de géocoder [address] tout de suite si réseau dispo.
     * @return LatLng si trouvé, ou null si hors-ligne, adresse introuvable ou erreur.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    suspend fun geocodeNowOrNull(address: String): LatLng? = withContext(Dispatchers.IO) {
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
                                continuation.resume(first?.let { LatLng(it.latitude, it.longitude) }) {}
                            }
                            override fun onError(errorMessage: String?) {
                                continuation.resume(null) {}
                            }
                        }
                    )
                    continuation.invokeOnCancellation {
                        // si la coroutine est annulée, rien à nettoyer de particulier
                    }
                }
            } else {
                geocoder.getFromLocationName(address, 1)
                    ?.firstOrNull()
                    ?.let { LatLng(it.latitude, it.longitude) }
            }
        } catch (e: IOException) {
            // p.ex. pas de net ou service down → on renvoie null pour différer
            null
        } catch (e: Exception) {
            // toute autre erreur (adresse invalide, bug Geocoder…) → null
            null
        }
    }

}