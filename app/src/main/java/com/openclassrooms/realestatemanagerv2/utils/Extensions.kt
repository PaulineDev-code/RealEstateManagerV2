package com.openclassrooms.realestatemanagerv2.utils

import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PhotoEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyWithDetails
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import java.util.UUID


// 1. Extensions to convert entities to 'in-app' model objects
//For better architecture, Entities should not have access to Model, prefer a mapFrom() function
    fun List<PropertyWithDetails>.mapToProperties(): List<Property> {
        return map { it.toProperty() }
    }
    fun PropertyWithDetails.toProperty(): Property {
        return Property(
            id = property.id,
            type = property.type,
            price = property.price,
            area = property.area,
            numberOfRooms = property.numberOfRooms,
            description = property.description,
            photos = photos.mapToPhotos(),
            videoUrl = property.videoUrl,
            address = property.address,
            nearbyPointsOfInterest = nearByPointsOfInterest.mapToPointsOfInterest(),
            status = if(property.status == "AVAILABLE") { PropertyStatus.Available }
            else { PropertyStatus.Sold },
            entryDate = property.entryDate,
            saleDate = property.saleDate,
            agent = agent.toAgent()
        )
    }
     private fun List<PhotoEntity>.mapToPhotos(): List<Photo> {
        return map { it.toPhoto() }
    }

    private fun PhotoEntity.toPhoto(): Photo {
        return Photo(
            imageUrl = photoUrl,
            description = description
        )
    }

    private fun List<PointOfInterestEntity>.mapToPointsOfInterest(): List<String> {
        return map { it.toPointOfInterest() }
    }

    private fun PointOfInterestEntity.toPointOfInterest() : String {
        return pointOfInterest
    }

    private fun AgentEntity.toAgent(): Agent {
        return Agent(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            email = email
        )
    }

    // 2. All Extensions to convert 'in-app' model objects to entities

fun List<Property>.mapToPropertyLocalEntities(): List<PropertyLocalEntity> {
        return map { it.toPropertyLocalEntity() }
    }
    fun Property.toPropertyLocalEntity(): PropertyLocalEntity {
        return PropertyLocalEntity(
            id = id,
            type = type,
            price = price,
            area = area,
            numberOfRooms = numberOfRooms,
            description = description,
            videoUrl = videoUrl,
            address = address,
            status =  status.toString(),
            entryDate = entryDate,
            saleDate = saleDate,
            agentId = agent.id
        )
    }
    fun Property.mapToPhotoEntities(): List<PhotoEntity> {
        return photos.map { it.toPhotoEntity(id) }
    }

    private fun Photo.toPhotoEntity(propertyLocalId: String): PhotoEntity {
        return PhotoEntity(
            id = UUID.randomUUID().toString(),
            photoUrl = imageUrl,
            description = description,
            propertyLocalId = propertyLocalId
        )
    }

    fun Property.mapToPointOfInterestEntities(): List<PointOfInterestEntity> {
        return nearbyPointsOfInterest.map { it.toPointOfInterestEntity(id) }
    }

    private fun String.toPointOfInterestEntity(propertyLocalId: String) : PointOfInterestEntity {
        return PointOfInterestEntity(
            id = UUID.randomUUID().toString(),
            pointOfInterest = this,
            propertyLocalId = propertyLocalId

        )
    }

    fun Agent.toAgentEntity(): AgentEntity {
        return AgentEntity(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            email = email
        )
    }



