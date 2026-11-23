package com.example.appcappucare.utils

// Validaciones de los datos ingresados en los formularios.
object ValidadorProducto {

    fun validarNombre(nombre: String): String? {
        return if (nombre.isBlank()) {
            "El nombre es obligatorio"
        } else {
            null
        }
    }

    fun validarCategoria(categoria: String): String? {
        return if (categoria.isBlank()) {
            "La categoría es obligatoria"
        } else {
            null
        }
    }

    fun validarPrecio(precio: String): String? {
        val numero = precio.toDoubleOrNull()

        return if (numero == null || !numero.isFinite() || numero <= 0) {
            "Ingresa un precio válido mayor a cero"
        } else {
            null
        }
    }

    fun validarStock(stock: String): String? {
        val numero = stock.toIntOrNull()

        return if (numero == null || numero < 0) {
            "Ingresa un stock válido"
        } else {
            null
        }
    }

    fun validarStockMinimo(stockMinimo: String): String? {
        val numero = stockMinimo.toIntOrNull()

        return if (numero == null || numero < 0) {
            "Ingresa un stock mínimo válido"
        } else {
            null
        }
    }

    fun validarCantidadMovimiento(cantidad: Int): Boolean {
        return cantidad > 0
    }
}
