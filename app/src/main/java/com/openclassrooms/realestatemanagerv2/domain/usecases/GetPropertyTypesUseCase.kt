package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import javax.inject.Inject

/** * Use case responsible for retrieving the list of all available property types.
 *
 * This component provides the UI layer with a dynamic list of property categories
 * (e.g., House, Apartment, Penthouse) collected from the data source.
 * It is primarily used to populate selection menus in search filters and property forms.
 */
interface GetPropertyTypesUseCase {

    /**
     * Executes the retrieval process for the unique property types.
     *
     * @return A list of unique strings representing the property categories.
     */
    suspend operator fun invoke(): List<String>
}

/**
 * Standard implementation of [GetPropertyTypesUseCase].
 *
 * It queries the [PropertyRepository] to gather all distinct property types
 * currently available in the system.
 *
 * @property propertyRepository The repository used to access property-related data.
 */
class GetPropertyTypesUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : GetPropertyTypesUseCase {

    override suspend operator fun invoke(): List<String> =
        propertyRepository.getPropertyTypes()
}