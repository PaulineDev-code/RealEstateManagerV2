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
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PropertyRepository @Inject constructor(private val database: MyDatabase,
                                             private val propertyDao : PropertyLocalDAO,
                                             private val agentDao: AgentDAO,
                                             private val mediaDao: MediaDAO,
                                             private val pointOfInterestCrossRefDao: PointOfInterestCrossRefDAO
) {

    suspend fun insertProperty(agentEntity: AgentEntity,
                               propertyEntity: PropertyLocalEntity,
                               photosEntities: List<MediaEntity>,
                               pointsOfInterestCrossRefs: List<PointOfInterestCrossRef> ) {
        database.withTransaction {

            agentDao.insertAgent(agentEntity)
            propertyDao.insertProperty(propertyEntity)
            mediaDao.insertMedias(photosEntities)
            pointOfInterestCrossRefDao.insertAll(pointsOfInterestCrossRefs)
        }

    }

    suspend fun updateProperty(
        agentEntity: AgentEntity,
        propertyEntity: PropertyLocalEntity,
        photosEntities: List<MediaEntity>,
        pointsOfInterestCrossRefs: List<PointOfInterestCrossRef>
    ) {
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

    suspend fun getAllProperties(): List<PropertyWithDetails> = withContext(Dispatchers.IO) {
        propertyDao.getAllProperties()
    }

    suspend fun getPropertyById(id: String): PropertyWithDetails = withContext(Dispatchers.IO) {
        propertyDao.getPropertyById(id)
    }

    suspend fun getPropertyTypes(): List<String> = withContext(Dispatchers.IO){
        propertyDao.getDistinctTypes()
    }

    suspend fun searchByCriteria(
        criteria: PropertySearchCriteria
    ): List<PropertyWithDetails> = withContext(Dispatchers.IO) {
        propertyDao.searchByCriteria(
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
    }
}
