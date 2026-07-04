package com.example.divelog.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.divelog.domain.model.Dive
import com.example.divelog.domain.model.DiveMediaNote
import com.example.divelog.data.local.DiveMediaNoteDao

@Database(
    entities = [
        Dive::class,
        DiveMediaNote::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DiveDatabase : RoomDatabase() {

    abstract fun diveDao(): DiveDao
    abstract fun diveMediaNoteDao(): DiveMediaNoteDao

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
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}