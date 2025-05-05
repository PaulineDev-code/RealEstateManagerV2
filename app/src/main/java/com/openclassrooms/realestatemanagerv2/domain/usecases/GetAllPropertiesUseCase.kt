package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllPropertiesUseCase @Inject constructor(private val propertyRepository: PropertyRepository) {
    suspend operator fun invoke(): Flow<List<Property>> =
        propertyRepository.getAllProperties().map { propertiesWithDetails ->
            propertiesWithDetails.map {
                Property.fromPropertyWithDetails(it)
            }
    }
}
