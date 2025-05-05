package com.openclassrooms.realestatemanagerv2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanagerv2.data.entity.MediaEntity

@Dao
interface MediaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedias(mediaEntities: List<MediaEntity>)

    @Update
    suspend fun updateMedias(mediaEntities: List<MediaEntity>)

    @Query("SELECT * FROM medias WHERE propertyLocalId = :propertyLocalId")
    suspend fun getMedias(propertyLocalId: Long): List<MediaEntity>?

}

