package com.openclassrooms.realestatemanagerv2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.NearByPointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PhotoEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.domain.model.AgentDAO
import com.openclassrooms.realestatemanagerv2.domain.model.NearByPointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PhotoDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyLocalDAO

@Database(entities = [PropertyLocalEntity::class, NearByPointOfInterestEntity::class,
    PhotoEntity::class, AgentEntity::class],
    version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract val propertyDao: PropertyLocalDAO
    abstract val photoDao: PhotoDAO
    abstract val pointsOfInterestDao: NearByPointOfInterestDAO
    abstract val agentDAO: AgentDAO
}