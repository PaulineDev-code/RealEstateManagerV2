package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetAllPropertiesUseCase @Inject constructor(private val propertyRepository: PropertyRepository) {
    suspend operator fun invoke(): List<Property> =
        propertyRepository.getAllProperties().map { propertyWithDetails ->
            Property.fromPropertyWithDetails(propertyWithDetails)
        }
}
