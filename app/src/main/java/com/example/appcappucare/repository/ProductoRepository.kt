package com.example.appcappucare.repository

import com.example.appcappucare.data.local.ProductoDao
import com.example.appcappucare.data.local.ProductoEntity
import com.example.appcappucare.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductoRepository(
    private val dao: ProductoDao
) {

    val productos: Flow<List<Producto>> = dao.obtenerProductos().map { lista ->
        lista.map { entidad ->
            Producto(
                id = entidad.id,
                nombre = entidad.nombre,
                categoria = entidad.categoria,
                precio = entidad.precio,
                stock = entidad.stock,
                stockMinimo = entidad.stockMinimo
            )
        }
    }

    suspend fun agregarProducto(producto: Producto) {
        dao.insertarProducto(
            ProductoEntity(
                nombre = producto.nombre,
                categoria = producto.categoria,
                precio = producto.precio,
                stock = producto.stock,
                stockMinimo = producto.stockMinimo
            )
        )
    }

    suspend fun registrarMovimiento(
        id: Int,
        cantidad: Int,
        entrada: Boolean
    ): Boolean {
        if (cantidad <= 0) return false

        val filasActualizadas = if (entrada) {
            dao.agregarStock(id, cantidad)
        } else {
            dao.descontarStock(id, cantidad)
        }

        return filasActualizadas > 0
    }
}
