package com.openclassrooms.realestatemanagerv2.di

import android.content.Context
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.data.dao.MediaDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideMyDatabase(@ApplicationContext context: Context): MyDatabase {
        return MyDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun providePropertyDao(myDatabase: MyDatabase): PropertyLocalDAO {
        return myDatabase.propertyDao()
    }
    @Provides
    @Singleton
    fun provideAgentDao(myDatabase: MyDatabase): AgentDAO {
        return myDatabase.agentDAO()
    }@Provides
    @Singleton
    fun providePhotoDao(myDatabase: MyDatabase): MediaDAO {
        return myDatabase.mediaDao()
    }
    @Provides
    @Singleton
    fun providePointOfInterestDao(myDatabase: MyDatabase): PointOfInterestDAO {
        return myDatabase.pointOfInterestDao()
    }

    @Provides
    @Singleton
    fun providePropertyRepository(database: MyDatabase, propertyLocalDAO: PropertyLocalDAO, agentDAO: AgentDAO,
                                  mediaDAO: MediaDAO, pointOfInterestDAO: PointOfInterestDAO
    ): PropertyRepository {
        return PropertyRepository( database, propertyLocalDAO, agentDAO, mediaDAO, pointOfInterestDAO)
    }

}

