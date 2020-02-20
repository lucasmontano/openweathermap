package com.lucasmontano.openweathermap.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lucasmontano.openweathermap.core.database.dao.BookmarkDao
import com.lucasmontano.openweathermap.core.database.entities.Bookmark

@Database(entities = [Bookmark::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val bookmarkDao: BookmarkDao

}
