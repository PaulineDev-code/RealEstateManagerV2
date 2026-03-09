package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case responsible for adding a new real estate property to the system.
 *
 * This component encapsulates the business logic for property creation,
 * delegating the actual persistence to the [PropertyRepository].
 * It ensures that property data is handled correctly before being stored.
 */
interface AddPropertyUseCase {

    /**
     * Executes the property creation process.
     *
     * @param property The [Property] domain model to be persisted.
     */
    suspend operator fun invoke(property: Property): Unit
}

/**
 * standard implementation of [AddPropertyUseCase].
 *
 * It ensures that the database operation is executed on the IO dispatcher
 * to avoid blocking the main thread.
 */
class AddPropertyUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : AddPropertyUseCase {

    override suspend operator fun invoke(property: Property): Unit = withContext(Dispatchers.IO) {
        propertyRepository.insertProperty(property)
    }
}