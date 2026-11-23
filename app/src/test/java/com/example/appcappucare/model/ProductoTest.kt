package com.example.appcappucare.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductoTest {

    private fun crearProducto(
        stock: Int,
        stockMinimo: Int
    ): Producto {
        return Producto(
            id = 1,
            nombre = "Producto de prueba",
            categoria = "Medicamentos",
            precio = 1000.0,
            stock = stock,
            stockMinimo = stockMinimo
        )
    }

    @Test
    fun stockMenorAlMinimo() {
        val producto = crearProducto(4, 5)
        assertTrue(producto.stockBajo)
    }

    @Test
    fun stockIgualAlMinimo() {
        val producto = crearProducto(5, 5)
        assertTrue(producto.stockBajo)
    }

    @Test
    fun stockMayorAlMinimo() {
        val producto = crearProducto(10, 5)
        assertFalse(producto.stockBajo)
    }
}
