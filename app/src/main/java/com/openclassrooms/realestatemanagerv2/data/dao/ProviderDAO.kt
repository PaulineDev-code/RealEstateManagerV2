package com.openclassrooms.realestatemanagerv2.data.dao

import androidx.room.Dao
import androidx.room.Query
import android.database.Cursor
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface ProviderDAO {
    @Query("SELECT * FROM property_with_details_view")
    fun queryAll(): Cursor

    @Query("SELECT * FROM property_with_details_view WHERE id = :id")
    fun queryById(id: String): Cursor

    @RawQuery
    fun rawCursor(query: SupportSQLiteQuery): Cursor
}