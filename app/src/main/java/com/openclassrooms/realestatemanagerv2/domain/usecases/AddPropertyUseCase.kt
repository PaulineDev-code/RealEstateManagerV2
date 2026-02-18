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

interface AddPropertyUseCase {
    suspend operator fun invoke(property: Property): Unit
}

class AddPropertyUseCaseImpl @Inject constructor(
    private val propertyRepository: PropertyRepository
) : AddPropertyUseCase {
    override suspend operator fun invoke(property: Property): Unit = withContext(Dispatchers.IO) {

        val propertyEntity = property.toPropertyLocalEntity()
        val agentEntity = property.agent.toAgentEntity()
        val photosEntities = property.mapToMediaEntities()
        val pointsOfInterestCrossRefs = property.mapToPointOfInterestCrossRefs()

        propertyRepository.insertProperty(
            agentEntity = agentEntity,
            propertyEntity = propertyEntity,
            photosEntities = photosEntities,
            pointsOfInterestCrossRefs = pointsOfInterestCrossRefs
        )
    }
}