package com.openclassrooms.realestatemanagerv2.provider

import android.net.Uri
import android.provider.BaseColumns
import androidx.core.net.toUri

object PropertyProviderContract {
    const val AUTHORITY: String = "com.openclassrooms.realestatemanagerv2.provider"
    val CONTENT_URI: Uri = "content://$AUTHORITY".toUri()

    object Properties : BaseColumns {
        const val PATH: String = "properties"
        val URI: Uri = CONTENT_URI.buildUpon().appendPath(PATH).build()

        // MIME
        const val MIME_DIR: String = "vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH"
        const val MIME_ITEM: String = "vnd.android.cursor.item/vnd.$AUTHORITY.$PATH"

        // Columns
        const val COL_ID: String = "id"
        const val COL_TYPE: String = "type"
        const val COL_PRICE: String = "price"
        const val COL_AREA: String          = "area"
        const val COL_ROOMS: String         = "numberOfRooms"
        const val COL_DESC: String          = "description"
        const val COL_ADDRESS: String       = "address"
        const val COL_STATUS: String        = "status"
        const val COL_ENTRY_DATE: String    = "entryDate"
        const val COL_SALE_DATE: String     = "saleDate"
        const val COL_AGENT_ID: String      = "agentId"
        const val COL_AGENT_NAME: String    = "agent_name"
        const val COL_AGENT_EMAIL: String   = "agent_email"
        const val COL_AGENT_PHONE: String   = "agent_phone"
        const val COL_PHOTOS_COUNT: String  = "photos_count"
        const val COL_VIDEOS_COUNT: String  = "videos_count"
        const val COL_POI_IDS: String       = "poi_ids"     // "id1|id2|id3"
        const val COL_MEDIA_URLS: String    = "media_urls"  // "url1|url2|url3"
    }
}