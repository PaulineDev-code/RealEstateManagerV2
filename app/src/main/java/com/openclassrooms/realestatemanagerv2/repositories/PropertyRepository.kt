package com.openclassrooms.realestatemanagerv2.repositories

import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyWithDetails
import com.openclassrooms.realestatemanagerv2.domain.model.AgentDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PhotoDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyLocalDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PropertyRepository @Inject constructor(private val propertyDao : PropertyLocalDAO, private val agentDAO: AgentDAO,
    private val photoDAO: PhotoDAO, private val pointOfInterestDAO: PointOfInterestDAO) {

    suspend fun insertProperty(property: PropertyLocalEntity) = propertyDao.insertProperty(property)

    suspend fun updateProperty(property: PropertyLocalEntity) = propertyDao.updateProperty(property)

    suspend fun getAllProperties(): Flow<List<PropertyWithDetails>> {
        return flow {
            emit(withContext(Dispatchers.IO) {
                propertyDao.getAllProperties()
            })
        }
    }

    suspend fun getPropertyById(property: PropertyLocalEntity) = propertyDao.getPropertyById(property.id)

}
