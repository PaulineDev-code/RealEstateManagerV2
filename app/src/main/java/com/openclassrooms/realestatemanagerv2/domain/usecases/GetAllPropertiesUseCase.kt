package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import javax.inject.Inject

/**
 * Use case responsible for retrieving the complete collection of real estate properties.
 *
 * This component acts as the primary data fetcher for the property list screens,
 * abstracting the [PropertyRepository] and providing the Domain layer with
 * the full dataset from the local storage.
 */
interface GetAllPropertiesUseCase {

    /**
     * Executes the retrieval process for all properties.
     *
     * @return A list of all [Property] domain models available in the system.
     */
    suspend operator fun invoke(): List<Property>
}

/**
 * Standard implementation of [GetAllPropertiesUseCase].
 *
 * It communicates with the [PropertyRepository] to fetch the data.
 * The execution context is typically managed by the repository or the caller
 * to ensure thread safety and non-blocking UI.
 *
 * @property propertyRepository The repository used as the single source of truth for property data.
 */
class GetAllPropertiesUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : GetAllPropertiesUseCase {

    override suspend operator fun invoke(): List<Property> =
        propertyRepository.getAllProperties()
}