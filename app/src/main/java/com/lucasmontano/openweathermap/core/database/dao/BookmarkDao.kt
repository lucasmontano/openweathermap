package com.lucasmontano.openweathermap.core.database.dao

import androidx.room.*
import com.lucasmontano.openweathermap.core.database.entities.Bookmark

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark): Long

    @Delete
    suspend fun delete(bookmark: Bookmark)

    @Query("SELECT * FROM bookmarks")
    suspend fun getAll(): List<Bookmark>
}
