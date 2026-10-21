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
}
