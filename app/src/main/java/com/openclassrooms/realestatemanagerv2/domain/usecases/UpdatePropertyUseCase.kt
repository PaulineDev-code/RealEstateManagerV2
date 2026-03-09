package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case responsible for updating the details of an existing real estate property.
 *
 * This component handles the business logic for property modifications,
 * ensuring that updated [Property] data is correctly passed to the [PropertyRepository].
 */
interface UpdatePropertyUseCase {

    /**
     * Executes the property update process.
     *
     * @param property The [Property] domain model containing the updated information.
     */
    suspend operator fun invoke(property: Property): Unit
}

/**
 * Standard implementation of [UpdatePropertyUseCase].
 *
 * It ensures the database update is performed on the [Dispatchers.IO] context
 * to prevent blocking the UI thread during data persistence.
 *
 * @property propertyRepository The repository used to access and modify property data.
 */
class UpdatePropertyUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : UpdatePropertyUseCase {

    /**
     * Updates the property in the repository using the IO dispatcher.
     */
    override suspend operator fun invoke(property: Property): Unit = withContext(Dispatchers.IO) {
        propertyRepository.updateProperty(property)
    }
}