package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import javax.inject.Inject

/**
 * Use case responsible for retrieving the detailed information of a specific property.
 *
 * This component is central to the Master-Detail pattern, allowing the UI to fetch
 * a single [Property] domain model whenever it needs to populate the detail pane
 * or the edition screen based on a unique identifier.
 */
interface GetPropertyByIdUseCase {

    /**
     * Executes the retrieval process for a single property.
     *
     * @param id The unique identifier (UUID) of the property to be fetched.
     * @return The [Property] domain model matching the provided ID.
     */
    suspend operator fun invoke(id: String): Property
}

/**
 * Standard implementation of [GetPropertyByIdUseCase].
 *
 * It communicates directly with the [PropertyRepository], which serves as the
 * single source of truth for all real estate data.
 *
 * @property propertyRepository The repository used to access the local data source.
 */
class GetPropertyByIdUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : GetPropertyByIdUseCase {

    override suspend operator fun invoke(id: String): Property {
        return propertyRepository.getPropertyById(id)
    }
}