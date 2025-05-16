package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import com.openclassrooms.realestatemanagerv2.utils.toAgentEntity
import com.openclassrooms.realestatemanagerv2.utils.toPropertyLocalEntity
import javax.inject.Inject
import com.openclassrooms.realestatemanagerv2.utils.mapToMediaEntities
import com.openclassrooms.realestatemanagerv2.utils.mapToPointOfInterestEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddPropertyUseCase @Inject constructor(private val propertyRepository: PropertyRepository) {
    suspend operator fun invoke(property: Property): Unit = withContext(Dispatchers.IO) {

        val propertyEntity = property.toPropertyLocalEntity()
        val agentEntity = property.agent.toAgentEntity()
        val photosEntities = property.mapToMediaEntities()
        val pointsOfInterestEntities = property.mapToPointOfInterestEntities()

        propertyRepository.insertProperty(
            agentEntity = agentEntity,
            propertyEntity = propertyEntity,
            photosEntities = photosEntities,
            pointsOfInterestEntities = pointsOfInterestEntities
            )
    }
}