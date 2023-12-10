package com.openclassrooms.realestatemanagerv2.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanagerv2.data.entity.PhotoEntity

@Dao
interface PhotoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photoEntities: List<PhotoEntity>)

    @Update
    suspend fun updatePhoto(photoEntities: List<PhotoEntity>)

    @Query("SELECT * FROM photos WHERE propertyLocalId = :propertyLocalId")
    suspend fun getPhotos(propertyLocalId: Long): List<PhotoEntity>?

}

