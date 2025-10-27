package com.openclassrooms.realestatemanagerv2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestCrossRef

@Dao
interface PointOfInterestCrossRefDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reference: PointOfInterestCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(references: List<PointOfInterestCrossRef>)
}