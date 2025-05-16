package com.openclassrooms.realestatemanagerv2.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.MediaEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.data.dao.MediaDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestCrossRef
import com.openclassrooms.realestatemanagerv2.utils.DatabaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Database(entities = [PropertyLocalEntity::class, PointOfInterestEntity::class,
    MediaEntity::class, AgentEntity::class, PointOfInterestCrossRef::class],
    version = 1, exportSchema = false)
@Singleton
abstract class MyDatabase : RoomDatabase() {

    abstract fun propertyDAO(): PropertyLocalDAO
    abstract fun mediaDAO(): MediaDAO
    abstract fun pointOfInterestDAO(): PointOfInterestDAO
    abstract fun agentDAO(): AgentDAO

}