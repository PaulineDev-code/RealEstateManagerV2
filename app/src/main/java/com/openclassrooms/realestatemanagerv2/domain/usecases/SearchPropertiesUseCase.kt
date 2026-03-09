package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import javax.inject.Inject

/**
 * Use case responsible for executing complex search queries on the property database.
 *
 * This component bridges the UI search criteria with the [PropertyRepository]
 * to perform multi-parameter filtering (price, area, rooms, media count, etc.).
 */
interface SearchPropertiesUseCase {

    /**
     * Executes the search operation based on the provided criteria.
     *
     * @param searchCriterias An object containing all filter parameters.
     * @return A list of [Property] domain models matching the specified criteria.
     */
    suspend operator fun invoke(searchCriterias: PropertySearchCriteria): List<Property>
}

/**
 * Standard implementation of [SearchPropertiesUseCase].
 *
 * Delegates the search execution to the [PropertyRepository], which translates
 * the criteria into a database query.
 *
 * @property propertyRepository The single source of truth for property data.
 */
class SearchPropertiesUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : SearchPropertiesUseCase {

    override suspend operator fun invoke(searchCriterias: PropertySearchCriteria): List<Property> {
        return propertyRepository.searchByCriteria(searchCriterias)
    }
}