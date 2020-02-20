package com.lucasmontano.openweathermap.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lucasmontano.openweathermap.core.database.entities.Bookmark

@Dao
interface BookmarkDao {

    @Insert
    suspend fun insert(bookmark: Bookmark): Long

    @Delete
    suspend fun delete(bookmark: Bookmark)

    @Query("SELECT * FROM bookmarks")
    suspend fun getAll(): List<Bookmark>
}
