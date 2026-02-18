package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import javax.inject.Inject

interface SearchPropertiesUseCase {
    suspend operator fun invoke(searchCriterias: PropertySearchCriteria): List<Property>
}

/**
 *  Search for properties using the given search criterias.
 *@param searchCriterias : PropertySearchCriteria()
 *@return List<Property>
 */
class SearchPropertiesUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : SearchPropertiesUseCase {

    override suspend operator fun invoke(searchCriterias: PropertySearchCriteria): List<Property> {
        return propertyRepository.searchByCriteria(searchCriterias)
    }
}