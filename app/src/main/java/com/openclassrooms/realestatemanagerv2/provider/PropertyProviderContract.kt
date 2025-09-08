package com.openclassrooms.realestatemanagerv2.provider

import android.net.Uri
import android.provider.BaseColumns
import androidx.core.net.toUri

object PropertyProviderContract {
    const val AUTHORITY   = "com.openclassrooms.realestatemanagerv2.provider"
    val CONTENT_URI: Uri = "content://$AUTHORITY".toUri()

    object Properties : BaseColumns {
        const val PATH = "properties"
        val URI: Uri = CONTENT_URI.buildUpon().appendPath(PATH).build()

        // MIME
        const val MIME_DIR  = "vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH"
        const val MIME_ITEM = "vnd.android.cursor.item/vnd.$AUTHORITY.$PATH"

        // Colonnes exposées
        const val COL_ID            = "id"
        const val COL_TYPE          = "type"
        const val COL_PRICE         = "price"
        const val COL_AREA          = "area"
        const val COL_ROOMS         = "numberOfRooms"
        const val COL_DESC          = "description"
        const val COL_ADDRESS       = "address"
        const val COL_STATUS        = "status"
        const val COL_ENTRY_DATE    = "entryDate"
        const val COL_SALE_DATE     = "saleDate"
        const val COL_AGENT_ID      = "agentId"
        const val COL_AGENT_NAME    = "agent_name"
        const val COL_AGENT_EMAIL   = "agent_email"
        const val COL_AGENT_PHONE   = "agent_phone"
        const val COL_PHOTOS_COUNT  = "photos_count"
        const val COL_VIDEOS_COUNT  = "videos_count"
        const val COL_POI_IDS       = "poi_ids"     // "id1|id2|id3"
        const val COL_MEDIA_URLS    = "media_urls"  // "url1|url2|url3"
    }
}