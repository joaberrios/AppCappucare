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
                stockMinimo = entidad.stockMinimo,
                fotoUri = entidad.fotoUri
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
                stockMinimo = producto.stockMinimo,
                fotoUri = producto.fotoUri
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

    suspend fun obtenerProductoPorId(id: Int): Producto? {
        val entidad = dao.obtenerProductoPorId(id) ?: return null
        return Producto(entidad.id, entidad.nombre, entidad.categoria, entidad.precio, entidad.stock, entidad.stockMinimo, entidad.fotoUri)
    }

    suspend fun actualizarProducto(producto: Producto) {
        val existente = dao.obtenerProductoPorId(producto.id)
            ?: throw IllegalArgumentException("Producto no encontrado")

        dao.actualizarProducto(
            existente.copy(
                nombre = producto.nombre,
                categoria = producto.categoria,
                precio = producto.precio,
                stockMinimo = producto.stockMinimo
            )
        )
    }

    suspend fun eliminarProducto(id: Int) {
        dao.eliminarProducto(id)
    }
}
