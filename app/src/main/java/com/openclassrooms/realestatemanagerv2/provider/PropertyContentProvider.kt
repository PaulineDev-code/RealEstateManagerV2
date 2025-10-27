package com.openclassrooms.realestatemanagerv2.provider


import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.room.InvalidationTracker
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.data.dao.ProviderDAO
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class PropertyContentProvider : ContentProvider() {

    @Volatile private var initialized = false

    @Volatile private var db: MyDatabase? = null
    @Volatile private var dao: ProviderDAO? = null

    private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(PropertyProviderContract.AUTHORITY, "properties", CODE_PROPERTIES)
        addURI(PropertyProviderContract.AUTHORITY, "properties/*", CODE_PROPERTY_ID)
    }

    override fun onCreate() = true

    private fun ensureInitialized() {
        if (db != null && dao != null) return
        synchronized(this) {
            if (db == null || dao == null) {
                val app = context?.applicationContext as Application
                val entry = EntryPointAccessors.fromApplication(app, ProviderEntryPoint::class.java)
                val database = entry.database()
                db = database
                dao = db?.providerDAO()



                /*database.invalidationTracker.addObserver(object : InvalidationTracker.Observer(
                    "properties","medias","agents","point_of_interest_cross_ref"
                ) {
                    override fun onInvalidated(tables: Set<String>) {
                        context?.contentResolver?.notifyChange(
                            PropertyProviderContract.Properties.URI, null
                        )
                    }
                })*/
            }
            if (!initialized && db != null) {
                registerInvalidationObserver(db!!)
                initialized = true
            }
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        ensureInitialized()
        val d = dao ?: return null
        val cursor = when (matcher.match(uri)) {
            CODE_PROPERTIES  -> d.queryAll()
            CODE_PROPERTY_ID -> d.queryById(uri.lastPathSegment ?: return null)
            else -> null
        }
        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String = when (matcher.match(uri)) {
        CODE_PROPERTIES -> PropertyProviderContract.Properties.MIME_DIR
        CODE_PROPERTY_ID -> PropertyProviderContract.Properties.MIME_ITEM
        else -> throw IllegalArgumentException("Unknown URI $uri")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? =
        throw UnsupportedOperationException("Read-only")

    override fun update(uri: Uri, values: ContentValues?, sel: String?, args: Array<out String>?): Int =
        throw UnsupportedOperationException("Read-only")

    override fun delete(uri: Uri, sel: String?, args: Array<out String>?): Int =
        throw UnsupportedOperationException("Read-only")

    companion object {
        private const val CODE_PROPERTIES  = 1
        private const val CODE_PROPERTY_ID = 2
    }

    private fun registerInvalidationObserver(database: MyDatabase, ctx: Context? = context) {
        ctx?.contentResolver ?: run {
            Log.e("ContentProvider", "Cannot register observer: ContentResolver is null")
            return
        }

        database.invalidationTracker.addObserver(object : InvalidationTracker.Observer(
            "properties", "medias", "agents", "point_of_interest_cross_ref"
        ) {
            override fun onInvalidated(tables: Set<String>) {
                Log.d("ContentProvider", "onInvalidated called for tables: $tables")
                context?.contentResolver?.notifyChange(
                    PropertyProviderContract.Properties.URI, null
                )?.also {
                    Log.d("ContentProvider", "notifyChange sent")
                }
            }
        })
    }

    // Optionnel : hook de test si tu veux forcer une DB spécifique (pas nécessaire avec le module de test Hilt)
    @VisibleForTesting
    internal fun setDatabaseForTesting(database: MyDatabase, testContext: Context) {
        synchronized(this) {
            db = database
            dao = database.providerDAO()
            registerInvalidationObserver(database, testContext)
            initialized = true
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProviderEntryPoint {
    fun database(): MyDatabase
}

