package com.openclassrooms.realestatemanagerv2.repositories

import androidx.room.withTransaction
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyWithDetails
import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.dao.MediaDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestCrossRefDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.MediaEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestCrossRef
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import com.openclassrooms.realestatemanagerv2.utils.mapToMediaEntities
import com.openclassrooms.realestatemanagerv2.utils.mapToPointOfInterestCrossRefs
import com.openclassrooms.realestatemanagerv2.utils.toAgentEntity
import com.openclassrooms.realestatemanagerv2.utils.toPropertyLocalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepositoryImpl @Inject constructor(private val database: MyDatabase,
                                             private val propertyDao : PropertyLocalDAO,
                                             private val agentDao: AgentDAO,
                                             private val mediaDao: MediaDAO,
                                             private val pointOfInterestCrossRefDao: PointOfInterestCrossRefDAO
) : PropertyRepository {

    override suspend fun insertProperty(property: Property ) {
        val propertyEntity = property.toPropertyLocalEntity()
        val agentEntity = property.agent.toAgentEntity()
        val photosEntities = property.mapToMediaEntities()
        val pointsOfInterestCrossRefs = property.mapToPointOfInterestCrossRefs()

        database.withTransaction {

            agentDao.insertAgent(agentEntity)
            propertyDao.insertProperty(propertyEntity)
            mediaDao.insertMedias(photosEntities)
            pointOfInterestCrossRefDao.insertAll(pointsOfInterestCrossRefs)
        }
    }

    override suspend fun updateProperty(property: Property) {
        val propertyEntity = property.toPropertyLocalEntity()
        val agentEntity = property.agent.toAgentEntity()
        val photosEntities = property.mapToMediaEntities()
        val pointsOfInterestCrossRefs = property.mapToPointOfInterestCrossRefs()

        database.withTransaction {
            agentDao.updateAgent(agentEntity)
            propertyDao.updateProperty(propertyEntity)
            // Medias : delete-then-reinsert
            mediaDao.deleteByPropertyId(propertyEntity.id)
            mediaDao.insertMedias(photosEntities)
            // Cross-refs : delete-then-reinsert
            pointOfInterestCrossRefDao.deleteByPropertyId(propertyEntity.id)
            pointOfInterestCrossRefDao.insertAll(pointsOfInterestCrossRefs)
        }
    }

    override suspend fun getAllProperties(): List<Property> = withContext(Dispatchers.IO) {
        val propertyEntities = propertyDao.getAllProperties()
        propertyEntities.map { propertyWithDetails ->
            Property.fromPropertyWithDetails(propertyWithDetails)
        }
    }

    override suspend fun getPropertyById(id: String): Property = withContext(Dispatchers.IO) {
        val propertyEntities = propertyDao.getPropertyById(id)
        Property.fromPropertyWithDetails(propertyEntities)
    }

    override suspend fun getPropertyTypes(): List<String> = withContext(Dispatchers.IO){
        propertyDao.getDistinctTypes()
    }

    override suspend fun searchByCriteria(
        criteria: PropertySearchCriteria
    ): List<Property> = withContext(Dispatchers.IO) {
        val propertyEntities = propertyDao.searchByCriteria(
            propertyTypes       = criteria.propertyType
                ?.takeIf { it.isNotEmpty() },
            propertyTypesCount = criteria.propertyType?.size,
            minPrice            = criteria.minPrice,
            maxPrice            = criteria.maxPrice,
            minArea             = criteria.minArea,
            maxArea             = criteria.maxArea,
            minRooms            = criteria.minNumberOfRooms,
            maxRooms            = criteria.maxNumberOfRooms,
            minPhotos           = criteria.minPhotos,
            minVideos           = criteria.minVideos,
            pointOfInterestIds  = criteria.nearbyPointsOfInterest
                ?.map { it.serialName }
                ?.takeIf { it.isNotEmpty() },
            pointOfInterestCount= criteria.nearbyPointsOfInterest?.size,
            minEntryDate        = criteria.minEntryDate,
            minSaleDate         = criteria.minSaleDate,
            agentId             = criteria.agentId
        )
        propertyEntities.map { propertyWithDetails ->
            Property.fromPropertyWithDetails(propertyWithDetails)
        }
    }

}