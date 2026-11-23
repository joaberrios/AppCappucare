package com.example.appcappucare.repository

import com.example.appcappucare.data.local.ProductoDao
import com.example.appcappucare.data.local.ProductoEntity
import com.example.appcappucare.data.remote.ProductoApi
import com.example.appcappucare.data.remote.ProductoDto
import com.example.appcappucare.model.Producto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class SincronizacionRepositoryTest {

    private lateinit var dao: ProductoDao
    private lateinit var api: ProductoApi
    private lateinit var repositorio: ProductoRepository

    private fun entidad(idServidor: Long? = null) = ProductoEntity(
        id = 1,
        nombre = "Paracetamol",
        categoria = "Medicamentos",
        precio = 1000.0,
        stock = 10,
        stockMinimo = 5,
        idServidor = idServidor
    )

    private fun dto(id: Long? = null) = ProductoDto(
        id = id,
        nombre = "Paracetamol",
        categoria = "Medicamentos",
        precio = 1000.0,
        stock = 10,
        stockMinimo = 5
    )

    @Before
    fun preparar() {
        dao = mock(ProductoDao::class.java)
        api = mock(ProductoApi::class.java)

        `when`(dao.obtenerProductos()).thenReturn(flowOf(emptyList()))

        repositorio = ProductoRepository(dao, api)
    }

    @Test
    fun productoLocalInexistente(): Unit = runBlocking {
        val resultado = repositorio.sincronizarProductoPendiente(999)

        assertEquals("Producto local no encontrado", resultado)
        verifyNoInteractions(api)
    }

    @Test
    fun productoYaSincronizado(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad(4L))

        val resultado = repositorio.sincronizarProductoPendiente(1)

        assertEquals(
            "Este producto ya esta vinculado al servidor",
            resultado
        )
        verifyNoInteractions(api)
    }

    @Test
    fun productoDuplicadoEnServidor(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad())
        `when`(api.listarProductos()).thenReturn(listOf(dto(8L)))

        val resultado = repositorio.sincronizarProductoPendiente(1)

        assertTrue(resultado.contains("Posible duplicado"))
        verify(api, never()).agregarProducto(dto())
    }

    @Test
    fun sincronizacionCorrecta(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad())
        `when`(api.listarProductos()).thenReturn(emptyList())
        `when`(api.agregarProducto(dto())).thenReturn(dto(10L))
        `when`(dao.vincularProductoConServidor(1, 10L))
            .thenReturn(1)

        val resultado = repositorio.sincronizarProductoPendiente(1)

        assertEquals("Producto sincronizado: Paracetamol", resultado)
        verify(dao).vincularProductoConServidor(1, 10L)
    }

    @Test
    fun servidorNoDevuelveId(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad())
        `when`(api.listarProductos()).thenReturn(emptyList())
        `when`(api.agregarProducto(dto())).thenReturn(dto())

        try {
            repositorio.sincronizarProductoPendiente(1)
            fail("Debia rechazar una respuesta sin ID")
        } catch (error: IllegalStateException) {
            assertTrue(error.message!!.contains("no devolvio ID"))
        }
    }

    @Test
    fun fallaVinculacionEnRoom(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad())
        `when`(api.listarProductos()).thenReturn(emptyList())
        `when`(api.agregarProducto(dto())).thenReturn(dto(10L))
        `when`(dao.vincularProductoConServidor(1, 10L))
            .thenReturn(0)

        try {
            repositorio.sincronizarProductoPendiente(1)
            fail("Debia detectar el error de Room")
        } catch (error: IllegalStateException) {
            assertTrue(error.message!!.contains("no vinculado en Room"))
        }
    }

    @Test
    fun consultarProductosDelServidor(): Unit = runBlocking {
        `when`(api.listarProductos()).thenReturn(listOf(dto(3L)))

        val productos = repositorio.consultarProductosServidor()

        assertEquals(1, productos.size)
        assertEquals(3L, productos[0].id)
    }

    @Test
    fun sinProductosPendientes(): Unit = runBlocking {
        `when`(dao.obtenerProductosSinSincronizar())
            .thenReturn(emptyList())

        assertEquals(0, repositorio.contarProductosPendientes())
    }

    @Test
    fun agregarProductoGuardaEnRoom(): Unit = runBlocking {
        val producto = Producto(
            id = 0,
            nombre = "Paracetamol",
            categoria = "Medicamentos",
            precio = 1000.0,
            stock = 10,
            stockMinimo = 5
        )

        repositorio.agregarProducto(producto)

        verify(dao).insertarProducto(
            ProductoEntity(
                nombre = "Paracetamol",
                categoria = "Medicamentos",
                precio = 1000.0,
                stock = 10,
                stockMinimo = 5
            )
        )
    }

    @Test
    fun listarProductosLocales(): Unit = runBlocking {
        `when`(dao.obtenerProductos())
            .thenReturn(flowOf(listOf(entidad())))

        val repositorioLocal = ProductoRepository(dao, api)
        val productos = repositorioLocal.productos.first()

        assertEquals(1, productos.size)
        assertEquals("Paracetamol", productos[0].nombre)
        assertEquals(10, productos[0].stock)
    }
}
