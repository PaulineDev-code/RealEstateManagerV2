package com.openclassrooms.realestatemanagerv2.domain.repositories

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria

interface PropertyRepository {
    suspend fun insertProperty(property: Property): Unit
    suspend fun updateProperty(property: Property): Unit
    suspend fun getAllProperties(): List<Property>
    suspend fun getPropertyById(id: String): Property
    suspend fun getPropertyTypes(): List<String>
    suspend fun searchByCriteria(criteria: PropertySearchCriteria): List<Property>
}