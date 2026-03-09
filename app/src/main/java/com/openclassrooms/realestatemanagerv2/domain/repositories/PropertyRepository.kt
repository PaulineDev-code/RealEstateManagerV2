package com.openclassrooms.realestatemanagerv2.domain.repositories

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria

/**
 * Repository interface that defines the data contract for all [Property] operations.
 *
 * This interface acts as the Single Source of Truth for the application's real estate data.
 * It abstracts the underlying storage mechanism (e.g., Room database) from the domain layer,
 * ensuring that business logic remains independent of data persistence details.
 */
interface PropertyRepository {

    /**
     * Persists a new property into the data source.
     *
     * @param property The [Property] domain model to be inserted.
     */
    suspend fun insertProperty(property: Property): Unit

    /**
     * Updates an existing property's information in the data source.
     *
     * @param property The [Property] domain model containing updated values.
     */
    suspend fun updateProperty(property: Property): Unit

    /**
     * Retrieves the complete list of all properties stored in the database.
     *
     * @return A list of all available [Property] objects.
     */
    suspend fun getAllProperties(): List<Property>

    /**
     * Retrieves a specific property by its unique identifier.
     *
     * @param id The unique ID of the property to find.
     * @return The [Property] matching the ID.
     * @throws Exception if no property is found with the given ID.
     */
    suspend fun getPropertyById(id: String): Property

    /**
     * Retrieves the list of all distinct property types currently available
     * in the data source (e.g., House, Apartment, Loft).
     *
     * This is primarily used to populate the property type filter in search or creation forms.
     *
     * @return A list of unique property type strings.
     */
    suspend fun getPropertyTypes(): List<String>

    /**
     * Performs a complex search query based on multiple filtering criteria.
     *
     * @param criteria An object containing the various filter parameters
     * (price range, area, rooms, etc.).
     * @return A filtered list of [Property] objects matching the specified [PropertySearchCriteria].
     */
    suspend fun searchByCriteria(criteria: PropertySearchCriteria): List<Property>
}