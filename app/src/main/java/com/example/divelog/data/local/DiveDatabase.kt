package com.example.divelog.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.divelog.data.model.Dive

@Database(
    entities = [Dive::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DiveDatabase : RoomDatabase() {

    abstract fun diveDao(): DiveDao

    companion object {
        @Volatile
        private var INSTANCE: DiveDatabase? = null

        fun getDatabase(context: Context): DiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiveDatabase::class.java,
                    "dive_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}