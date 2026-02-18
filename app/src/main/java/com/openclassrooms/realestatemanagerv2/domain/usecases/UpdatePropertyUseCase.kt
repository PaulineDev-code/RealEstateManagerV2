package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface UpdatePropertyUseCase {
    suspend operator fun invoke(property: Property): Unit
}

class UpdatePropertyUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : UpdatePropertyUseCase {

    override suspend operator fun invoke(property: Property): Unit = withContext(Dispatchers.IO) {
        propertyRepository.updateProperty(property)
    }
}