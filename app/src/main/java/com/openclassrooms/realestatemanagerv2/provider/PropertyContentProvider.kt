package com.openclassrooms.realestatemanagerv2.provider


import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.InvalidationTracker
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.data.dao.ProviderDAO
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class PropertyContentProvider : ContentProvider() {
    private lateinit var dao: ProviderDAO
    private lateinit var db: MyDatabase

    private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        // Liste
        addURI(PropertyProviderContract.AUTHORITY, "properties", CODE_PROPERTIES)
        // Détail par ID (UUID String -> wildcard *)
        addURI(PropertyProviderContract.AUTHORITY, "properties/*", CODE_PROPERTY_ID)
    }

    override fun onCreate(): Boolean {
        val app = context?.applicationContext as? Application ?: return false
        val entry = EntryPointAccessors.fromApplication(app, ProviderEntryPoint::class.java)
        db  = entry.database()
        dao = db.providerDAO()

        // Notifier automatiquement les observateurs quand la DB change
        db.invalidationTracker.addObserver(object : InvalidationTracker.Observer(
            "properties", "medias", "agents", "point_of_interest_cross_ref"
        ) {
            override fun onInvalidated(tables: Set<String>) {
                context?.contentResolver?.notifyChange(
                    PropertyProviderContract.Properties.URI, null
                )
            }
        })
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = when (matcher.match(uri)) {
            CODE_PROPERTIES -> dao.queryAll()
            CODE_PROPERTY_ID -> {
                val id = uri.lastPathSegment ?: return null
                dao.queryById(id)
            }
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

    // Read-only provider
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
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProviderEntryPoint {
    fun database(): MyDatabase
}
