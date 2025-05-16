package com.openclassrooms.realestatemanagerv2.utils

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.dao.MediaDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import java.util.UUID

class DatabaseUtil(propertyDAO: PropertyLocalDAO, agentDAO: AgentDAO,
                   pointOfInterestDAO: PointOfInterestDAO, mediaDAO: MediaDAO
) {
    private val propertyDao = propertyDAO
    private val agentDao = agentDAO
    private val photoDao = mediaDAO
    private val pointOfInterestDao = pointOfInterestDAO
    suspend fun prepopulateDatabase(
    ) {
        val pointsOfInterest = PointOfInterest.values().map { poiEnum ->
            PointOfInterestEntity(
                id = poiEnum.serialName,
                displayNameResId = poiEnum.displayNameResId
            )
        }

        pointOfInterestDao.insertPointsOfInterest(pointsOfInterest)


        val propertyList: List<Property> = listOf(
            Property(
                "1",
                "Apartment",
                1590000.0,
                127.84,
                6,
                "Paris XVIIIe - Damrémont Au septième étage d'un immeuble de standing construit en 2022, appartement traversant familial aux prestations haut de gamme ouvrant sur une terrasse plein ciel et offrant une vue dégagée sur Montmartre et le Sacré-Cœur. Ce bien au plan optimal se compose, coté jardin, d'un vaste séjour avec cuisine ouverte donnant sur une terrasse de 20 m² et d'une suite parentale avec sa salle d'eau attenante. La partie nuit se compose de 3 chambres donnant chacune sur un balcon filant de 8 m², d'une grande salle de bains avec toilettes, d'une buanderie et d'un dressing. Une place de parking complète ce bien.",
                listOf(
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/1/c/c/7/1cc70u75o969issv83hmdy59302zgkymg3xybn70z.jpg",
                        "Frontage"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/1/y/x/f/1yxfomqyl850c9kuixvume4ut7bu08623btpz0lzo.jpg",
                        "LivingRoom"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/2/8/9/l/289lgqpduoxa0n3xvmk7me1524xj60uybqzivzjv7.jpg",
                        "Bathroom with shower"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/0/f/2/f/0f2f22ulbmia6l3xkqsw1cxekocg0zgf1l6zwi1xv.jpg",
                        "Bathroom with bathtub"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/1/y/a/f/1yafso99sz6m0zxo61pfx0bls2ugl9fccee0gx4r7.jpg",
                    "Balcony to the right"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/0/u/k/5/0uk5d67u4kb8aunp2thhiaq382jnl6q8kz37ad2z7.jpg",
                        "Balcony to the left"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/0/j/6/i/0j6ii7pdynyf7u6mfjj0ow3wa8ssb4k96zhtpus0j.jpg",
                        "Bedroom number 1"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/1/g/w/u/1gwuqxzaf7ewuqjtgpqyfzi9t08mm1bweeebjrlub.jpg",
                        "Bedroom number 2"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/0/1/s/y/01sy3smzwl8raj8wdnvloo26gdzvpfb2algf79hzn.jpg",
                        "Bedroom number 3"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/1/v/l/l/1vllyh1gc496whmuvknpbitw6ltr2784sx8bkaqtv.jpg",
                        "Gaming room"
                    )
                ),
                "152 rue Lamarck, 75018 Paris, France",
                listOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
                PropertyStatus.Available,
                4477894998,
                null,
                Agent("1", "Will", "911", "willagent@brooklyn.com")
            ), Property(
                "2",
                "Duplex",
                700000.0,
                47.0,
                2,
                "Charmant duplex au calme, situé au premier étage d'un immeuble ancien datant de 1614, avec vue sur cour." +
                        "\n" +
                        "Cet agréable 2 pièces, de 46,65 m2 loi Carrez saura vous séduire par ses prestations de qualité et sa hauteur sous plafond de 3,85 mètres.\n" +
                        "\n" +
                        "Au premier niveau, vous y trouverez une entrée, un vaste séjour de 20 m2 avec cheminée et deux grandes fenêtres en double vitrage, une cuisine américaine équipée et un WC séparé. Au second niveau, vous trouverez une charmante mezzanine et une chambre exposée SUD EST avec vue sur jardin et rangement.\n" +
                        "\n" +
                        "Situé dans un quartier prisé à DEUX PAS DES QUAIS DE SEINE et des MONUMENTS PARISIENS.\n",
                listOf(
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/2/1/a/v/21avf4qbks8pbv924mtgtmkurp740d7xma6y628nq.jpg",
                        "Front door"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/0/r/9/f/0r9fn8jnkwy5622utakwb36ml3fo94w91cgyhzw46.jpg",
                        "LivingRoom side 1/2"
                    ),
                    Photo("https://v.seloger.com/s/cdn/x/visuels/1/9/v/b/19vb6ql2n8sv94gw32d1bf2vykmit3suwu5s6u79y.jpg",
                        "Living room side 2/2"
                    ),
                    Photo(
                        "https://v.seloger.com/s/cdn/x/visuels/1/1/k/o/11kon8apbn4g8j35o7ahweoqaqbn82ipv80flzeqe.jpg",
                        "Bathroom"
                    ),
                    Photo("https://v.seloger.com/s/cdn/x/visuels/1/h/l/5/1hl5dsclzgqucacx41ca9sbsqx61oga1lmd8eulhy.jpg",
                        "Bedroom"
                    ),
                    Photo("https://v.seloger.com/s/cdn/x/visuels/1/j/n/p/1jnp3nwdn4nuniwcunjt0vucfy9y3h7f1ij8f9is6.jpg",
                        "Kitchen"
                    ),
                    Photo("https://v.seloger.com/s/cdn/x/visuels/1/4/t/p/14tpmuphdszj25f8xhjnd7z2ro312jp8b52h8uiqe.jpg",
                        "First floor")

                ),
                "4 rue Poulletier, 75004 Paris, France",
                listOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
                PropertyStatus.Available,
                4477894645,
                null,
                Agent("1", "Will", "911", "willagent@brooklyn.com")
            )
        )


        propertyList.forEach { property ->
            val propertyLocalEntity = property.toPropertyLocalEntity()
            val agentEntity = property.agent.toAgentEntity()
            val photosEntities = property.mapToMediaEntities()
            val pointsOfInterestEntities = property.mapToPointOfInterestEntities()

            //Insert order to ensure no foreign-Keys violation
            agentDao.insertAgent(agentEntity)
            propertyDao.insertProperty(propertyLocalEntity)
            photoDao.insertMedias(photosEntities)
            pointOfInterestDao.insertPointsOfInterest(pointsOfInterestEntities)
        }
    }

}