package com.openclassrooms.realestatemanagerv2.di

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Installe les fournisseurs de sécurité réseau les plus récents pour les anciens Android
        // afin de résoudre les problèmes de certificats SSL/TLS.
        installTlsProvider()
    }

    @OptIn(UnstableApi::class)
    private fun installTlsProvider() {
        try {
            ProviderInstaller.installIfNeeded(applicationContext)
            Log.d("MyApplication", "TLS Provider installed successfully.")
        } catch (e: Exception) {
            // Si les Play Services ne sont pas disponibles ou à jour, cela peut échouer.
            // On peut tenter de le corriger.
            Log.e("MyApplication", "Failed to install TLS Provider: ${e.message}")
            GoogleApiAvailability.getInstance()
                .showErrorNotification(applicationContext, e.hashCode())
        }
    }
}