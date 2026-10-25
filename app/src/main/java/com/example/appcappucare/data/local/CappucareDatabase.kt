package com.example.appcappucare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ProductoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CappucareDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE productos ADD COLUMN fotoUri TEXT"
                )
            }
        }

        @Volatile
        private var INSTANCE: CappucareDatabase? = null

        fun obtenerBaseDatos(context: Context): CappucareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    CappucareDatabase::class.java,
                    "cappucare_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}
