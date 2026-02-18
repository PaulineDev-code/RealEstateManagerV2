package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AddPropertyUseCase {
    suspend operator fun invoke(property: Property): Unit
}

class AddPropertyUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : AddPropertyUseCase {

    override suspend operator fun invoke(property: Property): Unit = withContext(Dispatchers.IO) {
        propertyRepository.insertProperty(property)
    }
}