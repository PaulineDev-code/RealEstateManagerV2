package com.openclassrooms.realestatemanagerv2.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PhotoEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.domain.model.AgentDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PhotoDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.utils.DatabaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Database(entities = [PropertyLocalEntity::class, PointOfInterestEntity::class,
    PhotoEntity::class, AgentEntity::class],
    version = 1, exportSchema = false)
@Singleton
abstract class MyDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyLocalDAO
    abstract fun photoDao(): PhotoDAO
    abstract fun pointOfInterestDao(): PointOfInterestDAO
    abstract fun agentDAO(): AgentDAO

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        //Database instance not necessarily needed

        fun getInstance(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instanceDB = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "database"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val propertyDAO = getInstance(context).propertyDao()
                        val agentDAO = getInstance(context).agentDAO()
                        val photoDAO = getInstance(context).photoDao()
                        val pointOfInterestDAO = getInstance(context).pointOfInterestDao()
                        CoroutineScope(Dispatchers.IO).launch {

                            DatabaseUtil(propertyDAO, agentDAO, pointOfInterestDAO, photoDAO).prepopulateDatabase()
                        }
                    }
                })
                    .build()

                INSTANCE = instanceDB
                instanceDB
            }
        }
    }

}