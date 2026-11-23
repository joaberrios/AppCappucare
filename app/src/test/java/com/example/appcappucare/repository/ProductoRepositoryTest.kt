package com.example.appcappucare.repository

import com.example.appcappucare.data.local.ProductoDao
import com.example.appcappucare.data.local.ProductoEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class ProductoRepositoryTest {

    private lateinit var dao: ProductoDao
    private lateinit var repository: ProductoRepository

    @Before
    fun preparar() {
        dao = mock(ProductoDao::class.java)

        `when`(dao.obtenerProductos()).thenReturn(flowOf(emptyList()))

        repository = ProductoRepository(dao)
    }

    @Test
    fun productoInexistenteDevuelveNull(): Unit = runBlocking {
        val resultado = repository.obtenerProductoPorId(999)
        assertNull(resultado)
    }

    @Test
    fun cantidadCeroNoModificaStock(): Unit = runBlocking {
        val resultado = repository.registrarMovimiento(1, 0, true)

        assertFalse(resultado)
        verify(dao, never()).agregarStock(anyInt(), anyInt())
    }

    @Test
    fun cantidadNegativaNoModificaStock(): Unit = runBlocking {
        val resultado = repository.registrarMovimiento(1, -5, false)

        assertFalse(resultado)
        verify(dao, never()).descontarStock(anyInt(), anyInt())
    }

    @Test
    fun productoInexistenteNoPermiteMovimiento(): Unit = runBlocking {
        val resultado = repository.registrarMovimiento(999, 2, true)

        assertFalse(resultado)
    }

    @Test
    fun ventaMayorAlStockEsRechazada(): Unit = runBlocking {
        val producto = ProductoEntity(
            id = 1,
            nombre = "Paracetamol",
            categoria = "Medicamentos",
            precio = 1000.0,
            stock = 3,
            stockMinimo = 2
        )

        `when`(dao.obtenerProductoPorId(1)).thenReturn(producto)

        val resultado = repository.registrarMovimiento(1, 5, false)

        assertFalse(resultado)
        verify(dao, never()).descontarStock(anyInt(), anyInt())
    }

    @Test
    fun productoSinSincronizarDevuelveCantidadPendiente(): Unit = runBlocking {
        val producto = ProductoEntity(
            id = 1,
            nombre = "Producto local",
            categoria = "Prueba",
            precio = 1500.0,
            stock = 5,
            stockMinimo = 2
        )

        `when`(dao.obtenerProductosSinSincronizar())
            .thenReturn(listOf(producto))

        val resultado = repository.contarProductosPendientes()

        assertEquals(1, resultado)
    }

    private fun crearEntidad(): ProductoEntity {
        return ProductoEntity(
            id = 1,
            nombre = "Paracetamol",
            categoria = "Medicamentos",
            precio = 1000.0,
            stock = 10,
            stockMinimo = 5
        )
    }

    @Test
    fun entradaLocalAumentaStock(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(crearEntidad())
        `when`(dao.agregarStock(1, 5)).thenReturn(1)

        val resultado = repository.registrarMovimiento(1, 5, true)

        assertTrue(resultado)
        verify(dao).agregarStock(1, 5)
    }

    @Test
    fun ventaLocalDescuentaStock(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(crearEntidad())
        `when`(dao.descontarStock(1, 4)).thenReturn(1)

        val resultado = repository.registrarMovimiento(1, 4, false)

        assertTrue(resultado)
        verify(dao).descontarStock(1, 4)
    }

    @Test
    fun entradaSinActualizarRoomGeneraError(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(crearEntidad())
        `when`(dao.agregarStock(1, 5)).thenReturn(0)

        try {
            repository.registrarMovimiento(1, 5, true)
            fail("Debia generar un error")
        } catch (error: IllegalStateException) {
            assertTrue(error.message!!.contains("Room"))
        }
    }

    @Test
    fun obtenerProductoExistente(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(crearEntidad())

        val resultado = repository.obtenerProductoPorId(1)

        assertNotNull(resultado)
        assertEquals("Paracetamol", resultado!!.nombre)
        assertEquals(10, resultado.stock)
        assertEquals(5, resultado.stockMinimo)
    }

    @Test
    fun editarProductoLocal(): Unit = runBlocking {
        val entidad = crearEntidad()
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad)

        val producto = com.example.appcappucare.model.Producto(
            id = 1,
            nombre = "Paracetamol actualizado",
            categoria = "Farmacia",
            precio = 2000.0,
            stock = 10,
            stockMinimo = 3
        )

        repository.actualizarProducto(producto)

        verify(dao).actualizarProducto(
            entidad.copy(
                nombre = "Paracetamol actualizado",
                categoria = "Farmacia",
                precio = 2000.0,
                stockMinimo = 3
            )
        )
    }

    @Test
    fun eliminarProductoLocal(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(crearEntidad())

        repository.eliminarProducto(1)

        verify(dao).eliminarProducto(1)
    }

    @Test
    fun editarProductoInexistenteGeneraError(): Unit = runBlocking {
        try {
            repository.actualizarProducto(
                com.example.appcappucare.model.Producto(
                    999, "Prueba", "Farmacia", 1000.0, 5, 2
                )
            )
            fail("Debia generar un error")
        } catch (error: IllegalArgumentException) {
            assertEquals("Producto no encontrado", error.message)
        }
    }

    @Test
    fun eliminarProductoInexistenteGeneraError(): Unit = runBlocking {
        try {
            repository.eliminarProducto(999)
            fail("Debia generar un error")
        } catch (error: IllegalArgumentException) {
            assertEquals("Producto no encontrado", error.message)
        }
    }
}
