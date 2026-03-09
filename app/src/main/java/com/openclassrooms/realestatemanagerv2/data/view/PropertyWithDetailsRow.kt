package com.openclassrooms.realestatemanagerv2.data.view

import androidx.room.DatabaseView


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
      
      COALESCE((SELECT COUNT(*) FROM medias WHERE propertyLocalId = p.id AND type = 'photo'), 0) AS photos_count,
      COALESCE((SELECT COUNT(*) FROM medias WHERE propertyLocalId = p.id AND type = 'video'), 0) AS videos_count,

      (SELECT GROUP_CONCAT(DISTINCT pointOfInterestId) 
       FROM point_of_interest_cross_ref 
       WHERE propertyId = p.id) AS poi_ids,
      (SELECT GROUP_CONCAT(DISTINCT mediaUrl) 
       FROM medias 
       WHERE propertyLocalId = p.id) AS media_urls
    FROM properties p
    LEFT JOIN agents  AS a   ON a.id  = p.agentId
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
