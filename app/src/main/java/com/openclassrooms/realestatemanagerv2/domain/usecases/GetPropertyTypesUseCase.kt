package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import javax.inject.Inject

interface GetPropertyTypesUseCase {
    suspend operator fun invoke(): List<String>
}

class GetPropertyTypesUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : GetPropertyTypesUseCase {

    override suspend operator fun invoke(): List<String> =
        propertyRepository.getPropertyTypes()
}