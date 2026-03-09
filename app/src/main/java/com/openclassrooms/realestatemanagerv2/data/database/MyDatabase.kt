package com.openclassrooms.realestatemanagerv2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.dao.MediaDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestCrossRefDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.data.dao.ProviderDAO
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.MediaEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestCrossRef
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.data.view.PropertyWithDetailsRow
import javax.inject.Singleton

@Database(entities = [PropertyLocalEntity::class, PointOfInterestEntity::class,
    MediaEntity::class, AgentEntity::class, PointOfInterestCrossRef::class],
    views = [PropertyWithDetailsRow::class],
    version = 1, exportSchema = false)
@Singleton
abstract class MyDatabase : RoomDatabase() {

    abstract fun propertyDAO(): PropertyLocalDAO
    abstract fun mediaDAO(): MediaDAO
    abstract fun pointOfInterestDAO(): PointOfInterestDAO
    abstract fun pointOfInterestCrossRefDAO(): PointOfInterestCrossRefDAO
    abstract fun agentDAO(): AgentDAO
    abstract fun providerDAO(): ProviderDAO

}