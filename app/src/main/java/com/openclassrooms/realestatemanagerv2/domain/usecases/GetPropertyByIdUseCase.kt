package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPropertyByIdUseCase @Inject constructor(private val propertyRepository: PropertyRepository) {
    suspend operator fun invoke(id: String): Property {
        val propertyWithDetails = propertyRepository.getPropertyById(id)
        return Property.fromPropertyWithDetails(propertyWithDetails)
    }
}