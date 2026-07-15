package com.example.happyplaces.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.happyplaces.data.model.HappyPlace
import com.example.happyplaces.data.model.Note

@Database(
    entities = [
        HappyPlace::class,
        Note::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HappyPlacesDatabase : RoomDatabase() {

    abstract fun happyPlaceDao(): HappyPlaceDao

    abstract fun noteDao(): NoteDao

    companion object {

        @Volatile
        private var INSTANCE: HappyPlacesDatabase? = null

        /**
         * Fügt der vorhandenen Tabelle die neue Notizspalte hinzu.
         */
        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        ALTER TABLE happy_places
                        ADD COLUMN personalNote TEXT NOT NULL DEFAULT ''
                        """.trimIndent()
                    )
                }
            }

        fun getDatabase(
            context: Context
        ): HappyPlacesDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HappyPlacesDatabase::class.java,
                    "happy_places_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}