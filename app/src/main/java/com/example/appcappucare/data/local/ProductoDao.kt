package com.example.appcappucare.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerProductoPorId(id: Int): ProductoEntity?

    @Insert
    suspend fun insertarProducto(producto: ProductoEntity)

    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminarProducto(id: Int)

    @Query(
        "UPDATE productos SET stock = stock + :cantidad " +
        "WHERE id = :id AND :cantidad > 0"
    )
    suspend fun agregarStock(id: Int, cantidad: Int): Int

    @Query(
        "UPDATE productos SET stock = stock - :cantidad " +
        "WHERE id = :id AND :cantidad > 0 AND stock >= :cantidad"
    )
    suspend fun descontarStock(id: Int, cantidad: Int): Int

    // Productos locales que todavia no tienen ID de PostgreSQL.
    @Query("SELECT * FROM productos WHERE idServidor IS NULL ORDER BY id ASC")
    suspend fun obtenerProductosSinSincronizar(): List<ProductoEntity>

    // Guardar la relacion entre el ID local y el ID del servidor.
    @Query("UPDATE productos SET idServidor = :idServidor WHERE id = :id AND idServidor IS NULL")
    suspend fun vincularProductoConServidor(id: Int, idServidor: Long): Int
}
