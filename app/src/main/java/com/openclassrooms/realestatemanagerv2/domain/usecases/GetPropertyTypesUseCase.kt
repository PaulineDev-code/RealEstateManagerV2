package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.repositories.AgentRepository
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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