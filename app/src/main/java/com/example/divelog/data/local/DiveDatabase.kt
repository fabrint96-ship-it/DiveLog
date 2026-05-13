package com.example.divelog.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.divelog.domain.model.Dive

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

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE dives ADD COLUMN diveType TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        fun getDatabase(context: Context): DiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiveDatabase::class.java,
                    "dive_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}