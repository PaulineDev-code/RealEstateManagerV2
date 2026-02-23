package com.openclassrooms.realestatemanagerv2

import android.content.Context
import androidx.room.Room
import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.dao.MediaDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestCrossRefDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.data.dao.ProviderDAO
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)

object TestDbModule {

    @Provides
    @Singleton
    fun provideInMemoryDb(
        @ApplicationContext context: Context
    ): MyDatabase =
        Room.inMemoryDatabaseBuilder(context, MyDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides fun providePropertyDao(db: MyDatabase): PropertyLocalDAO = db.propertyDAO()
    @Provides fun provideAgentDao(db: MyDatabase): AgentDAO = db.agentDAO()
    @Provides fun provideMediaDao(db: MyDatabase): MediaDAO = db.mediaDAO()
    @Provides fun providePoiDao(db: MyDatabase): PointOfInterestDAO = db.pointOfInterestDAO()
    @Provides fun providePoiXDao(db: MyDatabase): PointOfInterestCrossRefDAO = db.pointOfInterestCrossRefDAO()
    @Provides fun provideProviderDao(db: MyDatabase): ProviderDAO = db.providerDAO()
}
