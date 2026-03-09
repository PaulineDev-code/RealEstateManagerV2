package com.openclassrooms.realestatemanagerv2.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A thread-safe synchronization utility used to track the initialization status
 * of the local database.
 *
 * In a clean architecture setup where the database is pre-populated asynchronously
 * on the first run, this tracker allows the UI layer and ViewModels to observe
 * the [isReady] flow. This prevents the application from attempting to display
 * empty lists while the initial data seeding is still in progress.
 *
 * This class is a [Singleton] to ensure a consistent state across all
 * components injected via Hilt.
 */
@Singleton
class DatabaseStatusTracker @Inject constructor() {

    /**
     * Internal mutable state indicating whether the database is ready for use.
     */
    private val _isReady = MutableStateFlow(false)

    /**
     * A reactive [StateFlow] that emits `true` once the database has been
     * successfully initialized and pre-populated with data.
     */
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    /**
     * Updates the status to ready.
     * This method is typically called by the database initializer after
     * the pre-population script has finished executing.
     */
    fun notifyPrepopulated() {
        _isReady.value = true
    }
}