package com.openclassrooms.realestatemanagerv2.di

import android.content.Context
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.domain.model.AgentDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PhotoDAO
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyLocalDAO
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
    fun providePropertyDao(myDatabase: MyDatabase): PropertyLocalDAO {
        return myDatabase.propertyDao()
    }
    @Provides
    fun provideAgentDao(myDatabase: MyDatabase): AgentDAO {
        return myDatabase.agentDAO()
    }@Provides
    fun providePhotoDao(myDatabase: MyDatabase): PhotoDAO {
        return myDatabase.photoDao()
    }
    @Provides
    fun providePointOfInterestDao(myDatabase: MyDatabase): PointOfInterestDAO {
        return myDatabase.pointOfInterestDao()
    }

    @Provides
    fun providePropertyRepository(propertyLocalDAO: PropertyLocalDAO, agentDAO: AgentDAO,
                                  photoDAO: PhotoDAO, pointOfInterestDAO: PointOfInterestDAO): PropertyRepository {
        return PropertyRepository(propertyLocalDAO, agentDAO, photoDAO, pointOfInterestDAO)
    }

}

