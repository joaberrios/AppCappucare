package com.example.appcappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcappucare.data.local.CappucareDatabase
import com.example.appcappucare.model.Producto
import com.example.appcappucare.repository.ProductoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductoRepository(
        CappucareDatabase.obtenerBaseDatos(application).productoDao()
    )

    val productos = repository.productos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun agregarProducto(
        nombre: String,
        categoria: String,
        precio: String,
        stock: String,
        stockMinimo: String,
        onResultado: (String, Boolean) -> Unit, fotoUri: String? = null) {
        if (nombre.isBlank()) {
            onResultado("El nombre es obligatorio", false)
            return
        }

        if (categoria.isBlank()) {
            onResultado("La categoría es obligatoria", false)
            return
        }

        val precioNumero = precio.toDoubleOrNull()
        val stockNumero = stock.toIntOrNull()
        val minimoNumero = stockMinimo.toIntOrNull()

        if (precioNumero == null || !precioNumero.isFinite() || precioNumero <= 0) {
            onResultado("Ingresa un precio válido mayor a cero", false)
            return
        }

        if (stockNumero == null || stockNumero < 0) {
            onResultado("Ingresa un stock válido", false)
            return
        }

        if (minimoNumero == null || minimoNumero < 0) {
            onResultado("Ingresa un stock mínimo válido", false)
            return
        }

        val producto = Producto(
            id = 0,
            nombre = nombre.trim(),
            categoria = categoria.trim(),
            precio = precioNumero,
            stock = stockNumero,
            stockMinimo = minimoNumero,
            fotoUri = fotoUri
        )

        viewModelScope.launch {
            try {
                repository.agregarProducto(producto)
                onResultado("Producto agregado correctamente", true)
            } catch (e: Exception) {
                onResultado("Error al guardar el producto", false)
            }
        }
    }

    fun registrarMovimiento(
        id: Int,
        cantidad: Int,
        entrada: Boolean,
        onResultado: (String, Boolean) -> Unit) {
        if (cantidad <= 0) {
            onResultado("La cantidad debe ser mayor a cero", false)
            return
        }

        viewModelScope.launch {
            try {
                val resultado = repository.registrarMovimiento(
                    id,
                    cantidad,
                    entrada
                )

                if (resultado) {
                    onResultado("Movimiento registrado correctamente", true)
                } else {
                    onResultado(
                        "No hay stock suficiente o el producto no existe",
                        false
                    )
                }
            } catch (e: Exception) {
                onResultado("Error al registrar el movimiento", false)
            }
        }
    }

    fun obtenerProductosStockBajo(): List<Producto> {
        return productos.value.filter { it.stockBajo }
    }

    fun obtenerTotalProductos(): Int {
        return productos.value.size
    }

    fun obtenerTotalUnidades(): Int {
        return productos.value.sumOf { it.stock }
    }

    fun editarProducto(
        producto: Producto,
        nombre: String,
        categoria: String,
        precio: String,
        stockMinimo: String,
        onResultado: (String, Boolean) -> Unit
    ) {
        if (nombre.isBlank()) {
            onResultado("El nombre es obligatorio", false)
            return
        }

        if (categoria.isBlank()) {
            onResultado("La categoría es obligatoria", false)
            return
        }

        val precioNumero = precio.toDoubleOrNull()
        val minimoNumero = stockMinimo.toIntOrNull()

        if (precioNumero == null || !precioNumero.isFinite() || precioNumero <= 0) {
            onResultado("Ingresa un precio válido mayor a cero", false)
            return
        }

        if (minimoNumero == null || minimoNumero < 0) {
            onResultado("Ingresa un stock mínimo válido", false)
            return
        }

        viewModelScope.launch {
            try {
                repository.actualizarProducto(
                    producto.copy(
                        nombre = nombre.trim(),
                        categoria = categoria.trim(),
                        precio = precioNumero,
                        stockMinimo = minimoNumero
                    )
                )
                onResultado("Producto actualizado correctamente", true)
            } catch (e: Exception) {
                onResultado("Error al actualizar el producto", false)
            }
        }
    }

    fun eliminarProducto(
        id: Int,
        onResultado: (String, Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.eliminarProducto(id)
                onResultado("Producto eliminado correctamente", true)
            } catch (e: Exception) {
                onResultado("Error al eliminar el producto", false)
            }
        }
    }
}
