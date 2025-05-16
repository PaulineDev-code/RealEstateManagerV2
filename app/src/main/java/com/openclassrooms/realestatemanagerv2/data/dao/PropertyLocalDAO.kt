package com.openclassrooms.realestatemanagerv2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyWithDetails
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria

@Dao
interface PropertyLocalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyLocalEntity)

    @Update
    suspend fun updateProperty(property: PropertyLocalEntity)

    // THE 2 NEXT FUNCTIONS QUERY FROM ALL TABLES NEEDED TO MATCH PROPERTY MODEL
    @Transaction
    @Query("SELECT * FROM properties WHERE id = :propertyId")
    suspend fun getPropertyById(propertyId: String): PropertyWithDetails

    @Transaction
    @Query("SELECT * FROM properties")
    suspend fun getAllProperties(): List<PropertyWithDetails>

    @Query("SELECT DISTINCT type FROM properties")
    suspend fun getDistinctTypes(): List<String>



   /* @Query("SELECT * FROM properties " +
            "WHERE (:propertyType IS NULL OR type = :propertyType) " +
            "AND (:minPrice IS NULL OR price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR price <= :maxPrice) " +
            "AND (:minArea IS NULL OR area >= :minArea) " +
            "AND (:maxArea IS NULL OR area <= :maxArea) " +
            "AND (:minNumberOfRooms IS NULL OR numberOfRooms >= :minNumberOfRooms) " +
            "AND (:minPhotos IS NULL OR (SELECT COUNT(*) FROM property_photo WHERE propertyId = id) >= :minPhotos) " +
            "AND (:minVideos IS NULL OR (SELECT COUNT(*) FROM property_video WHERE propertyId = id) >= :minVideos) " +
            "AND (:nearbyPointsOfInterest IS NULL OR nearbyPointsOfInterest = :nearbyPointsOfInterest) " +
            "AND (:status IS NULL OR status = :status) " +
            "AND (:minEntryDate IS NULL OR entryDate >= :minEntryDate) " +
            "AND (:minSaleDate IS NULL OR saleDate >= :minSaleDate) " +
            "AND (:agentId IS NULL OR agentId = :agentId)")
    suspend fun searchByCriterias(criteria: PropertySearchCriteria): List<PropertyWithDetails>*/

}
