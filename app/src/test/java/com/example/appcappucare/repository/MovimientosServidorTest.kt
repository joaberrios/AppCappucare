package com.example.appcappucare.repository

import com.example.appcappucare.data.local.ProductoDao
import com.example.appcappucare.data.local.ProductoEntity
import com.example.appcappucare.data.remote.ProductoApi
import com.example.appcappucare.data.remote.ProductoDto
import com.example.appcappucare.model.Producto
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Response

class MovimientosServidorTest {

    private lateinit var dao: ProductoDao
    private lateinit var api: ProductoApi
    private lateinit var repositorio: ProductoRepository

    private fun entidad(stock: Int = 10) = ProductoEntity(
        id = 1,
        nombre = "Paracetamol",
        categoria = "Medicamentos",
        precio = 1000.0,
        stock = stock,
        stockMinimo = 5,
        idServidor = 4L
    )

    private fun dto(stock: Int = 10) = ProductoDto(
        id = 4L,
        nombre = "Paracetamol",
        categoria = "Medicamentos",
        precio = 1000.0,
        stock = stock,
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
    fun entradaSincronizada(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad())
        `when`(api.buscarProducto(4L)).thenReturn(dto())
        `when`(api.actualizarProducto(4L, dto(15))).thenReturn(dto(15))
        `when`(dao.agregarStock(1, 5)).thenReturn(1)

        assertTrue(repositorio.registrarMovimiento(1, 5, true))

        verify(api).actualizarProducto(4L, dto(15))
        verify(dao).agregarStock(1, 5)
    }

    @Test
    fun ventaSincronizada(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad())
        `when`(api.buscarProducto(4L)).thenReturn(dto())
        `when`(api.actualizarProducto(4L, dto(7))).thenReturn(dto(7))
        `when`(dao.descontarStock(1, 3)).thenReturn(1)

        assertTrue(repositorio.registrarMovimiento(1, 3, false))

        verify(api).actualizarProducto(4L, dto(7))
        verify(dao).descontarStock(1, 3)
    }

    @Test
    fun rechazaStockDistintoEntreBases(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad(10))
        `when`(api.buscarProducto(4L)).thenReturn(dto(8))

        try {
            repositorio.registrarMovimiento(1, 2, false)
            fail("Debia detectar diferencias de stock")
        } catch (error: IllegalStateException) {
            assertTrue(error.message!!.contains("diferentes"))
        }

        verify(dao, never()).descontarStock(anyInt(), anyInt())
    }

    @Test
    fun rechazaVentaSinUnidades(): Unit = runBlocking {
        `when`(dao.obtenerProductoPorId(1)).thenReturn(entidad(2))

        assertFalse(repositorio.registrarMovimiento(1, 5, false))

        verifyNoInteractions(api)
    }

    @Test
    fun edicionSincronizada(): Unit = runBlocking {
        val original = entidad()

        `when`(dao.obtenerProductoPorId(1)).thenReturn(original)

        val editado = Producto(
            id = 1,
            nombre = "Paracetamol actualizado",
            categoria = "Farmacia",
            precio = 2000.0,
            stock = 10,
            stockMinimo = 3
        )

        val respuestaServidor = ProductoDto(
            id = 4L,
            nombre = "Paracetamol actualizado",
            categoria = "Farmacia",
            precio = 2000.0,
            stock = 10,
            stockMinimo = 3
        )

        `when`(
            api.actualizarProducto(4L, respuestaServidor)
        ).thenReturn(respuestaServidor)

        repositorio.actualizarProducto(editado)

        verify(api).actualizarProducto(
            4L,
            respuestaServidor
        )

        verify(dao).actualizarProducto(
            original.copy(
                nombre = "Paracetamol actualizado",
                categoria = "Farmacia",
                precio = 2000.0,
                stockMinimo = 3
            )
        )
    }
}
