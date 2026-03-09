package com.openclassrooms.realestatemanagerv2.domain.model

/**
 * Enumeration representing the real-time connectivity status of the device.
 *
 * This enum is used by the domain layer to monitor network availability,
 * allowing the application to adapt its behavior for online-dependent features
 * such as Geocoding services and Google Maps data fetching.
 */
enum class NetworkStatus {

    /** The device has an active and functional internet connection. */
    Available,

    /** The device is currently offline or the connection is too weak. */
    Unavailable,

    /** The network status cannot be determined at this moment. */
    Unknown
}