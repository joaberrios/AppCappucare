package com.example.appcappucare.viewmodel

import android.app.Application
import com.example.appcappucare.repository.ProductoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    private val despachador = StandardTestDispatcher()
    private lateinit var repositorio: ProductoRepository
    private lateinit var vm: ProductoViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(despachador)

        repositorio = mock(ProductoRepository::class.java)

        `when`(repositorio.productos)
            .thenReturn(flowOf(emptyList()))

        val aplicacion = mock(Application::class.java)

        vm = ProductoViewModel(aplicacion, repositorio)
    }

    @After
    fun finalizar() {
        Dispatchers.resetMain()
    }

    private fun comprobarError(
        nombre: String = "Paracetamol",
        categoria: String = "Medicamentos",
        precio: String = "1000",
        stock: String = "10",
        stockMinimo: String = "5",
        mensajeEsperado: String
    ) {
        var mensaje = ""
        var correcto = true

        vm.agregarProducto(
            nombre = nombre,
            categoria = categoria,
            precio = precio,
            stock = stock,
            stockMinimo = stockMinimo,
            onResultado = { texto, exito ->
                mensaje = texto
                correcto = exito
            }
        )

        assertFalse(correcto)
        assertEquals(mensajeEsperado, mensaje)
    }

    @Test
    fun rechazaNombreVacio() {
        comprobarError(
            nombre = " ",
            mensajeEsperado = "El nombre es obligatorio"
        )
    }

    @Test
    fun rechazaCategoriaVacia() {
        comprobarError(
            categoria = "",
            mensajeEsperado = "La categoría es obligatoria"
        )
    }

    @Test
    fun rechazaPrecioCero() {
        comprobarError(
            precio = "0",
            mensajeEsperado = "Ingresa un precio válido mayor a cero"
        )
    }

    @Test
    fun rechazaPrecioNegativo() {
        comprobarError(
            precio = "-100",
            mensajeEsperado = "Ingresa un precio válido mayor a cero"
        )
    }

    @Test
    fun rechazaPrecioTexto() {
        comprobarError(
            precio = "abc",
            mensajeEsperado = "Ingresa un precio válido mayor a cero"
        )
    }

    @Test
    fun rechazaStockNegativo() {
        comprobarError(
            stock = "-2",
            mensajeEsperado = "Ingresa un stock válido"
        )
    }

    @Test
    fun rechazaStockNoNumerico() {
        comprobarError(
            stock = "abc",
            mensajeEsperado = "Ingresa un stock válido"
        )
    }

    @Test
    fun rechazaStockMinimoNegativo() {
        comprobarError(
            stockMinimo = "-1",
            mensajeEsperado = "Ingresa un stock mínimo válido"
        )
    }

    @Test
    fun agregarProductoCorrectamente(): Unit = kotlinx.coroutines.test.runTest {
        var mensaje = ""
        var correcto = false

        vm.agregarProducto(
            nombre = "Paracetamol",
            categoria = "Medicamentos",
            precio = "1500",
            stock = "10",
            stockMinimo = "5",
            onResultado = { texto, exito ->
                mensaje = texto
                correcto = exito
            }
        )

        despachador.scheduler.advanceUntilIdle()

        assertTrue(correcto)
        assertEquals("Producto agregado correctamente", mensaje)
    }

    @Test
    fun agregarProductoConDatosEspaciados(): Unit = kotlinx.coroutines.test.runTest {
        var correcto = false

        vm.agregarProducto(
            nombre = "  Paracetamol  ",
            categoria = "  Medicamentos  ",
            precio = "1500",
            stock = "0",
            stockMinimo = "0",
            onResultado = { _, exito -> correcto = exito }
        )

        despachador.scheduler.advanceUntilIdle()

        assertTrue(correcto)
    }

    @Test
    fun movimientoConCantidadCeroEsRechazado() {
        var correcto = true

        vm.registrarMovimiento(
            id = 1,
            cantidad = 0,
            entrada = false,
            onResultado = { _, exito -> correcto = exito }
        )

        assertFalse(correcto)
    }

    @Test
    fun movimientoConCantidadNegativaEsRechazado() {
        var correcto = true

        vm.registrarMovimiento(
            id = 1,
            cantidad = -3,
            entrada = true,
            onResultado = { _, exito -> correcto = exito }
        )

        assertFalse(correcto)
    }

    @Test
    fun sincronizacionExitosaViewModel(): Unit =
        kotlinx.coroutines.test.runTest {
            var mensaje = ""
            var correcto = false

            org.mockito.Mockito.`when`(
                repositorio.sincronizarProductoPendiente(1)
            ).thenReturn("Producto sincronizado: Paracetamol")

            vm.sincronizarProductoConServidor(1) { texto, exito ->
                mensaje = texto
                correcto = exito
            }

            despachador.scheduler.advanceUntilIdle()

            assertTrue(correcto)
            assertEquals("Producto sincronizado: Paracetamol", mensaje)
        }

    @Test
    fun productoYaVinculadoViewModel(): Unit =
        kotlinx.coroutines.test.runTest {
            var mensaje = ""
            var correcto = true

            org.mockito.Mockito.`when`(
                repositorio.sincronizarProductoPendiente(1)
            ).thenReturn("Este producto ya esta vinculado al servidor")

            vm.sincronizarProductoConServidor(1) { texto, exito ->
                mensaje = texto
                correcto = exito
            }

            despachador.scheduler.advanceUntilIdle()

            assertFalse(correcto)
            assertEquals(
                "Este producto ya esta vinculado al servidor",
                mensaje
            )
        }

    @Test
    fun eliminarProductoCorrectamenteViewModel(): Unit =
        kotlinx.coroutines.test.runTest {
            var mensaje = ""
            var correcto = false

            vm.eliminarProducto(1) { texto, exito ->
                mensaje = texto
                correcto = exito
            }

            despachador.scheduler.advanceUntilIdle()

            assertTrue(correcto)
            assertEquals("Producto eliminado correctamente", mensaje)
            org.mockito.Mockito.verify(repositorio).eliminarProducto(1)
        }

    @Test
    fun errorAlEliminarProductoViewModel(): Unit =
        kotlinx.coroutines.test.runTest {
            var mensaje = ""
            var correcto = true

            org.mockito.Mockito.doAnswer {
                throw IllegalStateException("Error simulado")
            }.`when`(repositorio).eliminarProducto(1)

            vm.eliminarProducto(1) { texto, exito ->
                mensaje = texto
                correcto = exito
            }

            despachador.scheduler.advanceUntilIdle()

            assertFalse(correcto)
            assertEquals("Error al eliminar el producto", mensaje)
        }

    @Test
    fun entradaStockExitosaViewModel(): Unit =
        kotlinx.coroutines.test.runTest {
            var mensaje = ""
            var correcto = false

            org.mockito.Mockito.`when`(
                repositorio.registrarMovimiento(1, 5, true)
            ).thenReturn(true)

            vm.registrarMovimiento(1, 5, true) { texto, exito ->
                mensaje = texto
                correcto = exito
            }

            despachador.scheduler.advanceUntilIdle()

            assertTrue(correcto)
            assertTrue(mensaje.isNotBlank())
            org.mockito.Mockito.verify(repositorio)
                .registrarMovimiento(1, 5, true)
        }

    @Test
    fun ventaStockExitosaViewModel(): Unit =
        kotlinx.coroutines.test.runTest {
            var correcto = false

            org.mockito.Mockito.`when`(
                repositorio.registrarMovimiento(1, 2, false)
            ).thenReturn(true)

            vm.registrarMovimiento(1, 2, false) { _, exito ->
                correcto = exito
            }

            despachador.scheduler.advanceUntilIdle()

            assertTrue(correcto)

            org.mockito.Mockito.verify(repositorio)
                .registrarMovimiento(1, 2, false)
        }

    @Test
    fun movimientoRechazadoViewModel(): Unit =
        kotlinx.coroutines.test.runTest {
            var correcto = true

            org.mockito.Mockito.`when`(
                repositorio.registrarMovimiento(1, 20, false)
            ).thenReturn(false)

            vm.registrarMovimiento(1, 20, false) { _, exito ->
                correcto = exito
            }

            despachador.scheduler.advanceUntilIdle()

            assertFalse(correcto)
        }

    @Test
    fun calculaTotalProductosViewModel() {
        assertEquals(0, vm.obtenerTotalProductos())
    }

    @Test
    fun calculaTotalUnidadesViewModel() {
        assertEquals(0, vm.obtenerTotalUnidades())
    }

    @Test
    fun obtieneProductosStockBajoViewModel() {
        assertTrue(vm.obtenerProductosStockBajo().isEmpty())
    }

    @Test
    fun editarRechazaNombreVacio() {
        val producto = com.example.appcappucare.model.Producto(
            1, "Paracetamol", "Medicamentos", 1000.0, 10, 5
        )

        var mensaje = ""
        var correcto = true

        vm.editarProducto(
            producto = producto,
            nombre = "",
            categoria = "Medicamentos",
            precio = "1000",
            stockMinimo = "5",
            onResultado = { texto, exito ->
                mensaje = texto
                correcto = exito
            }
        )

        assertFalse(correcto)
        assertTrue(mensaje.isNotBlank())
    }

    @Test
    fun editarRechazaCategoriaVacia() {
        val producto = com.example.appcappucare.model.Producto(
            1, "Paracetamol", "Medicamentos", 1000.0, 10, 5
        )

        var correcto = true

        vm.editarProducto(
            producto = producto,
            nombre = "Paracetamol",
            categoria = "",
            precio = "1000",
            stockMinimo = "5",
            onResultado = { _, exito -> correcto = exito }
        )

        assertFalse(correcto)
    }

    @Test
    fun editarRechazaPrecioInvalido() {
        val producto = com.example.appcappucare.model.Producto(
            1, "Paracetamol", "Medicamentos", 1000.0, 10, 5
        )

        var correcto = true

        vm.editarProducto(
            producto = producto,
            nombre = "Paracetamol",
            categoria = "Medicamentos",
            precio = "-100",
            stockMinimo = "5",
            onResultado = { _, exito -> correcto = exito }
        )

        assertFalse(correcto)
    }

    @Test
    fun editarRechazaStockMinimoInvalido() {
        val producto = com.example.appcappucare.model.Producto(
            1, "Paracetamol", "Medicamentos", 1000.0, 10, 5
        )

        var correcto = true

        vm.editarProducto(
            producto = producto,
            nombre = "Paracetamol",
            categoria = "Medicamentos",
            precio = "1000",
            stockMinimo = "-1",
            onResultado = { _, exito -> correcto = exito }
        )

        assertFalse(correcto)
    }
}
