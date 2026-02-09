package com.openclassrooms.realestatemanagerv2.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestCrossRef

@Dao
interface PointOfInterestCrossRefDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reference: PointOfInterestCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(references: List<PointOfInterestCrossRef>)

    @Query("DELETE FROM point_of_interest_cross_ref WHERE propertyId = :propertyId")
    suspend fun deleteByPropertyId(propertyId: String)

}