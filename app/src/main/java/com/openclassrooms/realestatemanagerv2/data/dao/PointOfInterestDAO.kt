package com.openclassrooms.realestatemanagerv2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity

@Dao
interface PointOfInterestDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointsOfInterest(pointsOfInterest: List<PointOfInterestEntity>)

    @Update
    suspend fun updatePointsOfInterest(pointsOfInterest: List<PointOfInterestEntity>)

}