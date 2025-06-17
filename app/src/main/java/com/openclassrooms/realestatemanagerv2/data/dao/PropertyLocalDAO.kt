package com.openclassrooms.realestatemanagerv2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyWithDetails

@Dao
interface PropertyLocalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyLocalEntity)

    @Update
    suspend fun updateProperty(property: PropertyLocalEntity)

    @Query("SELECT DISTINCT type FROM properties")
    suspend fun getDistinctTypes(): List<String>

    // THE 3 NEXT FUNCTIONS QUERY FROM ALL TABLES, NEEDED TO MATCH PROPERTY MODEL
    @Transaction
    @Query("SELECT * FROM properties WHERE id = :propertyId")
    suspend fun getPropertyById(propertyId: String): PropertyWithDetails

    @Transaction
    @Query("SELECT * FROM properties")
    suspend fun getAllProperties(): List<PropertyWithDetails>

    @Query("""
    SELECT DISTINCT address
    FROM properties
    WHERE latitude  IS NULL
       OR longitude IS NULL
  """)
    suspend fun getAddressesWithoutLatLng(): List<String>

    @Query("""
    UPDATE properties
    SET latitude  = :latitude,
        longitude = :longitude
    WHERE address = :address
  """)
    suspend fun updateLocationByAddress(
        address: String,
        latitude: Double,
        longitude: Double
    )

    @Transaction
    @Query("""
    SELECT properties.*
      FROM properties
     WHERE (:propertyTypesCount IS NULL OR :propertyTypesCount = 0 OR properties.type IN (:propertyTypes))
       AND (:minPrice       IS NULL OR properties.price            >= :minPrice)
       AND (:maxPrice       IS NULL OR properties.price            <= :maxPrice)
       AND (:minArea        IS NULL OR properties.area             >= :minArea)
       AND (:maxArea        IS NULL OR properties.area             <= :maxArea)
       AND (:minRooms       IS NULL OR properties.numberOfRooms    >= :minRooms)
       AND (:maxRooms       IS NULL OR properties.numberOfRooms    <= :maxRooms)
       AND (:minPhotos      IS NULL
            OR (SELECT COUNT(*) 
                  FROM medias
                 WHERE medias.propertyLocalId = properties.id
                   AND medias.type              = 'photo'
               ) >= :minPhotos)
       AND (:minVideos      IS NULL
            OR (SELECT COUNT(*) 
                  FROM medias
                 WHERE medias.propertyLocalId = properties.id
                   AND medias.type              = 'video'
               ) >= :minVideos)
       AND (:pointOfInterestIds         IS NULL
            OR (
                 SELECT COUNT(DISTINCT poiCrossRef.pointOfInterestId)
                   FROM point_of_interest_cross_ref AS poiCrossRef
                  WHERE poiCrossRef.propertyId         = properties.id
                    AND poiCrossRef.pointOfInterestId IN (:pointOfInterestIds)
               ) >= :pointOfInterestCount)
       AND (:minEntryDate   IS NULL OR properties.entryDate        >= :minEntryDate)
       AND (:minSaleDate    IS NULL OR properties.saleDate         >= :minSaleDate)
       AND (:agentId        IS NULL OR properties.agentId          = :agentId)
       ORDER BY properties.entryDate DESC
  """)
    suspend fun searchByCriteria(
        propertyTypes: List<String>?,
        propertyTypesCount: Int?,
        minPrice: Double?,
        maxPrice: Double?,
        minArea: Double?,
        maxArea: Double?,
        minRooms: Int?,
        maxRooms: Int?,
        minPhotos: Int?,
        minVideos: Int?,
        pointOfInterestIds: List<String>?,  // ids de PointOfInterestEntity
        pointOfInterestCount: Int?,
        minEntryDate: Long?,
        minSaleDate: Long?,
        agentId: String?
    ): List<PropertyWithDetails>

}
