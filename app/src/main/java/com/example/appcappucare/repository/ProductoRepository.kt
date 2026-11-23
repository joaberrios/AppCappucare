package com.example.appcappucare.repository

import com.example.appcappucare.data.local.ProductoDao
import com.example.appcappucare.data.local.ProductoEntity
import com.example.appcappucare.data.remote.ProductoDto
import com.example.appcappucare.data.remote.RetrofitClient
import com.example.appcappucare.model.Producto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductoRepository(
    private val dao: ProductoDao,
    private val api: com.example.appcappucare.data.remote.ProductoApi =
        RetrofitClient.productoApi
) {
    private val mutexMovimientos = Mutex()

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
    ): Boolean = mutexMovimientos.withLock {

        if (cantidad <= 0) {
            return@withLock false
        }

        val producto = dao.obtenerProductoPorId(id)
            ?: return@withLock false

        // Comprobar que hay unidades suficientes.
        if (!entrada && producto.stock < cantidad) {
            return@withLock false
        }

        // Si esta vinculado, actualizar primero PostgreSQL.
        producto.idServidor?.let { idServidor ->

            val productoServidor =
                api.buscarProducto(idServidor)

            // Evitar cambios cuando las bases tienen stock diferente.
            if (productoServidor.stock != producto.stock) {
                throw IllegalStateException(
                    "El stock local y el del servidor son diferentes. Revisar antes de continuar."
                )
            }

            val nuevoStock = if (entrada) {
                producto.stock + cantidad
            } else {
                producto.stock - cantidad
            }

            if (nuevoStock < 0) {
                return@withLock false
            }

            val actualizado =
                api.actualizarProducto(
                    idServidor,
                    productoServidor.copy(stock = nuevoStock)
                )

            if (actualizado.stock != nuevoStock) {
                throw IllegalStateException(
                    "El servidor no confirmo el stock esperado."
                )
            }
        }

        // Actualizar Room conservando todos los demas datos.
        val filas = if (entrada) {
            dao.agregarStock(id, cantidad)
        } else {
            dao.descontarStock(id, cantidad)
        }

        if (filas != 1) {
            throw IllegalStateException(
                "No se pudo actualizar Room. Revisar ambas bases antes de repetir."
            )
        }

        return@withLock true
    }

    suspend fun obtenerProductoPorId(id: Int): Producto? {
        val entidad = dao.obtenerProductoPorId(id) ?: return null
        return Producto(entidad.id, entidad.nombre, entidad.categoria, entidad.precio, entidad.stock, entidad.stockMinimo, entidad.fotoUri)
    }

    suspend fun actualizarProducto(producto: Producto) {
        val existente = dao.obtenerProductoPorId(producto.id)
            ?: throw IllegalArgumentException("Producto no encontrado")

        // Si el producto esta vinculado, actualizar PostgreSQL.
        existente.idServidor?.let { idServidor ->
            val datosServidor = ProductoDto(
                id = idServidor,
                nombre = producto.nombre,
                categoria = producto.categoria,
                precio = producto.precio,
                stock = existente.stock,
                stockMinimo = producto.stockMinimo
            )

            api.actualizarProducto(
                idServidor,
                datosServidor
            )
        }

        // Actualizar Room conservando stock y fotografia.
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
        val existente = dao.obtenerProductoPorId(id)
            ?: throw IllegalArgumentException("Producto no encontrado")

        // Eliminar primero del servidor si esta vinculado.
        existente.idServidor?.let { idServidor ->
            val respuesta = api.eliminarProducto(idServidor)

            if (!respuesta.isSuccessful) {
                throw IllegalStateException(
                    "PostgreSQL rechazo la eliminacion: ${respuesta.code()}"
                )
            }
        }

        // Eliminar de Room cuando la operacion remota fue correcta.
        dao.eliminarProducto(id)
    }

    // Contar los productos locales que faltan por vincular.
    suspend fun contarProductosPendientes(): Int {
        return dao.obtenerProductosSinSincronizar().size
    }

    // Consultar productos de PostgreSQL sin modificar Room.
    suspend fun consultarProductosServidor(): List<ProductoDto> {
        return api.listarProductos()
    }

    // Sincronizar manualmente un producto por su ID local.
    suspend fun sincronizarProductoPendiente(idLocal: Int): String {
        val producto = dao.obtenerProductoPorId(idLocal)
            ?: return "Producto local no encontrado"

        if (producto.idServidor != null) {
            return "Este producto ya esta vinculado al servidor"
        }

        // Consultar primero los registros del servidor.
        val productosServidor = consultarProductosServidor()

        val coincidencias = productosServidor.filter {
            it.nombre == producto.nombre &&
            it.categoria == producto.categoria &&
            it.precio == producto.precio &&
            it.stock == producto.stock &&
            it.stockMinimo == producto.stockMinimo
        }

        if (coincidencias.isNotEmpty()) {
            return "Posible duplicado. Revisar PostgreSQL antes de continuar"
        }

        val datos = ProductoDto(
            nombre = producto.nombre,
            categoria = producto.categoria,
            precio = producto.precio,
            stock = producto.stock,
            stockMinimo = producto.stockMinimo
        )

        val creado = api.agregarProducto(datos)

        val idServidor = creado.id
            ?: throw IllegalStateException(
                "Servidor no devolvio ID. Revisar antes de reintentar"
            )

        val filas = dao.vincularProductoConServidor(
            idLocal,
            idServidor
        )

        if (filas != 1) {
            throw IllegalStateException(
                "Producto creado en PostgreSQL, pero no vinculado en Room"
            )
        }

        return "Producto sincronizado: ${producto.nombre}"
    }
}
