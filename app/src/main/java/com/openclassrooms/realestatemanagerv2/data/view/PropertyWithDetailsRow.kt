package com.openclassrooms.realestatemanagerv2.data.view

import androidx.room.DatabaseView
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus


@DatabaseView(
    viewName = "property_with_details_view",
    value = """
    SELECT
      p.id                       AS id,
      p.type                     AS type,
      p.price                    AS price,
      p.area                     AS area,
      p.numberOfRooms            AS numberOfRooms,
      p.description              AS description,
      p.address                  AS address,
      p.status                   AS status,
      p.entryDate                AS entryDate,
      p.saleDate                 AS saleDate,
      p.agentId                  AS agentId,
      a.name                     AS agent_name,
      a.email                    AS agent_email,
      a.phoneNumber              AS agent_phone,
      -- agrégations utiles
      SUM(CASE WHEN m.type = 'photo' THEN 1 ELSE 0 END) AS photos_count,
      SUM(CASE WHEN m.type = 'video' THEN 1 ELSE 0 END) AS videos_count,
      -- listes aplaties (séparateur peu probable)
      GROUP_CONCAT(DISTINCT poi.pointOfInterestId) AS poi_ids,
      GROUP_CONCAT(DISTINCT m.mediaUrl)            AS media_urls
    FROM properties AS p
    LEFT JOIN agents  AS a   ON a.id  = p.agentId
    LEFT JOIN medias  AS m   ON m.propertyLocalId = p.id
    LEFT JOIN point_of_interest_cross_ref AS poi ON poi.propertyId = p.id
    GROUP BY p.id
    """
)
data class PropertyWithDetailsRow(
    val id: String,
    val type: String,
    val price: Double,
    val area: Double,
    val numberOfRooms: Int,
    val description: String,
    val address: String,
    val status: String,
    val entryDate: Long,
    val saleDate: Long?,
    val agentId: String,
    val agent_name: String?,
    val agent_email: String?,
    val agent_phone: String?,
    val photos_count: Int?,
    val videos_count: Int?,
    val poi_ids: String?,     // "poi1|poi2|poi3"
    val media_urls: String?   // "url1|url2|url3"
)
