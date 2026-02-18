package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import javax.inject.Inject

interface GetPropertyByIdUseCase {
    suspend operator fun invoke(id: String): Property
}

class GetPropertyByIdUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : GetPropertyByIdUseCase {

    override suspend operator fun invoke(id: String): Property {
        val propertyWithDetails = propertyRepository.getPropertyById(id)
        return Property.fromPropertyWithDetails(propertyWithDetails)
    }
}