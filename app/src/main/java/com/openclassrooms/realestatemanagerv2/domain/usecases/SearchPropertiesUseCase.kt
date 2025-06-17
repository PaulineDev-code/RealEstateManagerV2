package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SearchPropertiesUseCase @Inject constructor(private val propertyRepository: PropertyRepository) {

    /**
     *  Search for properties using the given search criterias.
    *@param searchCriterias : PropertySearchCriteria()
    *@return List<Property>
     */

    suspend operator fun invoke(searchCriterias: PropertySearchCriteria): List<Property> {
        val propertiesWithDetails = propertyRepository.searchByCriteria(searchCriterias)
        return propertiesWithDetails.map {
            Property.fromPropertyWithDetails(it)
        }
    }
}