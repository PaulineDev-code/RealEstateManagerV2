@file:Suppress("SameParameterValue")

package com.openclassrooms.realestatemanagerv2

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.MediaEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestCrossRef
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.di.DatabaseModule
import com.openclassrooms.realestatemanagerv2.provider.PropertyContentProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
@RunWith(AndroidJUnit4::class)
class PropertyContentProviderTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var db: MyDatabase

    lateinit var contentProvider: PropertyContentProvider
    private val context: Context
        get() = ApplicationProvider.getApplicationContext()
    private val contentResolver get() = context.contentResolver

    private fun providerUri(path: String = "properties"): Uri =
        Uri.parse("content://${context.packageName}.provider/$path")

    @Before
    fun setup() = runBlocking {
        hiltRule.inject()
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        contentProvider = PropertyContentProvider().apply {
            attachInfo(appContext, null)    // ok dans les tests
            setDatabaseForTesting(db, appContext)    // branche la DB in-memory injectée
        }
        db.clearAllTables()
        seedDb()
    }

    private suspend fun seedDb() {

        // --- Seed minimal ---
        val agentDao = db.agentDAO()
        val propDao = db.propertyDAO()
        val mediaDao = db.mediaDAO()
        val poiDao = db.pointOfInterestDAO()
        val poiXDao = db.pointOfInterestCrossRefDAO()


        agentDao.insertAgent(
            AgentEntity(
                id = "A1", name = "Alice", phoneNumber = "0000", email = "alice@oc.com"
            )
        )

        propDao.insertProperty(
            PropertyLocalEntity(
                id = "P1",
                type = "Apartment",
                price = 250000.0,
                area = 70.0,
                numberOfRooms = 3,
                description = "Nice flat",
                address = "1 rue de Paris",
                latitude = 48.8566,
                longitude = 2.3522,
                status = "Available",
                entryDate = 1_700_000_000L,
                saleDate = null,
                agentId = "A1"
            )
        )

        mediaDao.insertMedias(
            listOf(
                MediaEntity(
                    id = "M1", type = "photo", description = "Front",
                    mediaUrl = "https://img/1.jpg", propertyLocalId = "P1"
                ),

                MediaEntity(
                    id = "M2", type = "video", description = "Tour",
                    mediaUrl = "https://vid/2.mp4", propertyLocalId = "P1"
                )
            )
        )

        poiDao.insertPointsOfInterest(
            listOf(
                PointOfInterestEntity(
                    "PARK",
                    R.string.park
                ),
                PointOfInterestEntity(
                    "HOSPITAL",
                    R.string.hospital
                )
            )
        )

        poiXDao.insertAll(
            listOf(
                PointOfInterestCrossRef(
                    "P1",
                    "PARK"
                ),
                PointOfInterestCrossRef(
                    "P1",
                    "HOSPITAL"
                )
            )
        )
    }

    @Test
    fun queryAll_returnsExpectedColumnsAndValues() {
        contentResolver.query(providerUri(), null, null, null, null).use { c ->
            Assert.assertNotNull("Cursor null", c)
            c!!; Assert.assertTrue("Cursor vide", c.moveToFirst())

            for (i in 0 until c.columnCount) {
                val columnName = c.getColumnName(i)
                val value = when {
                    c.isNull(i) -> "NULL"
                    else -> c.getString(i)
                }
                Log.d("TEST", "$columnName = $value")
            }

            // Colonnes de la @DatabaseView (telles que définies)
            Assert.assertEquals("P1", c.getString(c.getColumnIndexOrThrow("id")))
            Assert.assertEquals("Apartment", c.getString(c.getColumnIndexOrThrow("type")))
            Assert.assertEquals(250000.0, c.getDouble(c.getColumnIndexOrThrow("price")), 0.001)

            Assert.assertEquals(1, c.getInt(c.getColumnIndexOrThrow("photos_count")))
            Assert.assertEquals(1, c.getInt(c.getColumnIndexOrThrow("videos_count")))

            val poiIds = c.getString(c.getColumnIndexOrThrow("poi_ids"))
            val mediaUrls = c.getString(c.getColumnIndexOrThrow("media_urls"))

            val poiSet = poiIds?.split(',')?.toSet() ?: emptySet()
            Assert.assertEquals(setOf("HOSPITAL", "PARK"), poiSet)

            val mediaSet = mediaUrls?.split(',')?.toSet() ?: emptySet()
            Assert.assertEquals(setOf("https://img/1.jpg", "https://vid/2.mp4"), mediaSet)
        }
    }

    @Test
    fun queryById_returnsSingleRow() {
        contentResolver.query(providerUri("properties/P1"), null, null, null, null).use { c ->
            Assert.assertNotNull("Cursor null", c)
            c!!; Assert.assertTrue(c.moveToFirst())
            Assert.assertEquals("P1", c.getString(c.getColumnIndexOrThrow("id")))
            Assert.assertFalse("Plus d'une ligne renvoyée", c.moveToNext())
        }
    }

    @Test
    fun contentObserver_notifiedOnDbChange() {
        val uri = providerUri()
        val latch = CountDownLatch(1)

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) { latch.countDown() }
        }
        contentResolver.registerContentObserver(uri, true, observer)

        // Modifie la DB -> Room InvalidationTracker -> provider.notifyChange()
        runBlocking {
            db.propertyDAO().updateProperty(
                PropertyLocalEntity(
                    id = "P1",
                    type = "Apartment",
                    price = 260000.0, // change
                    area = 70.0,
                    numberOfRooms = 3,
                    description = "Nice flat",
                    address = "1 rue de Paris",
                    latitude = 48.8566,
                    longitude = 2.3522,
                    status = "Available",
                    entryDate = 1_700_000_000L,
                    saleDate = null,
                    agentId = "A1"
                )
            )
        }

        val notified = latch.await(2, TimeUnit.SECONDS)
        contentResolver.unregisterContentObserver(observer)

        Assert.assertTrue("ContentObserver non notifié", notified)
    }

}
