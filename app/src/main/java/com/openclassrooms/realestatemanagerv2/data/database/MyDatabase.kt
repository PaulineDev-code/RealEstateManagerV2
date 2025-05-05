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
    version = 3, exportSchema = false)
@Singleton
abstract class MyDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyLocalDAO
    abstract fun mediaDao(): MediaDAO
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
                        Log.d("MyDatabase", "Callback onCreate: DAOs are not null - ${getInstance(context).propertyDao() != null}")
                        CoroutineScope(Dispatchers.IO).launch {
                            val propertyDAO = getInstance(context).propertyDao()
                            val agentDAO = getInstance(context).agentDAO()
                            val mediaDAO = getInstance(context).mediaDao()
                            val pointOfInterestDAO = getInstance(context).pointOfInterestDao()

                            DatabaseUtil(propertyDAO, agentDAO, pointOfInterestDAO, mediaDAO).prepopulateDatabase()
                            //Prefer static method using DAO's than instanciating object for it
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