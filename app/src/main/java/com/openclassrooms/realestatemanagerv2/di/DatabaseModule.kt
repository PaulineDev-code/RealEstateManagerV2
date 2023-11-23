package com.openclassrooms.realestatemanagerv2.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanagerv2.data.database.Database
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyLocalDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.chrono.HijrahChronology.INSTANCE

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun providePropertyDao(
        @ApplicationContext context: Context
    ): PropertyLocalDAO {
        // Create and return an instance of ContactDao
        val db = Room.databaseBuilder(
            context,
            Database::class.java,
            "database"
        )/*.addCallback(callback : RoomDatabase.Callback() {
            {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Populate the database with initial data
                    // For example:
                    val propertyDao = INSTANCE?.propertyDAO
                    propertyDao?.insert(Property(1, "Apartment",))
                    propertyDao?.insert(User("Jane Smith"))
        })*/
            .build()
        return db.propertyDao
    }
}
