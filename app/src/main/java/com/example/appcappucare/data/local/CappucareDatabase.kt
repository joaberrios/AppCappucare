package com.example.appcappucare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ProductoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CappucareDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {

        @Volatile
        private var INSTANCE: CappucareDatabase? = null

        fun obtenerBaseDatos(context: Context): CappucareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    CappucareDatabase::class.java,
                    "cappucare_database"
                ).build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}
