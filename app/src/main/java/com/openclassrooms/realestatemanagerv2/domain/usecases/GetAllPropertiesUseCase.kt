package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import javax.inject.Inject

interface GetAllPropertiesUseCase {
    suspend operator fun invoke(): List<Property>
}

class GetAllPropertiesUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : GetAllPropertiesUseCase {

    override suspend operator fun invoke(): List<Property> =
        propertyRepository.getAllProperties()
}