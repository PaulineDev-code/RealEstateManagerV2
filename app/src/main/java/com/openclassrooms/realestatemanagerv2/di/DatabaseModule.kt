package com.openclassrooms.realestatemanagerv2.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.withTransaction
import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.dao.MediaDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestCrossRefDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.utils.DatabaseStatusTracker
import com.openclassrooms.realestatemanagerv2.utils.DatabaseUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        databaseStatusTracker: DatabaseStatusTracker
    ): MyDatabase {
        val db = Room.databaseBuilder(
            context.applicationContext,
            MyDatabase::class.java,
            "database"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("DatabaseModule", "Checking if database needs prepopulation")

                // Check if DB is empty by counting POIs
                val poiCount = db.pointOfInterestDAO().count()

                if (poiCount == 0) {
                    Log.d("DatabaseModule", "Database is empty, starting prepopulation")

                    db.withTransaction {
                        DatabaseUtil(
                            db.propertyDAO(),
                            db.agentDAO(),
                            db.pointOfInterestDAO(),
                            db.pointOfInterestCrossRefDAO(),
                            db.mediaDAO()
                        ).prepopulateDatabase()
                    }

                    Log.d("DatabaseModule", "✅ Database prepopulation completed successfully")
                    databaseStatusTracker.notifyPrepopulated()
                } else {
                    Log.d("DatabaseModule", "Database already contains $poiCount POIs, skipping prepopulation")
                    databaseStatusTracker.notifyPrepopulated()
                }
            } catch (e: Exception) {
                Log.e("DatabaseModule", "❌ Error during database prepopulation", e)
            }
        }

        return db
    }

    @Provides
    @Singleton
    fun providePropertyDao(myDatabase: MyDatabase): PropertyLocalDAO {
        return myDatabase.propertyDAO()
    }
    @Provides
    @Singleton
    fun provideAgentDao(myDatabase: MyDatabase): AgentDAO {
        return myDatabase.agentDAO()
    }@Provides
    @Singleton
    fun providePhotoDao(myDatabase: MyDatabase): MediaDAO {
        return myDatabase.mediaDAO()
    }
    @Provides
    @Singleton
    fun providePointOfInterestDao(myDatabase: MyDatabase): PointOfInterestDAO {
        return myDatabase.pointOfInterestDAO()
    }
    @Provides
    @Singleton
    fun providePoiXDao(myDatabase: MyDatabase): PointOfInterestCrossRefDAO {
        return myDatabase.pointOfInterestCrossRefDAO()
    }

}

