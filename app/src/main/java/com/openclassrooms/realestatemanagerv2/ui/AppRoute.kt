package com.openclassrooms.realestatemanagerv2.ui

import kotlinx.serialization.Serializable

sealed interface AppRoute

sealed interface TopLevelRoute : AppRoute

@Serializable
data object Home : TopLevelRoute

@Serializable
data object Search : TopLevelRoute

@Serializable
data object Map : TopLevelRoute

@Serializable
data class Details(
    val propertyId: String
) : AppRoute

@Serializable
data object AddProperty : AppRoute

@Serializable
data class EditProperty(
    val propertyId: String
) : AppRoute