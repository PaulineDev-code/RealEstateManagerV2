package com.openclassrooms.realestatemanagerv2.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.data.dao.MediaDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
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
        @ApplicationContext context: Context
    ): MyDatabase {
        // On déclare la variable avant de la construire, pour pouvoir la capturer dans le callback
        lateinit var dbInstance: MyDatabase

        dbInstance = Room.databaseBuilder(
            context.applicationContext,
            MyDatabase::class.java,
            "database"          // nom de ta BDD
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Ce bloc ne sera exécuté qu'au tout premier onCreate
                    CoroutineScope(Dispatchers.IO).launch {
                        DatabaseUtil(
                            dbInstance.propertyDAO(),
                            dbInstance.agentDAO(),
                            dbInstance.pointOfInterestDAO(),
                            dbInstance.mediaDAO()
                        ).prepopulateDatabase()
                    }
                }
            })
            .build()

        return dbInstance
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
    fun providePropertyRepository(database: MyDatabase, propertyLocalDAO: PropertyLocalDAO, agentDAO: AgentDAO,
                                  mediaDAO: MediaDAO, pointOfInterestDAO: PointOfInterestDAO
    ): PropertyRepository {
        return PropertyRepository( database, propertyLocalDAO, agentDAO, mediaDAO, pointOfInterestDAO)
    }

}

