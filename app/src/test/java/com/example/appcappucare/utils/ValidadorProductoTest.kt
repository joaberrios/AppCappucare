package com.example.appcappucare.utils

import org.junit.Assert.*
import org.junit.Test

class ValidadorProductoTest {

    @Test
    fun nombreVacioEsInvalido() {
        assertEquals(
            "El nombre es obligatorio",
            ValidadorProducto.validarNombre("   ")
        )
    }

    @Test
    fun nombreCorrectoEsValido() {
        assertNull(ValidadorProducto.validarNombre("Paracetamol"))
    }

    @Test
    fun categoriaVaciaEsInvalida() {
        assertNotNull(ValidadorProducto.validarCategoria(""))
    }

    @Test
    fun categoriaCorrectaEsValida() {
        assertNull(ValidadorProducto.validarCategoria("Medicamentos"))
    }

    @Test
    fun precioCeroEsInvalido() {
        assertNotNull(ValidadorProducto.validarPrecio("0"))
    }

    @Test
    fun precioNegativoEsInvalido() {
        assertNotNull(ValidadorProducto.validarPrecio("-100"))
    }

    @Test
    fun precioTextoEsInvalido() {
        assertNotNull(ValidadorProducto.validarPrecio("abc"))
    }

    @Test
    fun precioCorrectoEsValido() {
        assertNull(ValidadorProducto.validarPrecio("3500"))
    }

    @Test
    fun stockNegativoEsInvalido() {
        assertNotNull(ValidadorProducto.validarStock("-2"))
    }

    @Test
    fun stockCeroEsValido() {
        assertNull(ValidadorProducto.validarStock("0"))
    }

    @Test
    fun stockMinimoNegativoEsInvalido() {
        assertNotNull(ValidadorProducto.validarStockMinimo("-1"))
    }

    @Test
    fun stockMinimoCorrectoEsValido() {
        assertNull(ValidadorProducto.validarStockMinimo("5"))
    }

    @Test
    fun cantidadCeroEsInvalida() {
        assertFalse(ValidadorProducto.validarCantidadMovimiento(0))
    }

    @Test
    fun cantidadNegativaEsInvalida() {
        assertFalse(ValidadorProducto.validarCantidadMovimiento(-3))
    }

    @Test
    fun cantidadPositivaEsValida() {
        assertTrue(ValidadorProducto.validarCantidadMovimiento(4))
    }
}
