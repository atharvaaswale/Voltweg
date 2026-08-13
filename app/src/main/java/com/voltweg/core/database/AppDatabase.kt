package com.voltweg.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.voltweg.core.database.dao.SearchHistoryDao
import com.voltweg.core.database.dao.StationDao
import com.voltweg.core.database.entity.SearchHistoryEntity
import com.voltweg.core.database.entity.StationEntity

@Database(
    entities = [StationEntity::class, SearchHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "voltweg.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
