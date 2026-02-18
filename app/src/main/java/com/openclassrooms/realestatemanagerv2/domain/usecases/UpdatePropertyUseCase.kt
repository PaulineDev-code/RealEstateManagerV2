package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import com.openclassrooms.realestatemanagerv2.utils.mapToMediaEntities
import com.openclassrooms.realestatemanagerv2.utils.mapToPointOfInterestCrossRefs
import com.openclassrooms.realestatemanagerv2.utils.toAgentEntity
import com.openclassrooms.realestatemanagerv2.utils.toPropertyLocalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface UpdatePropertyUseCase {
    suspend operator fun invoke(property: Property): Unit
}

class UpdatePropertyUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : UpdatePropertyUseCase {

    override suspend operator fun invoke(property: Property) = withContext(Dispatchers.IO) {

        val propertyEntity = property.toPropertyLocalEntity()
        val agentEntity = property.agent.toAgentEntity()
        val photosEntities = property.mapToMediaEntities()
        val pointsOfInterestCrossRefs = property.mapToPointOfInterestCrossRefs()

        propertyRepository.updateProperty(
            agentEntity = agentEntity,
            propertyEntity = propertyEntity,
            photosEntities = photosEntities,
            pointsOfInterestCrossRefs = pointsOfInterestCrossRefs
        )
    }
}