package com.openclassrooms.realestatemanagerv2.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanagerv2.data.entity.NearByPointOfInterestEntity

@Dao
interface NearByPointOfInterestDAO {

    @Insert
    suspend fun insertPointsOfInterest(pointsOfInterest: List<NearByPointOfInterestEntity>)

    @Update
    suspend fun updatePointsOfInterest(pointsOfInterest: List<NearByPointOfInterestEntity>)

    @Query("SELECT * FROM points_of_interest WHERE propertyLocalId = :propertyLocalId")
    suspend fun getPointsOfInterest(propertyLocalId: Long): List<NearByPointOfInterestEntity>?

}