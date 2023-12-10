package com.openclassrooms.realestatemanagerv2.utils

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.AgentDAO
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PhotoDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus

class DatabaseUtil(propertyDAO: PropertyLocalDAO, agentDAO: AgentDAO,
                   pointOfInterestDAO: PointOfInterestDAO, photoDAO: PhotoDAO) {
    private val propertyDao = propertyDAO
    private val agentDao = agentDAO
    private val photoDao = photoDAO
    private val pointOfInterestDao = pointOfInterestDAO
    suspend fun prepopulateDatabase(
    ) {
        val propertyList: List<Property> = listOf(
            Property(
                "1",
                "Apartment",
                300000.0,
                90.0,
                3,
                "A spacious flat in the middle of Brooklyn",
                listOf(
                    Photo(
                        "https://unsplash.com/fr/photos/edificio-in-cemento-bianco-e-blu-sotto-il-cielo-blu-durante-il-giorno-jfRrtH1hDTo",
                        "façade"
                    ),
                    Photo(
                        "https://unsplash.com/fr/photos/divano-componibile-grigio-A4U4dEuN-hw",
                        "LivingRoom"
                    ),
                    Photo(
                        "https://unsplash.com/fr/photos/une-salle-de-bain-avec-baignoire-lavabo-et-miroir--4muZDx4-dM",
                        "Bathroom"
                    )
                ),
                null,
                "833 Ocean Ave, Brooklyn, NY 11226, États-Unis",
                listOf("Pharmacy", "Restaurant"),
                PropertyStatus.Available,
                "20/11/2023",
                null,
                Agent("1", "Will", "911", "willagent@brooklyn.com")
            ), Property(
                "2",
                "House",
                400000.0,
                120.0,
                3,
                "A house in the middle of Brooklyn",
                listOf(
                    Photo(
                        "https://unsplash.com/fr/photos/edificio-in-cemento-bianco-e-blu-sotto-il-cielo-blu-durante-il-giorno-jfRrtH1hDTo",
                        "façade"
                    ),
                    Photo(
                        "https://unsplash.com/fr/photos/divano-componibile-grigio-A4U4dEuN-hw",
                        "LivingRoom"
                    ),
                    Photo(
                        "https://unsplash.com/fr/photos/une-salle-de-bain-avec-baignoire-lavabo-et-miroir--4muZDx4-dM",
                        "Bathroom"
                    )
                ),
                null,
                "835 Ocean Ave, Brooklyn, NY 11226, États-Unis",
                listOf("Pharmacy", "Restaurant"),
                PropertyStatus.Available,
                "20/11/2023",
                null,
                Agent("1", "Will", "911", "willagent@brooklyn.com")
            )
        )

        // Opérations de base de données ou appels réseau ici
        propertyList.forEach { property ->
            val propertyLocalEntity = property.toPropertyLocalEntity()
            val agentEntity = property.agent.toAgentEntity()
            val photosEntities = property.mapToPhotoEntities()
            val pointsOfInterestEntities = property.mapToPointOfInterestEntities()

            propertyDao.insertProperty(propertyLocalEntity)
            agentDao.insertAgent(agentEntity)
            photoDao.insertPhoto(photosEntities)
            pointOfInterestDao.insertPointsOfInterest(pointsOfInterestEntities)
        }
    }

}